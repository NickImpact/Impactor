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

package net.impactdev.impactor.sponge.commands;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.placeholders.PlaceholderSources;
import net.impactdev.impactor.api.services.text.MessageService;
import net.impactdev.impactor.api.utilities.printing.PrettyPrinter;
import net.impactdev.impactor.sponge.SpongeImpactorPlugin;
import net.impactdev.impactor.sponge.text.placeholders.SpongePlaceholderManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.plugin.PluginContainer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class PlaceholdersCommand {

    public Command.Parameterized create() {
        Parameter.Value<ServerPlayer> player = Parameter.player()
                .key("player")
                .usage(key -> "Any current logged in player")
                .optional()
                .build();
        Parameter.Value<ResourceKey> placeholder = Parameter.resourceKey()
                .key("placeholder")
                .usage(key -> "provider:value")
                .completer((context, current) -> {
                    List<CommandCompletion> options = Lists.newArrayList();
                    String token = current.toLowerCase();

                    Impactor.getInstance().getRegistry()
                            .get(SpongePlaceholderManager.class)
                            .getAllPlatformParsers()
                            .stream()
                            .map(parser -> parser.key(RegistryTypes.PLACEHOLDER_PARSER))
                            .forEach(key -> {
                                if(key.formatted().startsWith(token)) {
                                    options.add(CommandCompletion.of(key.formatted()));
                                }
                            });

                    return options;
                })
                .build();

        Parameter.Value<String> plugin = Parameter.string()
                .key("key")
                .optional()
                .usage(key -> "The name of the plugin/key providing the placeholder")
                .completer((context, current) -> {
                    List<CommandCompletion> options = Lists.newArrayList();
                    String token = current.toLowerCase();

                    Impactor.getInstance().getRegistry()
                            .get(SpongePlaceholderManager.class)
                            .getAllPlatformParsers()
                            .stream()
                            .map(parser -> parser.key(RegistryTypes.PLACEHOLDER_PARSER))
                            .filter(key -> key.namespace().startsWith(token))
                            .forEach(key -> options.add(CommandCompletion.of(key.namespace())));

                    return options;
                })
                .build();

        return Command.builder()
                .addChild(Command.builder()
                        .addParameter(placeholder)
                        .addParameter(player)
                        .executor(context -> {
                            CommandCause cause = context.cause();
                            ServerPlayer target = context.one(player)
                                    .orElse(cause.first(ServerPlayer.class)
                                            .orElseThrow(() -> new CommandException(Component.text("Can only query against a player!"))));

                            MessageService<Component> service = Impactor.getInstance().getRegistry().get(MessageService.class);
                            ResourceKey in = context.requireOne(placeholder);
                            cause.audience().sendMessage(service.parse("&7Now attempting to parse placeholder: ").append(Component.text(in.formatted()).color(NamedTextColor.GREEN)));
                            Component result = service.parse("{{" + in.formatted() + "}}", PlaceholderSources.builder().append(ServerPlayer.class, () -> target).build());
                            if(result.equals(Component.empty())) {
                                return CommandResult.error(service.parse("Placeholder returned no value..."));
                            }

                            cause.audience().sendMessage(service.parse("&7Result: ").append(result));
                            return CommandResult.success();
                        })
                        .build(), "query"
                )
                .addChild(Command.builder()
                        .addParameter(plugin)
                        .executor(context -> {
                            Optional<String> namespace = context.one(plugin);
                            Multimap<String, ResourceKey> keys = Impactor.getInstance().getRegistry()
                                    .get(SpongePlaceholderManager.class)
                                    .getAllPlatformParsers()
                                    .stream()
                                    .map(parser -> parser.key(RegistryTypes.PLACEHOLDER_PARSER))
                                    .filter(key -> namespace.map(n -> key.namespace().equals(n)).orElse(true))
                                    .collect(this.toMultimap(
                                            key -> this.fromResourceKey(key).flatMap(pc -> pc.metadata().name()).orElse("Custom"),
                                            key -> key,
                                            ArrayListMultimap::create
                                    ));

                            Path target = SpongeImpactorPlugin.getInstance().getConfigDir().resolve("placeholders").resolve("placeholders.out");
                            try {
                                if(!target.toFile().exists()) {
                                    Files.createDirectories(target.getParent());
                                    Files.createFile(target);
                                }

                                try (PrintStream stream = new PrintStream(target.toFile())){
                                    PrettyPrinter printer = new PrettyPrinter(80);
                                    printer.title("Registered Placeholders");

                                    keys.keySet().stream().sorted((s1, s2) -> {
                                        if (s1.equals("Custom")) {
                                            return 1;
                                        }
                                        else if (s2.equals("Custom")) {
                                            return -1;
                                        }
                                        else {
                                            return s1.compareTo(s2);
                                        }
                                    }).forEach(key -> {
                                        printer.add(key);
                                        for (ResourceKey parser : keys.get(key)) {
                                            printer.add(parser.formatted(), 2);
                                        }
                                    });
                                    printer.print(stream);
                                }
                                context.cause().audience().sendMessage(Component.text("Successfully wrote placeholder information to "
                                        + SpongeImpactorPlugin.getInstance().getConfigDir().getParent().getParent().relativize(target)));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            return CommandResult.success();
                        })
                        .build(), "list"
                )
                .build();
    }

    private Optional<PluginContainer> fromResourceKey(ResourceKey input) {
        return Sponge.pluginManager().plugin(input.namespace());
    }

    private <T, K, U, M extends Multimap<K, U>>
        Collector<T, ?, M> toMultimap(Function<? super T, ? extends K> keyMapper,
                                      Function<? super T, ? extends U> valueMapper,
                                      Supplier<M> mapFactory) {

        BiConsumer<M, T> accumulator = (map, element) -> map.put(keyMapper.apply(element), valueMapper.apply(element));
        BinaryOperator<M> combiner = (left, right) -> { left.putAll(right); return left; };
        return Collector.of(mapFactory, accumulator, combiner);
    }
}
