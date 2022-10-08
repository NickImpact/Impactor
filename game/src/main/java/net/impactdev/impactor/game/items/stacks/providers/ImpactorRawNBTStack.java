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

package net.impactdev.impactor.game.items.stacks.providers;

import net.impactdev.impactor.api.items.extensions.RawNBTStack;
import net.impactdev.impactor.api.items.properties.MetaFlag;
import net.impactdev.impactor.api.items.properties.enchantments.Enchantment;
import net.impactdev.impactor.api.items.types.ItemType;
import net.impactdev.impactor.api.items.types.ItemTypes;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

@SuppressWarnings("PatternValidation")
public class ImpactorRawNBTStack implements RawNBTStack {

    private final CompoundTag parent;

    private ImpactorRawNBTStack(CompoundTag tag) {
        this.parent = tag;
    }

    @Override
    public CompoundTag nbt() {
        return this.parent;
    }

    @Override
    public ItemType type() {
        return this.findOrDefault("id", nbt -> ItemType.from(Key.key(nbt.getString("id"))), ItemTypes.AIR);
    }

    @Override
    public Component title() {
        return this.findOrDefault("display.Name", nbt -> MiniMessage.miniMessage().deserialize(nbt.getString("Name")), null);
    }

    @Override
    public List<Component> lore() {
        return null;
    }

    @Override
    public int quantity() {
        return 0;
    }

    @Override
    public Set<Enchantment> enchantments() {
        return null;
    }

    @Override
    public Set<MetaFlag> flags() {
        return null;
    }

    @Override
    public boolean unbreakable() {
        return false;
    }

    @Override
    public ItemStack asMinecraftNative() {
        return null;
    }

    private <T> T findOrDefault(String path, Function<CompoundTag, T> translator, T def) {
        return def;
    }

    public static class RawNBTFactory implements Factory {

        @Override
        public RawNBTStack from(CompoundTag nbt) {
            return null;
        }
    }

}
