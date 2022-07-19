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

import net.impactdev.impactor.common.players.CommonPlatformPlayer;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.math.vector.Vector3d;

import java.util.Optional;
import java.util.UUID;

public class SpongePlatformPlayer extends CommonPlatformPlayer {

    public SpongePlatformPlayer(UUID uuid, Component name) {
        super(uuid, name);
    }

    @Override
    public void sendMessage(final @NotNull Identity source, final @NotNull Component message, final @NotNull MessageType type) {
        this.translate().ifPresent(player -> player.sendMessage(source, message, type));
    }

    @Override
    public void playSound(@NotNull Sound sound) {
        this.translate().ifPresent(player -> player.playSound(sound));
    }

    @Override
    public void playSound(@NotNull Sound sound, double x, double y, double z) {
        this.translate().ifPresent(player -> player.playSound(sound, x, y, z));
    }

    @Override
    public void playSound(@NotNull Sound sound, Sound.@NotNull Emitter emitter) {
        this.translate().ifPresent(player -> player.playSound(sound, emitter));
    }

    @Override
    public Vector3d position() {
        return this.translate().map(Entity::position).orElse(Vector3d.ZERO);
    }

    private Optional<ServerPlayer> translate() {
        return Sponge.server().player(this.uuid());
    }

}
