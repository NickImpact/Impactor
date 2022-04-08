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

package net.impactdev.impactor.forge.api;

import net.impactdev.impactor.api.event.EventBus;
import net.impactdev.impactor.api.platform.Platform;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.platform.players.PlatformPlayerManager;
import net.impactdev.impactor.api.scheduler.SchedulerAdapter;
import net.impactdev.impactor.api.scoreboard.events.PlatformBus;
import net.impactdev.impactor.common.api.ImpactorAPIProvider;
import net.impactdev.impactor.common.players.CommonPlatformPlayer;
import net.impactdev.impactor.forge.adventure.RelocationTranslator;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.Optional;

public class ForgeImpactorAPIProvider extends ImpactorAPIProvider {

    @Override
    public Platform<?> getPlatform() {
        return new Platform<ServerPlayerEntity>() {
            @Override
            public PlatformPlayerManager<ServerPlayerEntity> playerManager() {
                return new PlatformPlayerManager<ServerPlayerEntity>() {
                    @Override
                    public Class<ServerPlayerEntity> platformType() {
                        return ServerPlayerEntity.class;
                    }

                    @Override
                    public boolean verify(Object instance) {
                        return this.platformType().isInstance(instance);
                    }

                    @Override
                    public PlatformPlayer from(Object instance) {
                        if(this.verify(instance)) {
                            ServerPlayerEntity player = this.platformType().cast(instance);
                            return new CommonPlatformPlayer(player.getUUID(), RelocationTranslator.adventure(player.getName()));
                        }

                        throw new IllegalArgumentException("Type is not a acceptable player typing");
                    }

                    @Override
                    public Optional<ServerPlayerEntity> translate(PlatformPlayer player) {
                        return Optional.ofNullable(ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(player.uuid()));
                    }
                };
            }

            @Override
            public <L> PlatformBus<L> createPlatformBus() {
                return null;
            }
        };
    }

    @Override
    public EventBus getEventBus() {
        return this.getRegistry().get(EventBus.class);
    }

    @Override
    public SchedulerAdapter getScheduler() {
        return null;
    }
}
