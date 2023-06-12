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

package net.impactdev.impactor.fabric.integrations;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.events.ImpactorEvent;
import net.impactdev.impactor.api.logging.PluginLogger;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.platform.sources.PlatformSource;
import net.impactdev.impactor.api.text.events.RegisterPlaceholdersEvent;
import net.impactdev.impactor.api.text.placeholders.PlaceholderArguments;
import net.impactdev.impactor.core.integrations.Dependencies;
import net.impactdev.impactor.core.integrations.Dependency;
import net.impactdev.impactor.core.integrations.Integration;
import net.impactdev.impactor.fabric.platform.FabricPlatform;
import net.impactdev.impactor.minecraft.api.text.AdventureTranslator;
import net.kyori.adventure.key.Key;
import net.kyori.event.EventBus;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

@Dependencies(@Dependency("placeholder-api"))
public final class PlaceholderAPIIntegration implements Integration {

    @Override
    public String name() {
        return "Placeholder API";
    }

    @Override
    public void subscribe(PluginLogger logger, EventBus<ImpactorEvent> bus) {
        logger.info("Integrating with PlaceholderAPI...");

        bus.subscribe(RegisterPlaceholdersEvent.class, event -> {
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
                    if (player != null) {
                        ctx = PlaceholderContext.of(player);
                    } else {
                        ctx = PlaceholderContext.of(server);
                    }

                    return AdventureTranslator.fromNative(handler.onPlaceholderRequest(ctx,
                            arguments.popOrDefault()).text());
                });
            });
        });
    }


}
