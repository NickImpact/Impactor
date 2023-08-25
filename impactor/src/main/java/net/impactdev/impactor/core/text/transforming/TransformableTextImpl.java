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

package net.impactdev.impactor.core.text.transforming;

import net.impactdev.impactor.api.text.transforming.TransformableText;
import net.impactdev.impactor.api.text.transforming.transformers.TextTransformer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class TransformableTextImpl implements TransformableText {

    private final Supplier<Component> supplier;
    private final TextTransformer transformer;

    private TransformableTextImpl(TransformableTextImplBuilder builder) {
        this.supplier = builder.supplier;
        this.transformer = builder.transformer;
    }

    @Override
    public Supplier<Component> supplier() {
        return this.supplier;
    }

    @Override
    public TextTransformer transformer() {
        return this.transformer;
    }

    @Override
    public @NotNull Component asComponent() {
        Component result = this.transformer.transform(this.supplier.get());
        this.transformer.step();

        return result;
    }

    public static class TransformableTextImplBuilder implements TransformableTextBuilder {

        private Supplier<Component> supplier;
        private TextTransformer transformer;

        @Override
        public TransformableTextBuilder supplier(Supplier<Component> supplier) {
            this.supplier = supplier;
            return this;
        }

        @Override
        public TransformableTextBuilder transformer(TextTransformer transformer) {
            this.transformer = transformer;
            return this;
        }

        @Override
        public TransformableText build() {
            return new TransformableTextImpl(this);
        }
    }
}
