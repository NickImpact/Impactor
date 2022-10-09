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

package net.impactdev.impactor.sponge.platform.players;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.game.platform.ImpactorGamePlatformPlayer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.util.locale.LocaleSource;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public final class SpongePlatformPlayer extends ImpactorGamePlatformPlayer {

    public SpongePlatformPlayer(UUID uuid) {
        super(uuid);
    }

    @Override
    public Locale locale() {
        return Sponge.server().player(this.uuid()).map(LocaleSource::locale).orElse(Locale.getDefault());
    }

    @Override
    public Optional<ServerPlayer> asMinecraftPlayer() {
        return Sponge.server().player(this.uuid()).map(sponge -> (ServerPlayer) sponge);
    }

    public static final class SpongePlatformPlayerFactory implements PlatformPlayer.Factory {

        private final LoadingCache<UUID, SpongePlatformPlayer> cache = Caffeine.newBuilder().build(SpongePlatformPlayer::new);

        @Override
        public PlatformPlayer create(@NotNull UUID uuid) {
            return this.cache.get(uuid);
        }
    }

}