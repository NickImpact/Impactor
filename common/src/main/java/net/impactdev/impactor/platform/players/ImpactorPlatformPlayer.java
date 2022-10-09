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

package net.impactdev.impactor.platform.players;

import net.impactdev.impactor.adventure.AdventureTranslator;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.GlobalTranslator;
import net.minecraft.network.chat.ChatType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.math.vector.Vector3d;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public abstract class ImpactorPlatformPlayer implements PlatformPlayer {

    private final UUID uuid;

    public ImpactorPlatformPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public UUID uuid() {
        return this.uuid;
    }

    protected abstract Optional<ServerPlayer> asMinecraftPlayer();

    @Override
    public Component name() {
        return this.asMinecraftPlayer()
                .map(Player::getName)
                .map(AdventureTranslator::fromNative)
                .orElse(Component.text("Unknown"));
    }

    @Override
    public Component displayName() {
        return this.asMinecraftPlayer()
                .map(Entity::getCustomName)
                .map(AdventureTranslator::fromNative)
                .orElse(Component.text("Unknown"));
    }

//    @Override
//    public ServerLevel world() {
//        return this.asMinecraftPlayer()
//                .map(ServerPlayer::getLevel)
//                .orElseThrow(() -> new IllegalStateException("Target player not found"));
//    }

    @Override
    public Vector3d position() {
        return this.asMinecraftPlayer()
                .map(Entity::position)
                .map(vector -> new Vector3d(vector.x, vector.y, vector.z))
                .orElseThrow(() -> new IllegalStateException("Target player not found"));
    }

    @Override
    public void sendMessage(@NotNull Identity source, @NotNull Component message, @NotNull MessageType type) {
        Component translated = GlobalTranslator.render(message, this.locale());
        net.minecraft.network.chat.Component vanilla = AdventureTranslator.toNative(translated);

        this.asMinecraftPlayer().ifPresent(target -> target.sendMessage(vanilla, ChatTypeMapping.mapping(type), source.uuid()));
    }

    private enum ChatTypeMapping {
        CHAT(ChatType.CHAT, MessageType.CHAT),
        SYSTEM(ChatType.SYSTEM, MessageType.SYSTEM);

        private final ChatType minecraft;
        private final MessageType adventure;

        ChatTypeMapping(final ChatType minecraft, final MessageType adventure) {
            this.minecraft = minecraft;
            this.adventure = adventure;
        }

        public static ChatType mapping(MessageType type) {
            return Arrays.stream(values())
                    .filter(m -> m.adventure.equals(type))
                    .map(m -> m.minecraft)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid message type"));
        }

        public ChatType minecraft() {
            return this.minecraft;
        }

        public MessageType adventure() {
            return this.adventure;
        }
    }
}
