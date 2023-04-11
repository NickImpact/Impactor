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
import cloud.commandframework.meta.SimpleCommandMeta;
import com.google.common.collect.Lists;
import io.leangen.geantyref.TypeToken;
import net.impactdev.impactor.api.commands.CommandSource;
import net.impactdev.impactor.api.commands.ImpactorCommandManager;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.platform.sources.PlatformSource;
import net.impactdev.impactor.api.utility.ExceptionPrinter;
import net.impactdev.impactor.core.commands.parsers.CurrencyParser;
import net.impactdev.impactor.core.commands.economy.EconomyCommands;
import net.impactdev.impactor.core.commands.parsers.PlatformSourceParser;
import net.impactdev.impactor.core.plugin.BaseImpactorPlugin;

import java.util.List;

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

        List<Class<?>> containers = Lists.newArrayList(
                EconomyCommands.class
        );

        containers.forEach(container -> {
            try {
                final Object instance = container.getConstructor().newInstance();
                parser.parse(instance);
            } catch (Exception e) {
                ExceptionPrinter.print(BaseImpactorPlugin.instance().logger(), e);
            }
        });
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
    }

    private AnnotationParser<CommandSource> createParser() {
        AnnotationParser<CommandSource> parser = new AnnotationParser<>(
                this.manager.delegate(),
                CommandSource.class,
                parameters -> SimpleCommandMeta.empty()
        );

        return parser;
    }

}
