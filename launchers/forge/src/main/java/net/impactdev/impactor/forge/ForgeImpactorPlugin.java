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

package net.impactdev.impactor.forge;

import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import io.leangen.geantyref.TypeToken;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.commands.CommandSource;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import net.impactdev.impactor.api.scheduler.AbstractJavaScheduler;
import net.impactdev.impactor.core.commands.ImpactorCommandRegistry;
import net.impactdev.impactor.core.commands.parsers.CurrencyParser;
import net.impactdev.impactor.core.modules.ImpactorModule;
import net.impactdev.impactor.core.plugin.ImpactorBootstrapper;
import net.impactdev.impactor.forge.commands.BrigadierMapper;
import net.impactdev.impactor.forge.commands.ForgeCommandManager;
import net.impactdev.impactor.forge.commands.ForgeCommandModule;
import net.impactdev.impactor.forge.platform.ForgePlatformModule;
import net.impactdev.impactor.forge.scheduler.ForgeSchedulerModule;
import net.impactdev.impactor.forge.ui.ForgeUIModule;
import net.impactdev.impactor.minecraft.plugin.GameImpactorPlugin;
import net.kyori.adventure.key.Key;

import java.util.Objects;
import java.util.Set;

public class ForgeImpactorPlugin extends GameImpactorPlugin implements ImpactorPlugin {

    public ForgeImpactorPlugin(ImpactorBootstrapper bootstrapper) {
        super(bootstrapper);
    }

    @Override
    public void construct() {
        super.construct();
    }

    @Override
    protected void registerCommandMappings(ImpactorCommandRegistry registry) {
        ForgeCommandManager manager = (ForgeCommandManager) registry.manager();
        BrigadierMapper mapper = manager.mapper();
        mapper.map(TypeToken.get(CurrencyParser.class), Key.key("minecraft:resource_location"), true);

        MinecraftHelp<CommandSource> helper = new MinecraftHelp<>(
                "/impactor help",
                CommandSource::source,
                registry.manager().delegate()
        );

        registry.manager().delegate().command(registry.manager()
                .delegate()
                .commandBuilder("impactor")
                .literal("help")
                .argument(StringArgument.optional("query", StringArgument.StringMode.GREEDY))
                .handler(context -> {
                    helper.queryCommands(Objects.requireNonNull(context.getOrDefault("query", "")), context.getSender());
                })
        );
    }

    @Override
    protected Set<Class<? extends ImpactorModule>> modules() {
        Set<Class<? extends ImpactorModule>> parent = super.modules();
        parent.add(ForgeSchedulerModule.class);
        parent.add(ForgeUIModule.class);
        parent.add(ForgePlatformModule.class);
        parent.add(ForgeCommandModule.class);

        return parent;
    }

    @Override
    public void shutdown() {
        AbstractJavaScheduler scheduler = (AbstractJavaScheduler) Impactor.instance().scheduler();
        scheduler.shutdownExecutor();
    }

}
