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

package net.impactdev.impactor.game.ui.containers;

import com.google.common.base.Preconditions;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import io.leangen.geantyref.TypeToken;
import net.impactdev.impactor.api.items.ImpactorItemStack;
import net.impactdev.impactor.api.ui.containers.Icon;
import net.impactdev.impactor.api.ui.containers.processors.ClickProcessor;
import net.impactdev.impactor.api.utilities.context.Context;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;

public class ImpactorIcon implements Icon {

    private final Context context;
    private final Supplier<ImpactorItemStack> provider;
    private final Set<ClickProcessor> processors;
    private final boolean refreshable;

    private ImpactorIcon(ImpactorIconBuilder builder) {
        this.context = builder.context;
        this.provider = builder.provider;
        this.processors = builder.processors;
        this.refreshable = builder.refreshable;

    }

    @Override
    public @NotNull Supplier<ImpactorItemStack> display() {
        return this.provider;
    }

    @Override
    public @NotNull Context context() {
        return this.context;
    }

    @Override
    public @NotNull Set<ClickProcessor> listeners() {
        return ImmutableSet.copyOf(this.processors);
    }

    @Override
    public Icon listener(ClickProcessor processor) {
        this.processors.add(processor);
        return this;
    }

    @Override
    public boolean refreshable() {
        return this.refreshable;
    }

    public static class ImpactorIconBuilder implements IconBuilder {

        private final Context context = Context.empty();
        private Supplier<ImpactorItemStack> provider;
        private final Set<ClickProcessor> processors = new LinkedHashSet<>();
        private boolean refreshable = true;

        @Override
        public IconBuilder display(Supplier<ImpactorItemStack> display) {
            this.provider = display;
            return this;
        }

        @Override
        public IconBuilder listener(ClickProcessor processor) {
            this.processors.add(processor);
            return this;
        }

        @Override
        public <T> IconBuilder append(TypeToken<T> key, T value) {
            this.context.append(key, value);
            return this;
        }

        @Override
        public IconBuilder constant() {
            this.refreshable = false;
            return this;
        }

        @Override
        public IconBuilder from(Icon parent) {
            return this;
        }

        @Override
        public Icon build() {
            Preconditions.checkNotNull(this.provider, "Display provider was null");

            this.provider = this.refreshable ? this.provider : Suppliers.memoize(this.provider::get);
            return new ImpactorIcon(this);
        }

    }
}
