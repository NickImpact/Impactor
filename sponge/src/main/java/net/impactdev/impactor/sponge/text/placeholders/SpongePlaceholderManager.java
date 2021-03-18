package net.impactdev.impactor.sponge.text.placeholders;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.placeholders.PlaceholderManager;
import net.impactdev.impactor.sponge.SpongeImpactorPlugin;
import net.impactdev.impactor.sponge.text.placeholders.provided.Memory;
import net.impactdev.impactor.sponge.text.placeholders.provided.tick.MeanTickTime;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.placeholder.PlaceholderContext;
import org.spongepowered.api.placeholder.PlaceholderParser;
import org.spongepowered.api.registry.RegistryKey;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.plugin.PluginContainer;

import java.util.List;
import java.util.Objects;
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
        SpongeImpactorPlugin plugin = SpongeImpactorPlugin.getInstance();

        this.register(this.create("tps", context -> formatTps(plugin.getWatcher().tps5Sec())));
        this.register(this.create("tps_10sec", context -> formatTps(plugin.getWatcher().tps10Sec())));
        this.register(this.create("tps_1min", context -> formatTps(plugin.getWatcher().tps1Min())));
        this.register(this.create("tps_5min", context -> formatTps(plugin.getWatcher().tps5Min())));
        this.register(this.create("tps_15min", context -> formatTps(plugin.getWatcher().tps15Min())));
        this.register(this.create("mspt", context -> Impactor.getInstance().getRegistry().get(MeanTickTime.class).getFormatted()));
        this.register(this.create("memory_used", context -> Component.text(Memory.getCurrent())));
        this.register(this.create("memory_allocated", context -> Component.text(Memory.getAllocated())));
        this.register(this.create("player_count", context -> Component.text(Sponge.getServer().getOnlinePlayers().size())));
        this.register(this.create("ping", context -> this.filterSource(ServerPlayer.class, context.getAssociatedObject())
                .map(player -> Component.text(player.getConnection().getLatency()))
                .orElse(Component.empty())
        ));

    }

    private PlaceholderMetadata create(String id, Function<PlaceholderContext, Component> parser) {
        return new PlaceholderMetadata(id, PlaceholderParser.builder().parser(parser).build());
    }

    private static TextComponent formatTps(double tps) {
        TextColor color;
        if (tps > 18.0) {
            color = NamedTextColor.GREEN;
        } else if (tps > 16.0) {
            color = NamedTextColor.YELLOW;
        } else {
            color = NamedTextColor.RED;
        }

        return Component.text((tps > 20.0 ? "*" : "") + Math.min(Math.round(tps * 100.0) / 100.0, 20.0)).color(color);
    }
}
