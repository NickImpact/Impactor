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

package net.impactdev.impactor.sponge.ui.containers.icons;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import net.impactdev.impactor.api.ui.icons.ClickProcessor;
import net.impactdev.impactor.api.ui.icons.DisplayProvider;
import net.impactdev.impactor.api.ui.icons.Icon;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Set;

public class SpongeIcon implements Icon<ItemStack> {

    private final DisplayProvider<ItemStack> display;
    private final Set<ClickProcessor> listeners;

    private SpongeIcon(SpongeIconBuilder builder) {
        this.display = builder.display;
        this.listeners = builder.listeners;
    }

    @Override
    @NotNull
    public DisplayProvider<ItemStack> display() {
        return this.display;
    }

    @Override
    public Set<ClickProcessor> listeners() {
        return this.listeners;
    }

    public static class SpongeIconBuilder implements Icon.IconBuilder<ItemStack> {

        private DisplayProvider<ItemStack> display;
        private final Set<ClickProcessor> listeners = Sets.newLinkedHashSet();

        @Override
        public IconBuilder<ItemStack> display(DisplayProvider<ItemStack> display) {
            this.display = display;
            return this;
        }

        @Override
        public IconBuilder<ItemStack> listener(ClickProcessor processor) {
            this.listeners.add(processor);
            return this;
        }

        @Override
        public IconBuilder<ItemStack> from(Icon<ItemStack> input) {
            return this;
        }

        @Override
        public Icon<ItemStack> build() {
            Preconditions.checkNotNull(this.display);
            return new SpongeIcon(this);
        }
    }

}
