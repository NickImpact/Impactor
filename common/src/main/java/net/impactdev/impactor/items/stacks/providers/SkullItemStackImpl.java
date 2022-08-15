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
import net.impactdev.impactor.api.items.builders.ImpactorItemStackBuilder;
import net.impactdev.impactor.api.items.extensions.SkullStack;
import net.impactdev.impactor.api.items.types.ItemTypes;
import net.impactdev.impactor.items.stacks.builders.SkullItemStackBuilderImpl;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class SkullItemStackImpl extends AbstractedItemStack implements SkullStack {

    private final SkullType type;
    private final String target;
    private final String texture;

    @SuppressWarnings("ConstantConditions")
    public SkullItemStackImpl(SkullItemStackBuilderImpl builder) {
        super(builder.isPlayer ? ItemTypes.PLAYER_HEAD : builder.type.delegate(), builder);
        this.type = builder.type;
        this.target = builder.target;
        this.texture = builder.texture;
    }

    @Override
    public boolean supportsTextures() {
        return this.skullType().isEmpty();
    }

    @Override
    public Optional<SkullType> skullType() {
        return Optional.ofNullable(this.type);
    }

    @Override
    public Optional<String> texture() {
        return Optional.ofNullable(this.texture);
    }

    @Override
    public Optional<String> owner() {
        return Optional.ofNullable(this.target);
    }

    @Override
    public <B extends ImpactorItemStackBuilder<B>> B asBuilder() {
        return null;
    }

    @Override
    public ItemStack toNative() {
        ItemStack result = super.toNative();
        // TODO - Satisfy skull based values

        return result;
    }
}
