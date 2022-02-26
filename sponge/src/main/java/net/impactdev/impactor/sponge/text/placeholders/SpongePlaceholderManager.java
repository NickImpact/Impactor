/*
 * This file is part of Impactor, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018-2022 NickImpact
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package net.impactdev.impactor.sponge.text.placeholders;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;
import net.impactdev.impactor.api.placeholders.PlaceholderManager;
import net.impactdev.impactor.api.placeholders.PlaceholderSources;
import net.impactdev.impactor.sponge.text.placeholders.provided.Memory;
import net.impactdev.impactor.sponge.text.processors.gradients.NumberBasedGradientProcessor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;

import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.placeholder.PlaceholderContext;
import org.spongepowered.api.placeholder.PlaceholderParser;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.util.MinecraftDayTime;
import org.spongepowered.math.vector.Vector3i;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
        this.register(this.create("memory_total", context -> Component.text(Memory.getMax())));
        this.register(this.create("player_count", context -> Component.text(Sponge.server().onlinePlayers().size())));
        this.register(this.create("ping", context -> context.associatedObject()
                .filter(source -> source instanceof PlaceholderSources)
                .map(source -> (PlaceholderSources) source)
                .flatMap(sources -> sources.getSource(ServerPlayer.class))
                .map(player -> PING_PROCESSOR.process(player.connection().latency()))
                .orElse(Component.empty())
        ));
        this.register(this.create("coordinates", context -> context.associatedObject()
                .filter(source -> source instanceof PlaceholderSources)
                .map(source -> (PlaceholderSources) source)
                .flatMap(sources -> sources.getSource(ServerPlayer.class))
                .map(player -> {
                    Vector3i position = player.blockPosition();
                    return Component.text(position.x())
                            .append(Component.text(", "))
                            .append(Component.text(position.y()))
                            .append(Component.text(", "))
                            .append(Component.text(position.z()));
                })
                .orElse(Component.text("?, ?, ?"))
        ));
        this.register(this.create("sponge_api", context -> Component.text(Sponge.platform().container(Platform.Component.API).metadata().version().toString())));
        this.register(this.create("world_day", context -> context.associatedObject()
                .filter(source -> source instanceof PlaceholderSources)
                .map(source -> (PlaceholderSources) source)
                .flatMap(sources -> sources.getSource(ServerPlayer.class))
                .map(ServerPlayer::world)
                .map(world -> {
                    MinecraftDayTime time = world.properties().dayTime();
                    return Component.text(time.day());
                })
                .orElse(Component.text("Failed to parse day..."))
        ));
        this.register(this.create("world_time", context -> context.associatedObject()
                .filter(source -> source instanceof PlaceholderSources)
                .map(source -> (PlaceholderSources) source)
                .flatMap(sources -> sources.getSource(ServerPlayer.class))
                .map(ServerPlayer::world)
                .map(world -> {
                    MinecraftDayTime time = world.properties().dayTime();
                    boolean military = context.argumentString().stream().anyMatch(arg -> arg.equalsIgnoreCase("military"));

                    int hour = time.hour() % (military ? 24 : 12);
                    int minute = time.minute();

                    String marker = time.hour() < 12 ? "AM" : "PM";

                    DecimalFormat df = new DecimalFormat("00");
                    return Component.text((hour == 0 ? 12 : hour) + ":" + df.format(minute) + (military ? "" : " " + marker));
                })
                .orElse(Component.text("Failed to parse time..."))
        ));
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
            .translator(value -> {
                BigDecimal decimal = BigDecimal.valueOf(value).setScale(3, RoundingMode.HALF_UP);
                return Component.text(decimal.doubleValue());
            })
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

    private static Component formatTps(double tps) {
        Component isHigh = tps > 20.0 ? Component.text("*") : Component.empty();
        Component result = TPS_PROCESSOR.process(tps);

        return isHigh.append(result).mergeStyle(result);
    }

    private static Component formatMilliseconds(double milliseconds) {
        return MSPT_PROCESSOR.process(milliseconds).append(Component.text("ms"));
    }

    private static Map<String, String> decodeArgs(String in) {
        String[] arguments = in.split(";");
        Map<String, String> map = Maps.newHashMap();
        for(String args : arguments) {
            String[] split = args.split("=");
            map.put(split[0], split[1]);
        }
        return map;
    }

    private final Map<UUID, RainbowManager> gradientManagers = Maps.newHashMap();

    private static class RainbowManager {

        private int step;

        public RainbowManager(int start) {
            this.step = start;
        }

        public int step() {
            return this.step++;
        }

    }


}
