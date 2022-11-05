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
import net.impactdev.impactor.api.services.economy.accounts.AccountAccessor;
import net.impactdev.impactor.economy.accounts.accessors.UniqueAccountAccessor;
import net.impactdev.impactor.util.ResourceKeyTranslator;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import net.kyori.adventure.translation.GlobalTranslator;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.protocol.game.ClientboundCustomSoundPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesPacket;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.math.vector.Vector3d;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public abstract class ImpactorPlatformPlayer implements PlatformPlayer {

    private final UUID uuid;
    private final AccountAccessor accessor;

    public ImpactorPlatformPlayer(UUID uuid) {
        this.uuid = uuid;
        this.accessor = new UniqueAccountAccessor(this.uuid);
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

    @Override
    public Vector3d position() {
        return this.asMinecraftPlayer()
                .map(Entity::position)
                .map(vector -> new Vector3d(vector.x, vector.y, vector.z))
                .orElseThrow(() -> new IllegalStateException("Target player not found"));
    }

    @Override
    public AccountAccessor accountAccessor() {
        return this.accessor;
    }

    @Override
    public void sendMessage(@NotNull Identity source, @NotNull Component message, @NotNull MessageType type) {
        Component translated = GlobalTranslator.render(message, this.locale());
        net.minecraft.network.chat.Component vanilla = AdventureTranslator.toNative(translated);

        this.asMinecraftPlayer().ifPresent(target -> target.sendMessage(vanilla, ChatTypeMapping.mapping(type), source.uuid()));
    }

    @Override
    public void sendActionBar(@NotNull Component message) {
        this.asMinecraftPlayer().ifPresent(target -> target.sendMessage(this.toVanillaComponent(message), ChatType.GAME_INFO, Identity.nil().uuid()));
    }

    @Override
    public <T> void sendTitlePart(@NotNull TitlePart<T> part, @NotNull T value) {
        this.asMinecraftPlayer().ifPresent(target -> {
            if(part == TitlePart.TIMES) {
                final Title.Times times = (Title.Times) value;
                target.connection.send(new ClientboundSetTitlesPacket(
                        (int) (times.fadeIn().toMillis() / 50L),
                        (int) (times.stay().toMillis() / 50L),
                        (int) (times.fadeOut().toMillis() / 50L)
                ));
            } else {
                if(part == TitlePart.TITLE) {
                    target.connection.send(new ClientboundSetTitlesPacket(
                            ClientboundSetTitlesPacket.Type.TITLE,
                            AdventureTranslator.toNative((Component) value)
                    ));
                } else {
                    target.connection.send(new ClientboundSetTitlesPacket(
                            ClientboundSetTitlesPacket.Type.SUBTITLE,
                            AdventureTranslator.toNative((Component) value)
                    ));
                }
            }
        });
    }

    @Override
    public void playSound(@NotNull Sound sound) {
        this.asMinecraftPlayer().ifPresent(target -> this.playSound(sound, target.getX(), target.getY(), target.getZ()));
    }

    @Override
    public void playSound(@NotNull Sound sound, double x, double y, double z) {
        this.asMinecraftPlayer().ifPresent(target -> {
            final Optional<SoundEvent> event = Registry.SOUND_EVENT.getOptional(ResourceKeyTranslator.asResourceLocation(sound.name()));
            if(event.isPresent()) {
                target.connection.send(new ClientboundSoundPacket(event.get(), AdventureTranslator.asVanilla(sound.source()), x, y, z, sound.volume(), sound.pitch()));
            } else {
                target.connection.send(new ClientboundCustomSoundPacket(
                        ResourceKeyTranslator.asResourceLocation(sound.name()),
                        AdventureTranslator.asVanilla(sound.source()),
                        new net.minecraft.world.phys.Vec3(x, y, z),
                        sound.volume(),
                        sound.pitch()
                ));
            }
        });
    }

    @Override
    public void playSound(@NotNull Sound sound, Sound.@NotNull Emitter emitter) {
        this.asMinecraftPlayer().ifPresent(target -> {
            final Optional<SoundEvent> event = Registry.SOUND_EVENT.getOptional(ResourceKeyTranslator.asResourceLocation(Objects.requireNonNull(sound, "sound").name()));
            if(event.isPresent()) {
                final Entity tracked;
                if(emitter == Sound.Emitter.self()) {
                    tracked = target;
                } else {
                    if(emitter instanceof Entity) {
                        tracked = (Entity) emitter;
                    } else {
                        throw new IllegalArgumentException("Specified emitter was not valid: '" + emitter.getClass() + "'");
                    }
                }

                target.connection.send(new ClientboundSoundEntityPacket(
                        event.get(),
                        AdventureTranslator.asVanilla(sound.source()),
                        tracked,
                        sound.volume(),
                        sound.pitch()
                ));
            }
        });
    }

    @Override
    public void stopSound(@NotNull SoundStop stop) {
        this.asMinecraftPlayer().ifPresent(target -> {
            target.connection.send(new ClientboundStopSoundPacket(ResourceKeyTranslator.asResourceLocationNullable(stop.sound()), AdventureTranslator.asVanillaNullable(stop.source())));
        });
    }

    private net.minecraft.network.chat.Component toVanillaComponent(@NotNull Component message) {
        return AdventureTranslator.toNative(GlobalTranslator.render(message, this.locale()));
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
