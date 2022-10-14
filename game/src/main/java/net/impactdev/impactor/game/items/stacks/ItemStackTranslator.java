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

import net.impactdev.impactor.api.items.ImpactorItemStack;
import net.impactdev.impactor.api.items.properties.MetaFlag;
import net.impactdev.impactor.api.items.properties.enchantments.Enchantment;
import net.impactdev.impactor.game.items.ImpactorItemType;
import net.impactdev.impactor.util.ResourceKeyTranslator;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.BinaryTagType;
import net.kyori.adventure.nbt.BinaryTagTypes;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.ListBinaryTag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.core.Registry;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.function.Function;

public final class ItemStackTranslator {

    public static ItemStack translate(ImpactorItemStack stack) {
        ItemLike like = ((ImpactorItemType)stack.type()).minecraft().orElse(null);
        ItemStack result = new ItemStack(like);
        result.setCount(stack.quantity());
        if(stack.title() != null) {
            result.getOrCreateTagElement("display").putString("Name", GsonComponentSerializer.gson().serialize(stack.title()));
        }

        if(!stack.lore().isEmpty()) {
            ListTag lore = new ListTag();
            for (Component line : stack.lore()) {
                lore.add(StringTag.valueOf(GsonComponentSerializer.gson().serialize(line)));
            }
            result.getOrCreateTagElement("display").put("Lore", lore);
        }

        for(Enchantment enchantment : stack.enchantments()) {
            net.minecraft.world.item.enchantment.Enchantment target = Registry.ENCHANTMENT.get(ResourceKeyTranslator.asResourceLocation(enchantment.type()));
            result.enchant(target, enchantment.level());
        }

        if(stack.unbreakable()) {
            result.getOrCreateTag().putBoolean("Unbreakable", true);
        }

        int flags = 0;
        for(MetaFlag flag : stack.flags()) {
            flags |= (1 << flag.ordinal());
        }
        result.getOrCreateTag().putInt("HideFlags", flags);

        translateNBT(result.getOrCreateTag(), stack.nbt());
        return result;
    }

    private static void translateNBT(CompoundTag minecraft, CompoundBinaryTag impactor) {
        translateCompound(minecraft, impactor);
    }

    private static CompoundTag translateCompound(CompoundTag source, CompoundBinaryTag compound) {
        compound.forEach(entry -> {
            String key = entry.getKey();
            BinaryTag tag = entry.getValue();

            if(tag.type().equals(BinaryTagTypes.COMPOUND)) {
                source.put(key, translateCompound(new CompoundTag(), (CompoundBinaryTag) tag));
            } else if(tag.type().equals(BinaryTagTypes.LIST)) {
                source.put(key, translateList((ListBinaryTag) tag));
            } else if(tag.type().equals(BinaryTagTypes.STRING)) {
                source.putString(key, as(BinaryTagTypes.STRING, tag).value());
            } else if(tag.type().equals(BinaryTagTypes.BYTE)) {
                source.putByte(key, as(BinaryTagTypes.BYTE, tag).value());
            } else if(tag.type().equals(BinaryTagTypes.SHORT)) {
                source.putShort(key, as(BinaryTagTypes.SHORT, tag).value());
            } else if(tag.type().equals(BinaryTagTypes.INT)) {
                source.putInt(key, as(BinaryTagTypes.INT, tag).value());
            } else if(tag.type().equals(BinaryTagTypes.LONG)) {
                source.putLong(key, as(BinaryTagTypes.LONG, tag).value());
            } else if(tag.type().equals(BinaryTagTypes.FLOAT)) {
                source.putFloat(key, as(BinaryTagTypes.FLOAT, tag).value());
            } else if(tag.type().equals(BinaryTagTypes.DOUBLE)) {
                source.putDouble(key, as(BinaryTagTypes.DOUBLE, tag).value());
            } else if(tag.type().equals(BinaryTagTypes.BYTE_ARRAY)) {
                source.putByteArray(key, as(BinaryTagTypes.BYTE_ARRAY, tag).value());
            } else if(tag.type().equals(BinaryTagTypes.INT_ARRAY)) {
                source.putIntArray(key, as(BinaryTagTypes.INT_ARRAY, tag).value());
            } else if(tag.type().equals(BinaryTagTypes.LONG_ARRAY)) {
                source.putLongArray(key, as(BinaryTagTypes.LONG_ARRAY, tag).value());
            }
        });

        return source;
    }

    private static ListTag translateList(ListBinaryTag list) {
        ListTag minecraft = new ListTag();
        list.forEach(tag -> {
            if(tag.type().equals(BinaryTagTypes.COMPOUND)) {
                minecraft.add(translateCompound(new CompoundTag(), (CompoundBinaryTag) tag));
            } else if(tag.type().equals(BinaryTagTypes.LIST)) {
                minecraft.add(translateList((ListBinaryTag) tag));
            } else if(tag.type().equals(BinaryTagTypes.STRING)) {
                minecraft.add(asMinecraft(as(BinaryTagTypes.STRING, tag).value(), StringTag::valueOf));
            } else if(tag.type().equals(BinaryTagTypes.BYTE)) {
                minecraft.add(asMinecraft(as(BinaryTagTypes.BYTE, tag).value(), ByteTag::valueOf));
            } else if(tag.type().equals(BinaryTagTypes.SHORT)) {
                minecraft.add(asMinecraft(as(BinaryTagTypes.SHORT, tag).value(), ShortTag::valueOf));
            } else if(tag.type().equals(BinaryTagTypes.INT)) {
                minecraft.add(asMinecraft(as(BinaryTagTypes.INT, tag).value(), IntTag::valueOf));
            } else if(tag.type().equals(BinaryTagTypes.LONG)) {
                minecraft.add(asMinecraft(as(BinaryTagTypes.LONG, tag).value(), LongTag::valueOf));
            } else if(tag.type().equals(BinaryTagTypes.FLOAT)) {
                minecraft.add(asMinecraft(as(BinaryTagTypes.FLOAT, tag).value(), FloatTag::valueOf));
            } else if(tag.type().equals(BinaryTagTypes.DOUBLE)) {
                minecraft.add(asMinecraft(as(BinaryTagTypes.DOUBLE, tag).value(), DoubleTag::valueOf));
            } else if(tag.type().equals(BinaryTagTypes.BYTE_ARRAY)) {
                minecraft.add(asMinecraft(as(BinaryTagTypes.BYTE_ARRAY, tag).value(), ByteArrayTag::new));
            } else if(tag.type().equals(BinaryTagTypes.INT_ARRAY)) {
                minecraft.add(asMinecraft(as(BinaryTagTypes.INT_ARRAY, tag).value(), IntArrayTag::new));
            } else if(tag.type().equals(BinaryTagTypes.LONG_ARRAY)) {
                minecraft.add(asMinecraft(as(BinaryTagTypes.LONG_ARRAY, tag).value(), LongArrayTag::new));
            }
        });

        return minecraft;
    }

    private static <T extends BinaryTag> T as(BinaryTagType<T> type, BinaryTag tag) {
        return (T) tag;
    }

    private static <M extends Tag, T> M asMinecraft(T value, Function<T, M> translator) {
        return translator.apply(value);
    }

}
