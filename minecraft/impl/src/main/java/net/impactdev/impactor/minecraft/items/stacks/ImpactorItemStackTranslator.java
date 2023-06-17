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

package net.impactdev.impactor.minecraft.items.stacks;

import net.impactdev.impactor.api.items.ImpactorItemStack;
import net.impactdev.impactor.api.items.builders.provided.BasicItemStackBuilder;
import net.impactdev.impactor.api.items.properties.MetaFlag;
import net.impactdev.impactor.api.items.properties.enchantments.Enchantment;
import net.impactdev.impactor.api.items.types.ItemType;
import net.impactdev.impactor.minecraft.api.items.ItemStackTranslator;
import net.impactdev.impactor.minecraft.api.text.AdventureTranslator;
import net.impactdev.impactor.minecraft.items.ImpactorItemType;
import net.impactdev.impactor.minecraft.api.key.ResourceKeyTranslator;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.BinaryTagType;
import net.kyori.adventure.nbt.BinaryTagTypes;
import net.kyori.adventure.nbt.ByteArrayBinaryTag;
import net.kyori.adventure.nbt.ByteBinaryTag;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.DoubleBinaryTag;
import net.kyori.adventure.nbt.FloatBinaryTag;
import net.kyori.adventure.nbt.IntArrayBinaryTag;
import net.kyori.adventure.nbt.IntBinaryTag;
import net.kyori.adventure.nbt.ListBinaryTag;
import net.kyori.adventure.nbt.LongArrayBinaryTag;
import net.kyori.adventure.nbt.LongBinaryTag;
import net.kyori.adventure.nbt.ShortBinaryTag;
import net.kyori.adventure.nbt.StringBinaryTag;
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
import net.minecraft.nbt.TagType;
import net.minecraft.nbt.TagTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ImpactorItemStackTranslator implements ItemStackTranslator {

    @Override
    public String name() {
        return "ItemStack Translator";
    }

    public ItemStack translate(ImpactorItemStack stack) {
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

    @Override
    public ImpactorItemStack from(ItemStack stack) {
        Key key = ResourceKeyTranslator.toAdventure(Registry.ITEM.getKey(stack.getItem()));
        ItemType type = ItemType.from(key);

        CompoundTag nbt = stack.getOrCreateTag();
        @Nullable ListTag lore = Optional.of(nbt.getCompound("display"))
                .filter(compound -> !compound.isEmpty())
                .map(display -> display.getList("Lore", Tag.TAG_STRING))
                .filter(list -> !list.isEmpty())
                .orElse(null);

        BasicItemStackBuilder builder = ImpactorItemStack.basic()
                .type(type)
                .title(AdventureTranslator.fromNative(stack.getHoverName()))
                .quantity(stack.getCount())
                .nbt(this.translateNativeNBT(nbt));

        if(lore != null) {
            builder.lore(lore.stream()
                    .map(tag -> GsonComponentSerializer.gson().deserialize(tag.getAsString()))
                    .collect(Collectors.toList())
            );
        }

        builder.unbreakable(nbt.getBoolean("Unbreakable"));
        int flags = nbt.getInt("HideFlags");
        for(MetaFlag flag : MetaFlag.values()) {
            if((flags & (1 << flag.ordinal())) == 1) {
                builder.hide(flag);
            }
        }

        ListTag enchantments = stack.getEnchantmentTags();
        for(Tag tag : enchantments) {
            CompoundTag compound = (CompoundTag) tag;
            builder.enchantment(Enchantment.create(Key.key(compound.getString("id")), compound.getInt("lvl")));
        }
        return builder.build();
    }

    private void translateNBT(CompoundTag minecraft, CompoundBinaryTag impactor) {
        translateCompound(minecraft, impactor);
    }

    private CompoundBinaryTag translateNativeNBT(CompoundTag minecraft) {
        AtomicReference<CompoundBinaryTag> result = new AtomicReference<>(CompoundBinaryTag.empty());
        minecraft.getAllKeys().forEach(key -> {
            @NotNull Tag tag = Objects.requireNonNull(minecraft.get(key));
            byte type = tag.getId();

            switch (type) {
                case Tag.TAG_BYTE -> result.set(result.get().putByte(key, from(ByteTag.TYPE, tag).getAsByte()));
                case Tag.TAG_SHORT -> result.set(result.get().putShort(key, from(ShortTag.TYPE, tag).getAsShort()));
                case Tag.TAG_INT -> result.set(result.get().putInt(key, from(IntTag.TYPE, tag).getAsInt()));
                case Tag.TAG_LONG -> result.set(result.get().putLong(key, from(LongTag.TYPE, tag).getAsLong()));
                case Tag.TAG_FLOAT -> result.set(result.get().putFloat(key, from(FloatTag.TYPE, tag).getAsFloat()));
                case Tag.TAG_DOUBLE -> result.set(result.get().putDouble(key, from(DoubleTag.TYPE, tag).getAsDouble()));
                case Tag.TAG_BYTE_ARRAY -> result.set(result.get().putByteArray(key, from(ByteArrayTag.TYPE, tag).getAsByteArray()));
                case Tag.TAG_STRING -> result.set(result.get().putString(key, from(StringTag.TYPE, tag).getAsString()));
                case Tag.TAG_LIST -> result.set(result.get().put(key, translateNativeListNBT(from(ListTag.TYPE, tag))));
                case Tag.TAG_COMPOUND -> result.set(result.get().put(key, translateNativeNBT(from(CompoundTag.TYPE, tag))));
                case Tag.TAG_INT_ARRAY -> result.set(result.get().putIntArray(key, from(IntArrayTag.TYPE, tag).getAsIntArray()));
                case Tag.TAG_LONG_ARRAY -> result.set(result.get().putLongArray(key, from(LongArrayTag.TYPE, tag).getAsLongArray()));
                default -> throw new IllegalStateException("End tag encountered");
            }
        });

        return result.get();
    }

    private ListBinaryTag translateNativeListNBT(ListTag minecraft) {
        AtomicReference<ListBinaryTag> result = new AtomicReference<>(ListBinaryTag.empty());
        minecraft.forEach(tag -> {
            byte type = tag.getId();

            switch (type) {
                case Tag.TAG_BYTE -> result.set(result.get().add(toAdventure(from(ByteTag.TYPE, tag).getAsByte(), ByteBinaryTag::byteBinaryTag)));
                case Tag.TAG_SHORT -> result.set(result.get().add(toAdventure(from(ShortTag.TYPE, tag).getAsShort(), ShortBinaryTag::shortBinaryTag)));
                case Tag.TAG_INT -> result.set(result.get().add(toAdventure(from(IntTag.TYPE, tag).getAsInt(), IntBinaryTag::intBinaryTag)));
                case Tag.TAG_LONG -> result.set(result.get().add(toAdventure(from(LongTag.TYPE, tag).getAsLong(), LongBinaryTag::longBinaryTag)));
                case Tag.TAG_FLOAT -> result.set(result.get().add(toAdventure(from(FloatTag.TYPE, tag).getAsFloat(), FloatBinaryTag::floatBinaryTag)));
                case Tag.TAG_DOUBLE -> result.set(result.get().add(toAdventure(from(DoubleTag.TYPE, tag).getAsDouble(), DoubleBinaryTag::doubleBinaryTag)));
                case Tag.TAG_BYTE_ARRAY -> {
                    ByteArrayBinaryTag ba = toAdventure(
                            from(ByteArrayTag.TYPE, tag).getAsByteArray(),
                            ByteArrayBinaryTag::byteArrayBinaryTag
                    );
                    result.set(result.get().add(ba));
                }
                case Tag.TAG_STRING -> result.set(result.get().add(toAdventure(from(StringTag.TYPE, tag).getAsString(), StringBinaryTag::stringBinaryTag)));
                case Tag.TAG_LIST -> {
                    ListBinaryTag lb = translateNativeListNBT(from(ListTag.TYPE, tag));
                    result.set(result.get().add((BinaryTag) lb));
                }
                case Tag.TAG_COMPOUND -> result.set(result.get().add(translateNativeNBT(from(CompoundTag.TYPE, tag))));
                case Tag.TAG_INT_ARRAY -> {
                    IntArrayBinaryTag ia = toAdventure(
                            from(IntArrayTag.TYPE, tag).getAsIntArray(),
                            IntArrayBinaryTag::intArrayBinaryTag
                    );
                    result.set(result.get().add(ia));
                }
                case Tag.TAG_LONG_ARRAY -> {
                    LongArrayBinaryTag la = toAdventure(
                            from(LongArrayTag.TYPE, tag).getAsLongArray(),
                            LongArrayBinaryTag::longArrayBinaryTag
                    );
                    result.set(result.get().add(la));
                }
                default -> throw new IllegalStateException("End tag encountered");
            }
        });

        return result.get();
    }

    private CompoundTag translateCompound(CompoundTag source, CompoundBinaryTag compound) {
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

    private ListTag translateList(ListBinaryTag list) {
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

    @SuppressWarnings("unchecked")
    private <T extends BinaryTag> T as(BinaryTagType<T> type, BinaryTag tag) {
        return (T) tag;
    }

    private <M extends Tag, T> M asMinecraft(T value, Function<T, M> translator) {
        return translator.apply(value);
    }

    private <T extends Tag> T from(TagType<T> type, Tag tag) {
        return (T) tag;
    }

    private <M extends BinaryTag, T> M toAdventure(T value, Function<T, M> translator) {
        return translator.apply(value);
    }

}
