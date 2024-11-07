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
import net.impactdev.impactor.core.translations.locale.LocaleCache;
import net.impactdev.impactor.minecraft.api.items.AdventureTranslator;
import net.impactdev.impactor.minecraft.api.items.ItemStackTranslator;
import net.impactdev.impactor.minecraft.api.items.ServerProvider;
import net.impactdev.impactor.minecraft.items.transactions.ImpactorItemTransaction;
import net.impactdev.impactor.minecraft.platform.GamePlatform;
import net.impactdev.impactor.minecraft.utility.RandomProvider;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.pointer.Pointer;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import net.kyori.adventure.translation.GlobalTranslator;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
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
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.math.vector.Vector2d;
import org.spongepowered.math.vector.Vector3d;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public abstract class ImpactorPlatformPlayer extends ImpactorPlatformSource implements PlatformPlayer {

    public static final Pointer<ServerPlayer> PLAYER_FALLBACK = Pointer.pointer(
            ServerPlayer.class,
            Key.key("impactor", "player-fallback")
    );

    public ImpactorPlatformPlayer(UUID uuid) {
        super(uuid, SourceType.PLAYER);
        AdventureTranslator.Server translator = AdventureTranslator.Server.get(ServerProvider.server());

        this.offer(MetadataKeys.DISPLAY_NAME, () -> this.asMinecraftPlayer()
                .map(Entity::getCustomName)
                .map(translator::asAdventure)
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
        AdventureTranslator.Server translator = AdventureTranslator.Server.get(ServerProvider.server());

        return this.asMinecraftPlayer()
                .map(Player::getName)
                .map(translator::asAdventure)
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
    public Locale locale() {
        return this.asMinecraftPlayer()
                .map(player -> LocaleCache.getLocale(player.clientInformation().language()))
                .orElse(Locale.getDefault());
    }

    @Override
    public void sendMessage(@NotNull Identity source, @NotNull Component message, @NotNull MessageType type) {
        AdventureTranslator.Server translator = AdventureTranslator.Server.get(ServerProvider.server());

        Component translated = GlobalTranslator.render(message, this.locale());
        net.minecraft.network.chat.Component vanilla = translator.asNative(translated);

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
        AdventureTranslator.Server translator = AdventureTranslator.Server.get(ServerProvider.server());

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
                            translator.asNative((Component) value)
                    ));
                } else {
                    target.connection.send(new ClientboundSetSubtitleTextPacket(
                            translator.asNative((Component) value)
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
        AdventureTranslator translator = AdventureTranslator.get();

        this.asMinecraftPlayer().ifPresent(target -> {
            final Optional<Holder.Reference<SoundEvent>> reference = BuiltInRegistries.SOUND_EVENT.holders()
                    .filter(event -> event.is(translator.asNative(sound.name())))
                    .findFirst();

            reference.ifPresent(soundEventReference -> target.connection.send(new ClientboundSoundPacket(
                    soundEventReference,
                    SoundSource.valueOf(sound.source().name()),
                    x,
                    y,
                    z,
                    sound.volume(),
                    sound.pitch(),
                    RandomProvider.nextLong()
            )));
        });
    }

    @Override
    public void playSound(@NotNull Sound sound, Sound.@NotNull Emitter emitter) {
        AdventureTranslator translator = AdventureTranslator.get();

        this.asMinecraftPlayer().ifPresent(target -> {
            final Optional<Holder.Reference<SoundEvent>> reference = BuiltInRegistries.SOUND_EVENT.holders()
                    .filter(event -> event.is(translator.asNative(sound.name())))
                    .findFirst();

            if(reference.isPresent()) {
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
                        reference.get(),
                        SoundSource.valueOf(sound.source().name()),
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
        AdventureTranslator translator = AdventureTranslator.get();

        this.asMinecraftPlayer().ifPresent(target -> {
            target.connection.send(new ClientboundStopSoundPacket(
                    stop.sound() != null ? translator.asNative(stop.sound()) : null,
                    stop.source() != null ? SoundSource.valueOf(stop.source().name()) : null
            ));
        });
    }

    private net.minecraft.network.chat.Component toVanillaComponent(@NotNull Component message) {
        AdventureTranslator.Server translator = AdventureTranslator.Server.get(ServerProvider.server());
        return translator.asNative(GlobalTranslator.render(message, this.locale()));
    }
}
