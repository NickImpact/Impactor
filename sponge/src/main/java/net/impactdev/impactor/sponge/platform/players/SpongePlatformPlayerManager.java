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

import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.platform.players.PlatformPlayerManager;
import net.impactdev.impactor.common.players.CommonPlatformPlayer;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import java.util.Optional;

public class SpongePlatformPlayerManager implements PlatformPlayerManager<ServerPlayer> {

    @Override
    public Class<ServerPlayer> platformType() {
        return ServerPlayer.class;
    }

    @Override
    public boolean verify(Object instance) {
        return platformType().isInstance(instance);
    }

    @Override
    public PlatformPlayer from(Object instance) {
        if(this.verify(instance)) {
            ServerPlayer player = this.platformType().cast(instance);
            return new CommonPlatformPlayer(player.uniqueId(), Component.text(player.name()));
        }

        throw new IllegalArgumentException("Type is not a acceptable player typing");
    }

    @Override
    public Optional<ServerPlayer> translate(PlatformPlayer player) {
        return Sponge.server().player(player.uuid());
    }
}
