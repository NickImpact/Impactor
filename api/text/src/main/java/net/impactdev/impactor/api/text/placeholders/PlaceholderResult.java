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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the result of a placeholder request. This is meant to wrap a component with a success flag,
 * indicating the overall success of the operation.
 *
 * @param result A Component representing the overall result of the request. This can be nullable to represent
 *               the default handling of invalid placeholder parsers (instances where they fail), or can be
 *               overwritten with a custom default result to inject the result instead.
 * @param successful If the request was valid and a proper result was returned.
 */
public record PlaceholderResult(@Nullable Component result, boolean successful) implements ComponentLike {

    /**
     * Generates a result built on the given result, with the success flag set to true.
     *
     * @param result The component to return
     * @return A result marked as successful
     */
    public static PlaceholderResult successful(final @NotNull Component result) {
        return new PlaceholderResult(result, true);
    }

    /**
     * Generates a result built on no custom result, with the success flag set to false.
     *
     * @return A result marked as failed
     */
    public static PlaceholderResult failed() {
        return new PlaceholderResult(null, false);
    }

    /**
     * Generates a result built on a custom override result, while still being considered a failed
     * parse attempt.
     *
     * @param result The component to return
     * @return A result marked as failed, with a custom result override
     */
    public static PlaceholderResult failed(final @NotNull Component result) {
        return new PlaceholderResult(result, false);
    }

    @Override
    public @NotNull Component asComponent() {
        return this.result != null ? this.result : Component.empty();
    }
}
