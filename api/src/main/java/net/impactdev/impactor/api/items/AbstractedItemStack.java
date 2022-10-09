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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.impactdev.impactor.api.items.builders.AbstractStackBuilder;
import net.impactdev.impactor.api.items.properties.MetaFlag;
import net.impactdev.impactor.api.items.properties.enchantments.Enchantment;
import net.impactdev.impactor.api.items.types.ItemType;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Set;

public abstract class AbstractedItemStack implements ImpactorItemStack {

    protected final ItemType type;
    protected final Component title;
    protected final List<Component> lore;
    protected final int quantity;
    protected final Set<Enchantment> enchantments;
    protected final Set<MetaFlag> flags;
    protected final boolean unbreakable;

    public AbstractedItemStack(ItemType type, AbstractStackBuilder<?, ?> builder) {
        this.type = type;
        this.title = builder.title;
        this.lore = ImmutableList.copyOf(builder.lore);
        this.quantity = builder.quantity;
        this.enchantments = ImmutableSet.copyOf(builder.enchantments);
        this.flags = ImmutableSet.copyOf(builder.flags);
        this.unbreakable = builder.unbreakable;
    }

    @Override
    public ItemType type() {
        return this.type;
    }

    @Override
    public Component title() {
        return this.title;
    }

    @Override
    public List<Component> lore() {
        return this.lore;
    }

    @Override
    public int quantity() {
        return this.quantity;
    }

    @Override
    public Set<Enchantment> enchantments() {
        return this.enchantments;
    }

    @Override
    public Set<MetaFlag> flags() {
        return this.flags;
    }

    @Override
    public boolean unbreakable() {
        return this.unbreakable;
    }

    @Override
    public CompoundBinaryTag nbt() {
        return CompoundBinaryTag.empty();
    }

}
