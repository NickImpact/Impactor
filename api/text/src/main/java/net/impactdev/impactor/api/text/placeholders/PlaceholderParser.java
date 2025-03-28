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

package net.impactdev.impactor.api.text.placeholders;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.pointer.Pointers;
import org.jetbrains.annotations.NotNull;

public interface PlaceholderParser {

    /**
     * Parses a placeholder given a target audience and, where necessary, additional context. Both elements
     * could be empty and even ignored, but are supplied purely to allow certain elements to parse data
     * in a streamlined fashion.
     *
     * <p>Where parsing should fail, a result should indicate this by resolving an empty result, and with a
     * status of false via {@link PlaceholderResult#result()}.
     *
     * @param audience The audience this placeholder is parsing relative to
     * @param context Additional sets of context which might be useful for helping parse a particular placeholder
     * @return A result indicating the processing of this particular placeholder
     */
    PlaceholderResult parse(final @NotNull Audience audience, final @NotNull Pointers context);

}
