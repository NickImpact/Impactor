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

package net.impactdev.impactor.api.text.components;

import net.impactdev.impactor.api.core.ServiceProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;

public interface ProgressBarComponent extends ComponentLike {

    static Builder builder() {
        return ServiceProvider.instance().provide(Builder.class);
    }

    interface Builder {

        /**
         * The amount of progress that should be complete within the progress bar. This is meant to scale
         * against the max value (default 100).
         *
         * @param value The value indicating the completeness of the overall progress
         * @return This builder
         */
        Builder complete(float value);

        /**
         * Represents the max value for the progress overall. This will ultimately be used as the denominator
         * to the given complete value. Default will be 100.
         *
         * @param max The amount needed to reach a total of 100% overall.
         * @return This builder
         */
        Builder max(float max);

        /**
         * The amount of characters that should make up the overall progress bar. The more characters, the better
         * certain values can be displayed. For instance, 15% on a size of 5 might not even show progress against
         * a single character, whereas a size of 10 would show at least one.
         *
         * @param size The amount of characters that'll make up the overall bar
         * @return This builder
         */
        Builder size(int size);

        /**
         * Represents the component that should be used to display completed progress for the progress bar.
         * Ideally, the component is composed of a single character, which whatever styling you see fit.
         *
         * @param component The component that will represent a section of completed progress
         * @return This builder
         */
        Builder filled(Component component);

        /**
         * Represents the component that should be used to display incomplete progress for the progress bar.
         * Ideally, the component is composed of a single character, which whatever styling you see fit.
         *
         * @param component The component that will represent a section of incomplete progress
         * @return This builder
         */
        Builder empty(Component component);

        @NotNull ProgressBarComponent build();
    }
}
