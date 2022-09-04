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

package net.impactdev.impactor.api.items.types;

import net.impactdev.impactor.api.Impactor;
import net.kyori.adventure.key.Key;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ItemType {

    private final Key key;

    public ItemType(Key key) {
        this.key = key;
    }

    public static ItemType from(Key key) {
        return Impactor.instance().factories().provide(Factory.class).from(key);
    }

    public static ItemType from(@NotNull @Subst("minecraft") @Pattern("[a-z0-9_\\-.]+") final String root, @NotNull @Subst("air") @Pattern("[a-z0-9_\\-./]+") final String location) {
        return Impactor.instance().factories().provide(Factory.class).from(Key.key(root, location));
    }

    public Key key() {
        return this.key;
    }

    public Optional<Item> minecraft() {
        Registry<Item> registry = Registry.ITEM;
        ResourceLocation location = new ResourceLocation(this.key.namespace() + ":" + this.key.value());
        return registry.getOptional(location);
    }

    public interface Factory {

        ItemType from(Key key);

    }

}
