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
import net.impactdev.impactor.api.items.ImpactorItemStack;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.platform.players.transactions.ItemTransaction;
import net.impactdev.impactor.platform.players.transactions.ImpactorItemTransaction;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.GlobalTranslator;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.math.vector.Vector3d;

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
    public ServerLevel world() {
        return this.asMinecraftPlayer()
                .map(ServerPlayer::getLevel)
                .orElseThrow(() -> new IllegalStateException("Target player not found"));
    }

    @Override
    public Vector3d position() {
        return this.asMinecraftPlayer()
                .map(Entity::position)
                .map(vector -> new Vector3d(vector.x, vector.y, vector.z))
                .orElseThrow(() -> new IllegalStateException("Target player not found"));
    }

    @Override
    public ItemTransaction offer(ImpactorItemStack stack) {
        return this.asMinecraftPlayer()
                .map(player -> player.inventory)
                .map(inventory -> {
                    ItemStack minecraft = stack.asMinecraftNative();

                    boolean result = inventory.add(minecraft);
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
    public void sendMessage(@NotNull Identity source, @NotNull Component message, @NotNull MessageType type) {
        Component translated = GlobalTranslator.render(message, this.locale());
        net.minecraft.network.chat.Component vanilla = AdventureTranslator.toNative(translated);

        // TODO - Add ChatType <-> MessageType mapping
        this.asMinecraftPlayer().ifPresent(target -> target.sendMessage(vanilla, source.uuid()));
    }
}
