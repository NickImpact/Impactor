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

package net.impactdev.impactor.minecraft.platform.sources;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.items.ImpactorItemStack;
import net.impactdev.impactor.api.items.extensions.BookStack;
import net.impactdev.impactor.api.items.platform.ItemTransaction;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.platform.sources.SourceType;
import net.impactdev.impactor.api.platform.sources.metadata.MetadataKeys;
import net.impactdev.impactor.core.platform.sources.ImpactorPlatformSource;
import net.impactdev.impactor.minecraft.api.items.ItemStackTranslator;
import net.impactdev.impactor.minecraft.items.transactions.ImpactorItemTransaction;
import net.impactdev.impactor.minecraft.platform.GamePlatform;
import net.impactdev.impactor.minecraft.api.text.AdventureTranslator;
import net.impactdev.impactor.minecraft.utility.RandomProvider;
import net.impactdev.impactor.minecraft.api.key.ResourceKeyTranslator;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import net.kyori.adventure.translation.GlobalTranslator;
import net.minecraft.core.Registry;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundCustomSoundPacket;
import net.minecraft.network.protocol.game.ClientboundOpenBookPacket;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.math.vector.Vector2d;
import org.spongepowered.math.vector.Vector3d;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public abstract class ImpactorPlatformPlayer extends ImpactorPlatformSource implements PlatformPlayer {

    public ImpactorPlatformPlayer(UUID uuid) {
        super(uuid, SourceType.PLAYER);

        this.offer(MetadataKeys.DISPLAY_NAME, () -> this.asMinecraftPlayer()
                .map(Entity::getCustomName)
                .map(AdventureTranslator::fromNative)
                .orElse(Component.text("Unknown"))
        );
        this.offer(MetadataKeys.POSITION, () -> this.asMinecraftPlayer()
                .map(Entity::position)
                .map(vec3 -> new Vector3d(vec3.x, vec3.y, vec3.z))
                .orElse(Vector3d.ZERO)
        );
        this.offer(MetadataKeys.ROTATION, () -> this.asMinecraftPlayer()
                .map(Entity::getRotationVector)
                .map(vec2 -> new Vector2d(vec2.x, vec2.y))
                .orElse(Vector2d.ZERO)
        );
        this.offer(MetadataKeys.PERMISSION_LEVEL, () -> this.asMinecraftPlayer()
                .map(player -> {
                    MinecraftServer server = ((GamePlatform) Impactor.instance().platform()).server();
                    if(server.getPlayerList().isOp(player.getGameProfile())) {
                        return 4;
                    }

                    return 0;
                })
                .orElse(4)
        );
    }

    public abstract Optional<ServerPlayer> asMinecraftPlayer();

    private Optional<GameProfile> profile() {
        GameProfileCache cache = ((GamePlatform) Impactor.instance().platform()).server().getProfileCache();
        return cache.get(this.uuid());
    }

    @Override
    public Component name() {
        return this.asMinecraftPlayer()
                .map(Player::getName)
                .map(AdventureTranslator::fromNative)
                .orElseGet(() -> this.profile()
                        .map(GameProfile::getName)
                        .map(Component::text)
                        .orElse(Component.text("Unknown"))
                );
    }

    @Override
    public ItemTransaction offer(ImpactorItemStack stack) {
        return this.asMinecraftPlayer()
                .map(player -> {
                    ItemStack minecraft = Impactor.instance().services()
                            .provide(ItemStackTranslator.class)
                            .translate(stack);

                    boolean result = player.addItem(minecraft);
                    return new ImpactorItemTransaction(
                            stack,
                            minecraft.getCount(),
                            result,
                            null
                    );
                })
                .orElse(null);
    }

    @Override
    public ItemTransaction take(ImpactorItemStack stack) {
        return null;
    }

    @Override
    @SuppressWarnings("StaticPseudoFunctionalStyleMethod")
    public void openBook(@NotNull Book book) {
        this.asMinecraftPlayer().ifPresent(target -> {
            final ServerGamePacketListenerImpl connection = target.connection;
            final Inventory inventory = target.getInventory();
            final int slot = inventory.items.size() + inventory.selected;

            final BookStack item = ImpactorItemStack.book()
                    .title(GlobalTranslator.render(book.title(), this.locale()))
                    .author(LegacyComponentSerializer.legacyAmpersand().serialize(GlobalTranslator.render(book.author(), this.locale())))
                    .pages(Lists.transform(book.pages(), page -> GlobalTranslator.render(page, this.locale())))
                    .build();

            final ItemStackTranslator translator = Impactor.instance().services().provide(ItemStackTranslator.class);
            final ItemStack vanilla = translator.translate(item);

            connection.send(new ClientboundContainerSetSlotPacket(0, target.containerMenu.getStateId(), slot, vanilla));
            connection.send(new ClientboundOpenBookPacket(InteractionHand.MAIN_HAND));
            connection.send(new ClientboundContainerSetSlotPacket(0, target.containerMenu.getStateId(), slot, inventory.getSelected()));
        });
    }

    @Override
    public void sendMessage(@NotNull Identity source, @NotNull Component message, @NotNull MessageType type) {
        Component translated = GlobalTranslator.render(message, this.locale());
        net.minecraft.network.chat.Component vanilla = AdventureTranslator.toNative(translated);

//        this.asMinecraftPlayer().ifPresent(target -> target.sendMessage(vanilla, ChatTypeMapping.mapping(type), source.uuid()));
        this.asMinecraftPlayer().ifPresent(target -> target.sendSystemMessage(vanilla));
    }

    @Override
    public void sendActionBar(@NotNull Component message) {
//        this.asMinecraftPlayer().ifPresent(target -> target.sendMessage(this.toVanillaComponent(message), ChatType.GAME_INFO, Identity.nil().uuid()));
        this.asMinecraftPlayer().ifPresent(target -> {
            ClientboundSetActionBarTextPacket packet = new ClientboundSetActionBarTextPacket(this.toVanillaComponent(message));
            target.connection.send(packet);
        });
    }

    @Override
    public <T> void sendTitlePart(@NotNull TitlePart<T> part, @NotNull T value) {
        this.asMinecraftPlayer().ifPresent(target -> {
            if(part == TitlePart.TIMES) {
                final Title.Times times = (Title.Times) value;
                target.connection.send(new ClientboundSetTitlesAnimationPacket(
                        (int) (times.fadeIn().toMillis() / 50L),
                        (int) (times.stay().toMillis() / 50L),
                        (int) (times.fadeOut().toMillis() / 50L)
                ));
            } else {
                if(part == TitlePart.TITLE) {
                    target.connection.send(new ClientboundSetTitleTextPacket(
                            AdventureTranslator.toNative((Component) value)
                    ));
                } else {
                    target.connection.send(new ClientboundSetSubtitleTextPacket(
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
                target.connection.send(new ClientboundSoundPacket(event.get(), AdventureTranslator.asVanilla(sound.source()), x, y, z, sound.volume(), sound.pitch(), RandomProvider.nextLong()));
            } else {
                target.connection.send(new ClientboundCustomSoundPacket(
                        ResourceKeyTranslator.asResourceLocation(sound.name()),
                        AdventureTranslator.asVanilla(sound.source()),
                        new net.minecraft.world.phys.Vec3(x, y, z),
                        sound.volume(),
                        sound.pitch(),
                        RandomProvider.nextLong()
                ));
            }
        });
    }

    @Override
    public void playSound(@NotNull Sound sound, Sound.@NotNull Emitter emitter) {
        this.asMinecraftPlayer().ifPresent(target -> {
            final Optional<SoundEvent> event = Registry.SOUND_EVENT.getOptional(ResourceKeyTranslator.asResourceLocation(
                    Objects.requireNonNull(sound, "sound").name()));
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
                        sound.pitch(),
                        RandomProvider.nextLong()
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

//    private enum ChatTypeMapping {
//        CHAT(ChatType.CHAT, MessageType.CHAT),
//        SYSTEM(ChatType.SYSTEM, MessageType.SYSTEM);
//
//        private final ChatType minecraft;
//        private final MessageType adventure;
//
//        ChatTypeMapping(final ChatType minecraft, final MessageType adventure) {
//            this.minecraft = minecraft;
//            this.adventure = adventure;
//        }
//
//        public static ChatType mapping(MessageType type) {
//            return Arrays.stream(values())
//                    .filter(m -> m.adventure.equals(type))
//                    .map(m -> m.minecraft)
//                    .findFirst()
//                    .orElseThrow(() -> new IllegalArgumentException("Invalid message type"));
//        }
//
//        public ChatType minecraft() {
//            return this.minecraft;
//        }
//
//        public MessageType adventure() {
//            return this.adventure;
//        }
//    }
}
