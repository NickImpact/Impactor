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

package net.impactdev.impactor.game.items.stacks;

import net.impactdev.impactor.api.items.AbstractedItemStack;
import net.impactdev.impactor.api.items.builders.AbstractStackBuilder;
import net.impactdev.impactor.api.items.properties.MetaFlag;
import net.impactdev.impactor.api.items.properties.enchantments.Enchantment;
import net.impactdev.impactor.api.items.types.ItemType;
import net.impactdev.impactor.game.items.ImpactorItemType;
import net.impactdev.impactor.util.ResourceKeyTranslator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.core.Registry;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public abstract class ImpactorAbstractedItemStack extends AbstractedItemStack {

    public ImpactorAbstractedItemStack(ItemType type, AbstractStackBuilder<?, ?> builder) {
        super(type, builder);
    }

    @Override
    public ItemStack asMinecraftNative() {
        ItemLike like = ((ImpactorItemType) this.type()).minecraft().orElse(null);
        ItemStack result = new ItemStack(like);
        result.setCount(this.quantity());
        if(this.title != null) {
            result.getOrCreateTagElement("display").putString("Name", GsonComponentSerializer.gson().serialize(this.title));
        }

        if(!this.lore.isEmpty()) {
            ListTag lore = new ListTag();
            for (Component line : this.lore()) {
                lore.add(StringTag.valueOf(GsonComponentSerializer.gson().serialize(line)));
            }
            result.getOrCreateTagElement("display").put("Lore", lore);
        }

        for(Enchantment enchantment : this.enchantments()) {
            net.minecraft.world.item.enchantment.Enchantment target = Registry.ENCHANTMENT.get(ResourceKeyTranslator.asResourceLocation(enchantment.type()));
            result.enchant(target, enchantment.level());
        }

        if(this.unbreakable) {
            result.getOrCreateTag().putBoolean("Unbreakable", true);
        }

        int flags = 0;
        for(MetaFlag flag : this.flags) {
            flags |= (1 << flag.ordinal());
        }
        result.getOrCreateTag().putInt("HideFlags", flags);

        return result;
    }
}
