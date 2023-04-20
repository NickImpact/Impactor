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

package net.impactdev.impactor.fabric;

import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import io.leangen.geantyref.TypeToken;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.commands.CommandSource;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.platform.sources.PlatformSource;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import net.impactdev.impactor.api.scheduler.AbstractJavaScheduler;
import net.impactdev.impactor.api.text.events.RegisterPlaceholdersEvent;
import net.impactdev.impactor.api.text.placeholders.PlaceholderArguments;
import net.impactdev.impactor.core.commands.ImpactorCommandRegistry;
import net.impactdev.impactor.core.commands.parsers.CurrencyParser;
import net.impactdev.impactor.core.modules.ImpactorModule;
import net.impactdev.impactor.core.plugin.ImpactorBootstrapper;
import net.impactdev.impactor.core.utility.future.Futures;
import net.impactdev.impactor.fabric.commands.BrigadierMapper;
import net.impactdev.impactor.fabric.commands.FabricCommandManager;
import net.impactdev.impactor.fabric.commands.FabricCommandModule;
import net.impactdev.impactor.fabric.platform.FabricPlatform;
import net.impactdev.impactor.fabric.platform.FabricPlatformModule;
import net.impactdev.impactor.fabric.scheduler.FabricSchedulerModule;
import net.impactdev.impactor.fabric.ui.FabricUIModule;
import net.impactdev.impactor.minecraft.plugin.GameImpactorPlugin;
import net.impactdev.impactor.minecraft.text.AdventureTranslator;
import net.kyori.adventure.key.Key;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class FabricImpactorPlugin extends GameImpactorPlugin implements ImpactorPlugin {

    public FabricImpactorPlugin(ImpactorBootstrapper bootstrapper) {
        super(bootstrapper);
    }

    @Override
    public void construct() {
        super.construct();
        Impactor.instance().events().subscribe(RegisterPlaceholdersEvent.class, event -> {
            Placeholders.getPlaceholders().forEach((location, handler) -> {
                Key key = Key.key(location.toString());
                event.register(key, (viewer, context) -> {
                    FabricPlatform platform = (FabricPlatform) Impactor.instance().platform();
                    MinecraftServer server = platform.server();
                    ServerPlayer player = context.request(PlatformPlayer.class)
                            .map(PlatformSource::uuid)
                            .or(() -> Optional.ofNullable(viewer).map(PlatformSource::uuid))
                            .map(p -> server.getPlayerList().getPlayer(p))
                            .orElse(null);

                    PlaceholderArguments arguments = context.require(PlaceholderArguments.class);

                    PlaceholderContext ctx;
                    if(player != null) {
                        ctx = PlaceholderContext.of(player);
                    } else {
                        ctx = PlaceholderContext.of(server);
                    }

                    return AdventureTranslator.fromNative(handler.onPlaceholderRequest(ctx, arguments.popOrDefault()).text());
                });
            });
        });
    }

    @Override
    protected void registerCommandMappings(ImpactorCommandRegistry registry) {
        FabricCommandManager manager = (FabricCommandManager) registry.manager();
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
        parent.add(FabricSchedulerModule.class);
        parent.add(FabricUIModule.class);
        parent.add(FabricPlatformModule.class);
        parent.add(FabricCommandModule.class);

        return parent;
    }

    @Override
    public void shutdown() {
        this.logger().info("Shutting down Impactor scheduler");
        AbstractJavaScheduler scheduler = (AbstractJavaScheduler) Impactor.instance().scheduler();
        scheduler.shutdownExecutor();
        scheduler.shutdownScheduler();

        Futures.shutdown();

        this.logger().info("Scheduler shutdown successfully!");
    }
}
