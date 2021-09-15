/*
 * This file is part of Impactor, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018-2021 NickImpact
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

package net.impactdev.impactor.sponge.text.processors.gradients;

import net.impactdev.impactor.api.utilities.Builder;
import net.impactdev.impactor.sponge.text.processors.ComponentProcessor;
import net.kyori.adventure.text.format.TextColor;

import java.util.Arrays;
import java.util.LinkedList;

public abstract class GradientProcessor<T> implements ComponentProcessor<T> {

    protected final LinkedList<TextColor> colors;

    protected GradientProcessor(GradientProcessorBuilder<? extends GradientProcessor<T>> builder) {
        this.colors = builder.colors;
    }

    public static abstract class GradientProcessorBuilder<T extends GradientProcessor<?>> implements Builder<T, GradientProcessorBuilder<T>> {

        protected final LinkedList<TextColor> colors = new LinkedList<>();

        public GradientProcessorBuilder<T> colors(TextColor... colors) {
            this.colors.addAll(Arrays.asList(colors));
            return this;
        }

    }

}
