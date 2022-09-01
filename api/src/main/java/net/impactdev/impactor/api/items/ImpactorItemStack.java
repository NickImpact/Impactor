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

package net.impactdev.impactor.api.items;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.items.builders.ImpactorItemStackBuilder;
import net.impactdev.impactor.api.items.builders.provided.BasicItemStackBuilder;
import net.impactdev.impactor.api.items.builders.provided.BookStackBuilder;
import net.impactdev.impactor.api.items.builders.provided.SkullStackBuilder;
import net.impactdev.impactor.api.items.properties.enchantments.Enchantment;
import net.impactdev.impactor.api.items.properties.MetaFlag;
import net.impactdev.impactor.api.items.types.ItemType;
import net.impactdev.impactor.api.items.types.ItemTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Set;

public interface ImpactorItemStack {

    static ImpactorItemStack empty() {
        return basic().type(ItemTypes.AIR).quantity(0).build();
    }

    static BasicItemStackBuilder basic() {
        return Impactor.instance().builders().provide(BasicItemStackBuilder.class);
    }

    static SkullStackBuilder skull() {
        return Impactor.instance().builders().provide(SkullStackBuilder.class);
    }

    static BookStackBuilder book() {
        return Impactor.instance().builders().provide(BookStackBuilder.class);
    }

    /**
     * Provides a native Minecraft ItemStack representation matching this particular Impactor ItemStack.
     * Effectively, all values assigned through this typing should match 1:1 on the native stack.
     *
     * <p>Implementations are expected to provide their means of creating the ItemStack correctly, such
     * as applying NBT tags or general fields. To simplify the process, {@link AbstractedItemStack#toNative()}
     * provides the basic support to assign the fields present at minimum with this interface
     * to the target ItemStack. Implementations can extend this to simplify the code they
     * need to write to complete their translation.
     *
     * @return A native Minecraft ItemStack with a 1:1 mapping of all the particular fields
     * of an extension to this interface.
     */
    ItemStack toNative();

    /**
     * Specifies the type of item this viewable item is based on. Effectively, this represents
     * a {@link net.kyori.adventure.key.Key key} that holds the namespace and value for
     * the item.
     *
     * @return The type of item of this ViewableItem
     */
    ItemType type();

    /**
     * Represents the title of the item. This will act as the display name of the ItemStack.
     *
     * <p>Note that in 1.13+, titles which do not forcibly set italics to false will be forced
     * into italics. To combat that, you'll want to update the style of your root component such
     * that italics is set to false.
     *
     * @return The title of the ViewableItem
     * @see net.kyori.adventure.text.format.Style.Builder#decoration(TextDecoration, TextDecoration.State)
     */
    Component title();

    /**
     * Represents the lore of the item. These components act as individual lines within the lore,
     * and do not inherit styling from the previous line.
     *
     * <p>Note that in 1.13+, each line of lore which do not forcibly set italics to false will be forced
     * into italics. To combat that, you'll want to update the style of your root component such
     * that italics is set to false.
     *
     * @return An immutable list of components featuring the lore of the ViewableItem
     */
    List<Component> lore();

    /**
     * Specifies the size of the ViewableItem stack. If this amount is zero, the item can be considered
     * empty. Empty items are not drawn to the client.
     *
     * @return The size of the ViewableItem stack
     */
    int quantity();

    /**
     * Specifies a set of enchantments that are applied to the item. Effectively, these provide the associated
     * key specifying the typing, as well as the level of the enchantment.
     *
     * @return An immutable set of enchantments applied to the item
     */
    Set<Enchantment> enchantments();

    /**
     * Represents a set of flags applied to the viewable item. These flags control visualization of certain
     * attributes of an item, such as it being unbreakable or providing a potion effect. Realistically,
     * a flag should only be available once on an item, so these flags are built into a set to help
     * indicate such.
     *
     * @return An immutable set of flags applied to the item
     */
    Set<MetaFlag> flags();

    /**
     * Specifies if the item is capable of receiving damage through usage. For example, items like a
     * pickaxe are capable of receiving damage from mining blocks or attacking entities. With this
     * flag enabled, the item effectively has infinite durability, and will last indefinitely without
     * ever breaking.
     *
     * <p>By default, all item stacks are considered to be breakable.
     *
     * @return <code>true</code> if the stack is unbreakable, <code>false</code> otherwise
     */
    boolean unbreakable();

    /**
     * Returns a new builder satisfied with the set values of this component. This is to allow
     * for mutability on an item stack without actually altering this particular stack, thereby
     * maintaining thread safety and immutability.
     *
     * @return A builder composed of the values making up this particular item stack
     * @param <B> The type of builder responsible for manipulating the item stack
     */
    <I extends ImpactorItemStack, B extends ImpactorItemStackBuilder<I, B>> B asBuilder();

}
