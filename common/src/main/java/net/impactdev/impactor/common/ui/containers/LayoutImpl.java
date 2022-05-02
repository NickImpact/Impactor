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

package net.impactdev.impactor.common.ui.containers;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.impactdev.impactor.api.ui.containers.icons.Icon;
import net.impactdev.impactor.api.ui.containers.layouts.Layout;
import org.spongepowered.math.vector.Vector2i;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class LayoutImpl implements Layout {

    private final Map<Integer, Icon<?>> slots;
    private final Vector2i dimensions;

    private LayoutImpl(LayoutImplBuilder builder) {
        this.slots = builder.icons;
        this.dimensions = new Vector2i(9, builder.rows);
    }

    @Override
    public ImmutableMap<Integer, Icon<?>> elements() {
        return ImmutableMap.copyOf(this.slots);
    }

    @Override
    public Optional<Icon<?>> icon(int slot) {
        return Optional.ofNullable(this.slots.get(slot));
    }

    @Override
    public Vector2i dimensions() {
        return this.dimensions;
    }

    public static class LayoutImplBuilder implements LayoutBuilder {

        private int rows = 6;
        private final Map<Integer, Icon<?>> icons = Maps.newHashMap();

        @Override
        public LayoutBuilder size(int rows) {
            this.rows = rows;
            return this;
        }

        @Override
        public LayoutBuilder slot(Icon<?> icon, int slot) {
            this.icons.put(slot, icon);
            return this;
        }

        @Override
        public LayoutBuilder fill(Icon<?> icon) {
            for(int i = 1; i < (9 * this.rows); i++) {
                if(!this.icons.containsKey(i)) {
                    this.slot(icon, i);
                }
            }
            return this;
        }

        @Override
        public LayoutBuilder border(Icon<?> icon) {
            this.column(icon, 1);
            this.column(icon, 9);
            this.row(icon, 1);
            this.row(icon, this.rows);

            return this;
        }

        @Override
        public LayoutBuilder row(Icon<?> icon, int row) {
            Preconditions.checkArgument(row >= 1 && row <= 6, "Row outside boundaries");
            int start = 9 * (row - 1);
            for(int i = 0; i < 9; i++) {
                this.slot(icon, start + i);
            }

            return this;
        }

        @Override
        public LayoutBuilder column(Icon<?> icon, int column) {
            Preconditions.checkArgument(column >= 1 && column <= 9, "Column outside boundaries");

            for(int i = (column - 1); i < (9 * this.rows); i += 9) {
                this.slot(icon, i);
            }
            return this;
        }

        @Override
        public LayoutBuilder center(Icon<?> icon) {
            if(this.rows % 2 == 0) {
                this.slot(icon, (this.rows * 9) / 2 - 4);
                this.slot(icon, (this.rows * 9) / 2 + 5);
            } else {
                this.slot(icon, (this.rows * 9) / 2 + 1);
            }

            return this;
        }

        @Override
        public LayoutBuilder square(Icon<?> icon, int center, int radius, boolean hollow) {
            Vector2i size = Vector2i.from(radius);
            Vector2i offsets = Vector2i.from(center % 9, center / 9).sub(size);
            return this.rectangle(icon, size, offsets, hollow);
        }

        @Override
        public LayoutBuilder rectangle(Icon<?> icon, Vector2i size, Vector2i offset, boolean hollow) {
            for(int x = offset.x(); x < size.x() + offset.x(); x++) {
                this.slot(icon, x + (9 * offset.y()));
            }

            for(int x = offset.x(); x < size.x() + offset.x(); x++) {
                for(int y = offset.y() + 1; y < size.y() + offset.y() - 1; y++) {
                    if(hollow && x != offset.x() && x != size.x() + offset.x()) {
                        continue;
                    }

                    this.slot(icon, x + (9 * y));
                }
            }

            for(int x = offset.x(); x < size.x() + offset.x(); x++) {
                this.slot(icon, x + (9 * (size.y() + offset.y() - 1)));
            }

            return this;
        }

        @Override
        public LayoutBuilder custom(Consumer<LayoutBuilder> consumer) {
            consumer.accept(this);
            return this;
        }

        @Override
        public LayoutBuilder from(Layout input) {
            this.size(input.dimensions().y());
            input.elements().forEach((key, value) -> this.slot(value, key));
            return this;
        }

        @Override
        public Layout build() {
            return new LayoutImpl(this);
        }
    }
}
