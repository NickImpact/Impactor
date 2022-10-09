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

import net.impactdev.impactor.api.items.ImpactorItemStack;
import net.impactdev.impactor.api.platform.players.transactions.ItemTransaction;
import net.impactdev.impactor.game.items.stacks.ItemStackTranslator;
import net.impactdev.impactor.platform.players.ImpactorPlatformPlayer;
import net.impactdev.impactor.platform.players.transactions.ImpactorItemTransaction;
import net.minecraft.world.item.ItemStack;

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
}
