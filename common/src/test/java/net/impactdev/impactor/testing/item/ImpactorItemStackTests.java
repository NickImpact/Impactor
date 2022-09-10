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

package net.impactdev.impactor.testing.item;

import com.google.common.collect.Lists;
import net.impactdev.impactor.api.items.ImpactorItemStack;
import net.impactdev.impactor.api.items.extensions.BookStack;
import net.impactdev.impactor.api.items.extensions.SkullStack;
import net.impactdev.impactor.api.items.properties.MetaFlag;
import net.impactdev.impactor.api.items.types.ItemType;
import net.impactdev.impactor.api.items.types.ItemTypes;
import net.impactdev.impactor.items.stacks.ImpactorAbstractedItemStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ImpactorItemStackTests {

    @Test
    public void itemTypes() {
        ItemType air = ItemTypes.AIR;
        assertEquals("minecraft", air.key().namespace());
        assertEquals("air", air.key().value());
    }

    @Test
    public void basicItemStack() {
        ImpactorItemStack basic = ImpactorItemStack.basic()
                .type(ItemTypes.DIRT)
                .title(Component.text("Test Item"))
                .unbreakable()
                .build();

        assertEquals(ItemTypes.DIRT, basic.type());
        assertNotNull(basic.title());
        assertTrue(basic.unbreakable());
        assertEquals(1, basic.quantity());

        ImpactorItemStack basic2 = ImpactorItemStack.basic()
                .type(ItemTypes.GRASS)
                .glow()
                .hide(MetaFlag.UNBREAKABLE)
                .unbreakable()
                .quantity(5)
                .build();
        assertEquals(5, basic2.quantity());
        assertEquals("grass", basic2.type().key().value());
        assertTrue(basic2.unbreakable());
        assertTrue(basic2.flags().contains(MetaFlag.ENCHANTMENTS));

        ItemStack test = ((ImpactorAbstractedItemStack) basic2).toNative();
        assertEquals(Items.GRASS, test.getItem());
        assertTrue(test.isEnchanted());
    }

    @Test
    public void skullItemStack() {
        SkullStack skeleton = ImpactorItemStack.skull()
                .mob()
                .type(SkullStack.SkullType.SKELETON)
                .title(Component.text("A skeleton skull").color(NamedTextColor.YELLOW))
                .quantity(1)
                .build();
        assertEquals(1, skeleton.quantity());
        assertEquals(ItemTypes.SKELETON_SKULL, skeleton.type());
        assertEquals(0, skeleton.enchantments().size());

        ItemStack sSkull = ((ImpactorAbstractedItemStack) skeleton).toNative();
        assertEquals(1, sSkull.getCount());
        assertEquals(Items.SKELETON_SKULL, sSkull.getItem());
        assertFalse(sSkull.getOrCreateTag().contains("SkullOwner"));

        SkullStack player = ImpactorItemStack.skull()
                .player()
                .of("NickImpact")
                .complete()
                .title(mini("<gradient:green:blue>NickImpact's Skull"))
                .lore(Lists.newArrayList(
                        mini("<gray>A skull of a really cool guy")
                ))
                .unbreakable()
                .quantity(1)
                .build();
        assertTrue(player.supportsTextures());
        assertFalse(player.skullType().isPresent());
        assertEquals(1, player.quantity());
        assertTrue(player.owner().filter(username -> username.equals("NickImpact")).isPresent());
        assertTrue(player.unbreakable());

        ItemStack pSkull = ((ImpactorAbstractedItemStack) player).toNative();
        assertTrue(pSkull.getOrCreateTag().contains("SkullOwner"));
        assertNotNull(pSkull.getOrCreateTag().get("SkullOwner"));
        assertEquals("NickImpact", pSkull.getOrCreateTag().getString("SkullOwner"));
        assertTrue(pSkull.getOrCreateTag().getBoolean("Unbreakable"));
    }

    @Test
    public void bookItemStack() {
        BookStack book = ImpactorItemStack.book()
                .type(BookStack.BookType.WRITTEN)
                .title(Component.text("A Very Neat Book").color(NamedTextColor.GOLD))
                .author("NickImpact")
                .generation(BookStack.Generation.ORIGINAL)
                .page(1, Component.text("A set of text that makes up").append(Component.text(" page 1")))
                .page(3, Component.text("Hello World!").color(NamedTextColor.RED))
                .build();

        assertEquals(BookStack.BookType.WRITTEN, book.bookType());
        assertEquals(2, book.pages());
        assertEquals(BookStack.Generation.ORIGINAL, book.generation());

        ItemStack minecraft = ((ImpactorAbstractedItemStack) book).toNative();
        CompoundTag nbt = minecraft.getOrCreateTag();
        assertEquals(Items.WRITTEN_BOOK, minecraft.getItem());
        assertEquals(0, nbt.getInt("generation"));
        assertEquals(3, nbt.getList("pages", 8).size());
        assertEquals(Component.text("NickImpact"), GsonComponentSerializer.gson().deserialize(nbt.getString("author")));
    }

    private static Component mini(String input) {
        return MiniMessage.miniMessage().deserialize(input);
    }

}
