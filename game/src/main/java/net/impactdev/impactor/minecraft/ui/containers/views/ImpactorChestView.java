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

package net.impactdev.impactor.minecraft.ui.containers.views;

import net.impactdev.impactor.api.ui.containers.layouts.ChestLayout;
import net.impactdev.impactor.api.ui.containers.views.ChestView;
import net.impactdev.impactor.api.utility.Context;
import net.impactdev.impactor.api.utility.printing.PrettyPrinter;
import net.impactdev.impactor.minecraft.ui.containers.views.builders.ImpactorBaseViewBuilder;
import net.impactdev.impactor.minecraft.ui.containers.views.layers.ImpactorView;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.checkerframework.common.value.qual.IntRange;
import org.spongepowered.math.vector.Vector2i;

public abstract class ImpactorChestView extends ImpactorView implements ChestView {

    private final ChestLayout layout;

    protected ImpactorChestView(ImpactorChestViewBuilder builder) {
        super(builder.namespace, builder.title, builder.readonly, builder.click, builder.close);
        this.layout = builder.layout;
    }

    @Override
    public ChestLayout layout() {
        return this.layout;
    }

    @Override
    public @IntRange(from = 1, to = 6) int rows() {
        return this.layout().dimensions().y();
    }

    @Override
    public void refresh(Vector2i dimensions, Vector2i offsets) {

    }

    protected void writeException(Context context) {
        PrettyPrinter printer = new PrettyPrinter(80);
        printer.newline().add("Exception during Inventory Action").newline().hr();
        printer.add("Namespace: " + this.namespace);
        printer.add("Context:");
        printer.kv("Title", PlainTextComponentSerializer.plainText().serialize(this.title));
        printer.kv("Read Only", this.readonly);
        context.print(printer);
    }

    public static abstract class ImpactorChestViewBuilder extends ImpactorBaseViewBuilder<ChestViewBuilder> implements ChestViewBuilder {

        private ChestLayout layout;

        @Override
        public ChestViewBuilder layout(ChestLayout layout) {
            this.layout = layout;
            return this;
        }

        @Override
        public ChestViewBuilder from(ChestView parent) {
            return this.provider(parent.namespace())
                    .title(parent.title())
                    .layout(parent.layout())
                    .readonly(parent.readonly())
                    .onClick(((ImpactorChestView) parent).click)
                    .onClose(((ImpactorChestView) parent).close);
        }

    }

}
