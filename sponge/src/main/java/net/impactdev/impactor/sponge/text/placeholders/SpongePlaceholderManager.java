package net.impactdev.impactor.sponge.text.placeholders;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.placeholders.PlaceholderManager;
import net.impactdev.impactor.sponge.SpongeImpactorPlugin;
import net.impactdev.impactor.sponge.text.placeholders.provided.Memory;
import net.impactdev.impactor.sponge.text.placeholders.provided.tick.MeanTickTime;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.placeholder.PlaceholderContext;
import org.spongepowered.api.text.placeholder.PlaceholderParser;

import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

public class SpongePlaceholderManager implements PlaceholderManager<PlaceholderParser> {

    private static final Pattern PARSER_MATCHER = Pattern.compile("(\\w+):(\\w+)");

    private final List<PlaceholderParser> parsers = Lists.newArrayList();

    public SpongePlaceholderManager() {
        this.populate();
    }

    public void register(PlaceholderParser parser) {
        this.parsers.add(parser);
    }

    public ImmutableList<PlaceholderParser> getAllInternalParsers() {
        return ImmutableList.copyOf(this.parsers);
    }

    public ImmutableList<PlaceholderParser> getAllPlatformParsers() {
        List<PlaceholderParser> parsers = Lists.newArrayList(Sponge.getRegistry().getAllOf(PlaceholderParser.class));
        return ImmutableList.copyOf(parsers);
    }

    public void populate() {
        PluginContainer container = SpongeImpactorPlugin.getInstance().getPluginContainer();
        SpongeImpactorPlugin plugin = SpongeImpactorPlugin.getInstance();

        this.register(this.create("tps", "Server Ticks per Second", container, context -> formatTps(plugin.getWatcher().tps5Sec())));
        this.register(this.create("tps_10sec", "Server Ticks per Second", container, context -> formatTps(plugin.getWatcher().tps10Sec())));
        this.register(this.create("tps_1min", "Server Ticks per Second", container, context -> formatTps(plugin.getWatcher().tps1Min())));
        this.register(this.create("tps_5min", "Server Ticks per Second", container, context -> formatTps(plugin.getWatcher().tps5Min())));
        this.register(this.create("tps_15min", "Server Ticks per Second", container, context -> formatTps(plugin.getWatcher().tps15Min())));
        this.register(this.create("mspt", "Average Milliseconds per Tick", container, context -> Impactor.getInstance().getRegistry().get(MeanTickTime.class).getFormatted()));
        this.register(this.create("memory_used", "Server Memory In Use", container, context -> Text.of(Memory.getCurrent())));
        this.register(this.create("memory_allocated", "Server Memory Allocated", container, context -> Text.of(Memory.getAllocated())));
        this.register(this.create("player_count", "Current Online Player Count", container, context -> Text.of(Sponge.getServer().getOnlinePlayers().size())));
        this.register(this.create("ping", "Player's Ping", container, context -> this.filterSource(Player.class, context.getAssociatedObject())
                .map(player -> Text.of(player.getConnection().getLatency()))
                .orElse(Text.EMPTY)
        ));

    }

    private PlaceholderParser create(String id, String name, PluginContainer plugin, Function<PlaceholderContext, Text> parser) {
        return PlaceholderParser.builder()
                .id(id)
                .name(name)
                .plugin(plugin)
                .parser(parser)
                .build();
    }

    private static Text formatTps(double tps) {
        TextColor color;
        if (tps > 18.0) {
            color = TextColors.GREEN;
        } else if (tps > 16.0) {
            color = TextColors.YELLOW;
        } else {
            color = TextColors.RED;
        }

        return Text.of(color, (tps > 20.0 ? "*" : "") + Math.min(Math.round(tps * 100.0) / 100.0, 20.0));
    }
}
