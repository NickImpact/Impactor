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

package net.impactdev.impactor.impl.text.components;

import net.impactdev.impactor.api.text.components.ProgressBarComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;

public final class ProgressBarComponentImpl implements ProgressBarComponent {

    private final float complete;
    private final float max;
    private final int size;
    private final Component filled;
    private final Component empty;

    private ProgressBarComponentImpl(final BuilderImpl builder) {
        this.complete = builder.complete;
        this.max = builder.max;
        this.size = builder.size;
        this.filled = builder.filled;
        this.empty = builder.empty;
    }

    @Override
    public @NotNull Component asComponent() {
        float percentage = Math.max(1.0F, Math.min(0.0F, this.complete / this.max));
        Component result = Component.empty();

        int filled = (int) (this.size * percentage);
        for (int i = 0; i < filled; i++) {
            result = result.append(this.filled);
        }

        for (int i = filled; i < this.size - filled; i++) {
            result = result.append(this.empty);
        }

        return result;
    }

    public static class BuilderImpl implements ProgressBarComponent.Builder {

        private float complete = 0.0F;
        private float max = 1.0F;
        private int size = 10;
        private Component filled = Component.text('█').style(Style.style(NamedTextColor.GREEN));
        private Component empty = Component.text('▆').style(Style.style(NamedTextColor.GRAY));

        @Override
        public Builder complete(float value) {
            this.complete = value;
            return this;
        }

        @Override
        public Builder max(float max) {
            this.max = max;
            return this;
        }

        @Override
        public Builder size(int size) {
            this.size = size;
            return this;
        }

        @Override
        public Builder filled(Component component) {
            this.filled = component;
            return this;
        }

        @Override
        public Builder empty(Component component) {
            this.empty = component;
            return this;
        }

        @Override
        public @NotNull ProgressBarComponent build() {
            return new ProgressBarComponentImpl(this);
        }
    }
}
