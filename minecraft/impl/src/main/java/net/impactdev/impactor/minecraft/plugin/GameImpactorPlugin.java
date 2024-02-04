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

package net.impactdev.impactor.minecraft.plugin;

import com.mojang.brigadier.arguments.ArgumentType;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.commands.CommandSource;
import net.impactdev.impactor.api.commands.events.RegisterBrigadierMappingsEvent;
import net.impactdev.impactor.core.commands.parsers.CurrencyParser;
import net.impactdev.impactor.core.modules.ModuleInitializer;
import net.impactdev.impactor.core.plugin.BaseImpactorPlugin;
import net.impactdev.impactor.core.plugin.ImpactorBootstrapper;
import net.impactdev.impactor.minecraft.items.ItemsModule;
import net.impactdev.impactor.minecraft.ui.UIModule;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import org.incendo.cloud.brigadier.argument.BrigadierMapping;
import org.incendo.cloud.brigadier.argument.BrigadierMappings;
import org.incendo.cloud.parser.ArgumentParser;

import java.util.function.Function;

public abstract class GameImpactorPlugin extends BaseImpactorPlugin {

    public GameImpactorPlugin(ImpactorBootstrapper bootstrapper) {
        super(bootstrapper);
    }

    @Override
    protected ModuleInitializer registerModules() {
        return super.registerModules()
                .with(ItemsModule.class)
                .with(UIModule.class);
    }

    @Override
    public void construct() {
        super.construct();
        Impactor.instance().events().subscribe(RegisterBrigadierMappingsEvent.class, event -> {
            BrigadierMappings<CommandSource, CommandSourceStack> mappings = (BrigadierMappings<CommandSource, CommandSourceStack>) event.mappings();
            BrigadierMapping<?, CurrencyParser, CommandSourceStack> currency = this.createMapping(
                    parser -> ResourceLocationArgument.id()
            );

            mappings.registerMapping(CurrencyParser.class, currency);
        });
    }

    private <T, K extends ArgumentParser<CommandSource, T>> BrigadierMapping<?, K, CommandSourceStack> createMapping(
            Function<K, ? extends ArgumentType<?>> adapter
    ) {
        return BrigadierMapping.<CommandSource, K, CommandSourceStack>builder()
                .cloudSuggestions()
                .to(adapter)
                .build();
    }
}
