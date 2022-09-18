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

package net.impactdev.impactor.fabric.platform.players;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import net.impactdev.impactor.adventure.AdventureTranslator;
import net.impactdev.impactor.api.items.ImpactorItemStack;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.platform.players.transactions.ItemReceiptTransaction;
import net.impactdev.impactor.fabric.FabricImpactorPlugin;
import net.kyori.adventure.text.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;
import java.util.UUID;

public class FabricPlatformPlayer implements PlatformPlayer {

    private final UUID uuid;

    public FabricPlatformPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public UUID uuid() {
        return this.uuid;
    }

    @Override
    public Component name() {
        return this.toFabric().map(player -> AdventureTranslator.fromNative(player.getDisplayName())).orElse(Component.empty());
    }

    @Override
    public ItemReceiptTransaction offer(ImpactorItemStack stack) {
        return null;
    }

    public Optional<ServerPlayer> toFabric() {
        return ((FabricImpactorPlugin) FabricImpactorPlugin.instance()).server().map(server -> server.getPlayerList().getPlayer(this.uuid));
    }

    public static class FabricPlayerFactory implements Factory {

        private final LoadingCache<UUID, PlatformPlayer> cache = Caffeine.newBuilder()
                .build(FabricPlatformPlayer::new);

        @Override
        public PlatformPlayer create(UUID uuid) {
            return this.cache.get(uuid);
        }
    }

}
