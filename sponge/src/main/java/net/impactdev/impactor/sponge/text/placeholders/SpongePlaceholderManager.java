package net.impactdev.impactor.sponge.text.placeholders;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.placeholders.PlaceholderManager;
import net.impactdev.impactor.sponge.SpongeImpactorPlugin;
import net.impactdev.impactor.sponge.text.placeholders.provided.Memory;
import net.impactdev.impactor.sponge.text.processors.gradients.NumberBasedGradientProcessor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.spongepowered.api.Sponge;

import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.placeholder.PlaceholderContext;
import org.spongepowered.api.placeholder.PlaceholderParser;
import org.spongepowered.api.registry.RegistryTypes;

import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SpongePlaceholderManager implements PlaceholderManager<PlaceholderMetadata, PlaceholderParser> {

    private static final Pattern PARSER_MATCHER = Pattern.compile("(\\w+):(\\w+)");

    private final List<PlaceholderMetadata> parsers = Lists.newArrayList();

    public SpongePlaceholderManager() {
        this.populate();
    }

    public void register(PlaceholderMetadata parser) {
        this.parsers.add(parser);
    }

    public ImmutableList<PlaceholderMetadata> getAllInternalParsers() {
        return ImmutableList.copyOf(this.parsers);
    }

    public ImmutableList<PlaceholderParser> getAllPlatformParsers() {
        List<PlaceholderParser> parsers = RegistryTypes.PLACEHOLDER_PARSER.get().stream().collect(Collectors.toList());
        return ImmutableList.copyOf(parsers);
    }

    public void populate() {
        this.register(this.create("tps", context -> formatTps(Sponge.server().ticksPerSecond())));
        this.register(this.create("mspt", context -> formatMilliseconds(Sponge.server().averageTickTime())));
        this.register(this.create("memory_used", context -> Component.text(Memory.getCurrent())));
        this.register(this.create("memory_allocated", context -> Component.text(Memory.getAllocated())));
        this.register(this.create("player_count", context -> Component.text(Sponge.server().onlinePlayers().size())));
        this.register(this.create("ping", context -> this.filterSource(ServerPlayer.class, context.associatedObject())
                .map(player -> PING_PROCESSOR.process(player.connection().latency()))
                .orElse(Component.empty())
        ));
        AtomicInteger step = new AtomicInteger();
        this.register(this.create("test", context -> {
            if(step.get() > 200) {
                step.set(0);
            }
            return PING_PROCESSOR.process(step.getAndIncrement());
        }));
    }

    private PlaceholderMetadata create(String id, Function<PlaceholderContext, Component> parser) {
        return new PlaceholderMetadata(id, PlaceholderParser.builder().parser(parser).build());
    }

    private static final TextColor min = NamedTextColor.RED;
    private static final TextColor max = NamedTextColor.GREEN;

    private static final NumberBasedGradientProcessor<Double> TPS_PROCESSOR = NumberBasedGradientProcessor.builder()
            .type(new TypeToken<Double>(){})
            .min(10)
            .max(20)
            .translator(x -> Component.text(Math.min(Math.round(x * 100.0) / 100.0, 20.0)))
            .factor(x -> x.floatValue() * 0.1f - 1)
            .colors(min, max)
            .build();

    private static final NumberBasedGradientProcessor<Double> MSPT_PROCESSOR = NumberBasedGradientProcessor.builder()
            .type(new TypeToken<Double>(){})
            .min(0)
            .max(50)
            .translator(Component::text)
            .factor(x -> x.floatValue() * 0.1f / 5)
            .colors(max, min)
            .build();

    private static final NumberBasedGradientProcessor<Integer> PING_PROCESSOR = NumberBasedGradientProcessor.<Integer>builder()
            .type(new TypeToken<Integer>(){})
            .min(0)
            .max(200)
            .translator(Component::text)
            .factor(x -> x.floatValue() * 0.1f / 20)
            .colors(max, min)
            .build();

    private static final NumberBasedGradientProcessor<Integer> IV_PROCESSOR = NumberBasedGradientProcessor.<Integer>builder()
            .type(new TypeToken<Integer>(){})
            .min(0)
            .max(31)
            .translator(Component::text)
            .factor(x -> x.floatValue() * 0.1f / 31 * 10)
            .colors(max, min)
            .build();

    private static Component formatTps(double tps) {
        Component isHigh = tps > 20.0 ? Component.text("*") : Component.empty();
        Component result = TPS_PROCESSOR.process(tps);

        return isHigh.append(result).mergeStyle(result);
    }

    private static Component formatMilliseconds(double milliseconds) {
        return MSPT_PROCESSOR.process(milliseconds).append(Component.text("ms"));
    }
}
