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

package net.impactdev.impactor.core.commands;

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.annotations.processing.CommandContainer;
import cloud.commandframework.arguments.parser.StandardParameters;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.meta.SimpleCommandMeta;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import io.leangen.geantyref.TypeToken;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.commands.CommandSource;
import net.impactdev.impactor.api.commands.ImpactorCommandManager;
import net.impactdev.impactor.api.commands.brigadier.BrigadierMapper;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.platform.sources.PlatformSource;
import net.impactdev.impactor.core.commands.events.RegisterCommandsEvent;
import net.impactdev.impactor.core.commands.parsers.ActivePaginationParser;
import net.impactdev.impactor.core.commands.parsers.CurrencyParser;
import net.impactdev.impactor.core.commands.parsers.LocaleParser;
import net.impactdev.impactor.core.commands.parsers.PlatformSourceParser;
import net.impactdev.impactor.core.plugin.BaseImpactorPlugin;
import net.impactdev.impactor.core.text.pagination.ActivePagination;
import net.impactdev.impactor.core.utility.events.EventPublisher;
import net.kyori.adventure.key.Key;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public final class ImpactorCommandRegistry {

    private final ImpactorCommandManager manager = ImpactorCommandManager.create(
            BaseImpactorPlugin.instance().metadata(),
            BaseImpactorPlugin.instance().logger()
    );

    public ImpactorCommandManager manager() {
        return this.manager;
    }

    public void registerAllCommands() {
        AnnotationParser<CommandSource> parser = this.createParser();
        EventPublisher.post(new RegisterCommandsEvent(parser));

        MinecraftHelp<CommandSource> helper = new MinecraftHelp<>(
                "/impactor help",
                CommandSource::source,
                this.manager().delegate()
        );

        this.manager().delegate().command(this.manager()
                .delegate()
                .commandBuilder("impactor")
                .literal("help")
                .argument(StringArgument.optional("query", StringArgument.StringMode.GREEDY))
                .handler(context -> {
                    helper.queryCommands(Objects.requireNonNull(context.getOrDefault("query", "")), context.getSender());
                })
        );
    }

    public void registerArgumentParsers() {
        this.manager.delegate().parserRegistry().registerParserSupplier(
                TypeToken.get(Currency.class),
                options -> new CurrencyParser()
        );

        this.manager.delegate().parserRegistry().registerParserSupplier(
                TypeToken.get(PlatformSource.class),
                options -> new PlatformSourceParser()
        );

        this.manager.delegate().parserRegistry().registerParserSupplier(
                TypeToken.get(Locale.class),
                options -> new LocaleParser()
        );

        this.manager.delegate().parserRegistry().registerParserSupplier(
                TypeToken.get(ActivePagination.class),
                options -> new ActivePaginationParser()
        );

        BrigadierMapper mapper = this.manager().mapper();
        mapper.map(Key.key("minecraft", "resource_location"), TypeToken.get(CurrencyParser.class));
    }

    private AnnotationParser<CommandSource> createParser() {
        return new AnnotationParser<>(
                this.manager.delegate(),
                CommandSource.class,
                parameters -> {
                    SimpleCommandMeta.Builder meta = CommandMeta.simple();

                    if(parameters.has(StandardParameters.DESCRIPTION)) {
                        meta.with(CommandMeta.DESCRIPTION, parameters.get(StandardParameters.DESCRIPTION, ""));
                    }

                    if(parameters.has(StandardParameters.HIDDEN)) {
                        meta.with(CommandMeta.HIDDEN, parameters.get(StandardParameters.HIDDEN, false));
                    }

                    return meta.build();
                }
        );
    }

    private List<Class<?>> collectCommandContainers(Impactor service) {
        ClassGraph graph = new ClassGraph()
                .acceptPackages("net.impactdev.impactor")
                .enableClassInfo()
                .enableAnnotationInfo()
                .overrideClassLoaders(this.getClass().getClassLoader());

        try (ScanResult scan = graph.scan()) {
            ClassInfoList list = scan.getClassesWithAnnotation(CommandContainer.class);
            return list.stream()
                    .map(ClassInfo::loadClass)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

}
