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

package net.impactdev.impactor.items.stacks.providers;

import net.impactdev.impactor.api.items.AbstractedItemStack;
import net.impactdev.impactor.api.items.ImpactorItemStack;
import net.impactdev.impactor.api.items.builders.ImpactorItemStackBuilder;
import net.impactdev.impactor.api.items.properties.enchantments.Enchantment;
import net.impactdev.impactor.api.items.properties.MetaFlag;
import net.impactdev.impactor.items.stacks.builders.ImpactorBasicStackBuilder;
import net.minecraft.world.item.ItemStack;

public class ImpactorBasicStack extends AbstractedItemStack {

    public ImpactorBasicStack(ImpactorBasicStackBuilder builder) {
        super(builder.type, builder);
    }

    @Override
    public <I extends ImpactorItemStack, B extends ImpactorItemStackBuilder<I, B>> B asBuilder() {
        B builder = (B) ImpactorItemStack.basic()
                .type(this.type())
                .title(this.title())
                .lore(this.lore())
                .quantity(this.quantity());

        for(Enchantment enchantment : this.enchantments()) {
            builder.enchantment(enchantment);
        }

        for(MetaFlag flag : this.flags()) {
            builder.hide(flag);
        }

        if(this.unbreakable()) {
            builder.unbreakable();
        }

        return builder;
    }
}
