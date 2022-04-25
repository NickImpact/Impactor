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

package net.impactdev.impactor.forge.ui.container.icons;

import com.google.common.collect.Sets;
import net.impactdev.impactor.api.ui.containers.icons.ClickProcessor;
import net.impactdev.impactor.api.ui.containers.icons.DisplayProvider;
import net.impactdev.impactor.api.ui.containers.icons.Icon;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.Supplier;

public class ForgeIcon implements Icon<ItemStack> {

    private final ImpactorButton delegate;

    private ForgeIcon(ForgeIconBuilder builder) {
        this.delegate = ImpactorButton.builder()
                .display(builder.display)
                .processors(builder.listeners)
                .build();
    }

    public ImpactorButton getDelegate() {
        return delegate;
    }

    @NotNull
    @Override
    public DisplayProvider<ItemStack> display() {
        return this.delegate.provider();
    }

    @Override
    public Set<ClickProcessor> listeners() {
        return this.delegate.listeners();
    }

    @Override
    public Icon<ItemStack> listener(ClickProcessor processor) {
        this.delegate.listeners().add(processor);
        return this;
    }

    public static class ForgeIconBuilder implements IconBuilder<ItemStack> {

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
        public IconBuilder<ItemStack> from(Icon<?> parent) {
            return this;
        }

        @Override
        public <E> Binding<ItemStack, E> build(Supplier<E> binding) {
            return null;
        }

        @Override
        public Icon<ItemStack> build() {
            return new ForgeIcon(this);
        }
    }
}
