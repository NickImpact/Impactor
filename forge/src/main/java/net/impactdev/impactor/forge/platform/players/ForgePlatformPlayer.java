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

package net.impactdev.impactor.forge.platform.players;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.forge.ForgeImpactorPlugin;
import net.impactdev.impactor.game.platform.ImpactorGamePlatformPlayer;
import net.impactdev.impactor.locale.LocaleProvider;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public final class ForgePlatformPlayer extends ImpactorGamePlatformPlayer {

    public ForgePlatformPlayer(UUID uuid) {
        super(uuid);
    }

    @Override
    public Locale locale() {
        return this.asMinecraftPlayer()
                .map(player -> (LocaleProvider) player)
                .map(LocaleProvider::locale)
                .orElse(Locale.getDefault());
    }

    @Override
    public Optional<ServerPlayer> asMinecraftPlayer() {
        return ((ForgeImpactorPlugin) ForgeImpactorPlugin.instance())
                .server()
                .map(server -> server.getPlayerList().getPlayer(this.uuid()));
    }

    public static final class ForgePlatformPlayerFactory implements Factory {

        private final LoadingCache<UUID, ForgePlatformPlayer> cache = Caffeine.newBuilder().build(ForgePlatformPlayer::new);

        @Override
        public PlatformPlayer create(@NotNull UUID uuid) {
            return this.cache.get(uuid);
        }
    }

}
