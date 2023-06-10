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

package net.impactdev.impactor.minecraft.ui.containers.layouts;

import com.google.common.base.Preconditions;
import net.impactdev.impactor.api.ui.containers.Icon;
import net.impactdev.impactor.api.ui.containers.Layout;
import net.impactdev.impactor.api.ui.containers.layouts.ChestLayout;
import net.impactdev.impactor.minecraft.ui.containers.ImpactorLayout;
import org.spongepowered.math.vector.Vector2i;

import java.util.function.Consumer;

public class ImpactorChestLayout extends ImpactorLayout implements ChestLayout {

    private final Vector2i dimensions;

    protected ImpactorChestLayout(final ImpactorChestLayoutBuilder builder) {
        super(builder.icons());
        this.dimensions = Vector2i.from(9, builder.rows);
    }

    @Override
    public Vector2i dimensions() {
        return this.dimensions;
    }

    public static class ImpactorChestLayoutBuilder extends IconHolder implements ChestLayoutBuilder {

        private int rows = 6;

        @Override
        public ChestLayoutBuilder size(int rows) {
            this.rows = rows;
            return this;
        }

        @Override
        public ChestLayoutBuilder slot(Icon icon, int slot) {
            this.icons.put(slot, icon);
            return this;
        }

        @Override
        public ChestLayoutBuilder fill(Icon icon) {
            for(int i = 1; i < (9 * this.rows); i++) {
                if(!this.icons.containsKey(i)) {
                    this.slot(icon, i);
                }
            }
            return this;
        }

        @Override
        public ChestLayoutBuilder border(Icon icon) {
            this.column(icon, 1);
            this.column(icon, 9);
            this.row(icon, 1);
            this.row(icon, this.rows);

            return this;
        }

        @Override
        public ChestLayoutBuilder row(Icon icon, int row) {
            Preconditions.checkArgument(row >= 1 && row <= 6, "Row outside boundaries");
            int start = 9 * (row - 1);
            for(int i = 0; i < 9; i++) {
                this.slot(icon, start + i);
            }

            return this;
        }

        @Override
        public ChestLayoutBuilder column(Icon icon, int column) {
            Preconditions.checkArgument(column >= 1 && column <= 9, "Column outside boundaries");

            for(int i = (column - 1); i < (9 * this.rows); i += 9) {
                this.slot(icon, i);
            }
            return this;
        }

        @Override
        public ChestLayoutBuilder center(Icon icon) {
            if(this.rows % 2 == 0) {
                this.slot(icon, (this.rows * 9) / 2 - 5);
                this.slot(icon, (this.rows * 9) / 2 + 4);
            } else {
                this.slot(icon, (this.rows * 9) / 2);
            }

            return this;
        }

        @Override
        public ChestLayoutBuilder square(Icon icon, int center, int radius, boolean hollow) {
            Vector2i size = Vector2i.from(radius);
            Vector2i offsets = Vector2i.from(center % 9, center / 9).sub(size);
            return this.rectangle(icon, size, offsets, hollow);
        }

        @Override
        public ChestLayoutBuilder rectangle(Icon icon, Vector2i size, Vector2i offset, boolean hollow) {
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
        public ChestLayoutBuilder consume(Consumer<ChestLayoutBuilder> consumer) {
            consumer.accept(this);
            return this;
        }

        @Override
        public ChestLayoutBuilder from(Layout layout) {
            return this;
        }

        @Override
        public ChestLayout build() {
            return new ImpactorChestLayout(this);
        }
    }
}
