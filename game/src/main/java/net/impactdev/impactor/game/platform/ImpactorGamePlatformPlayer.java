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

package net.impactdev.impactor.game.platform;

import com.google.common.collect.Lists;
import net.impactdev.impactor.api.items.ImpactorItemStack;
import net.impactdev.impactor.api.items.extensions.BookStack;
import net.impactdev.impactor.api.platform.players.transactions.ItemTransaction;
import net.impactdev.impactor.game.items.stacks.ItemStackTranslator;
import net.impactdev.impactor.platform.players.ImpactorPlatformPlayer;
import net.impactdev.impactor.platform.players.transactions.ImpactorItemTransaction;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.translation.GlobalTranslator;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundOpenBookPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class ImpactorGamePlatformPlayer extends ImpactorPlatformPlayer {

    public ImpactorGamePlatformPlayer(UUID uuid) {
        super(uuid);
    }

    @Override
    public ItemTransaction offer(ImpactorItemStack stack) {
        return this.asMinecraftPlayer()
                .map(player -> {
                    ItemStack minecraft = ItemStackTranslator.translate(stack);

                    boolean result = player.inventory.add(minecraft);
                    player.inventoryMenu.broadcastChanges();
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
    @SuppressWarnings("StaticPseudoFunctionalStyleMethod")
    public void openBook(@NotNull Book book) {
        this.asMinecraftPlayer().ifPresent(target -> {
            final ServerGamePacketListenerImpl connection = target.connection;
            final Inventory inventory = target.inventory;
            final int slot = inventory.items.size() + inventory.selected;

            final BookStack item = ImpactorItemStack.book()
                    .title(GlobalTranslator.render(book.title(), this.locale()))
                    .author(LegacyComponentSerializer.legacyAmpersand().serialize(GlobalTranslator.render(book.author(), this.locale())))
                    .pages(Lists.transform(book.pages(), page -> GlobalTranslator.render(page, this.locale())))
                    .build();
            final ItemStack vanilla = ItemStackTranslator.translate(item);

            connection.send(new ClientboundContainerSetSlotPacket(0, slot, vanilla));
            connection.send(new ClientboundOpenBookPacket(InteractionHand.MAIN_HAND));
            connection.send(new ClientboundContainerSetSlotPacket(0, slot, inventory.getSelected()));
        });
    }
}
