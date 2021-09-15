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

package net.impactdev.impactor.api.scoreboard.lines;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.placeholders.PlaceholderSources;
import net.impactdev.impactor.api.scoreboard.components.Updatable;
import net.impactdev.impactor.api.scoreboard.lines.types.AnimatedLine;
import net.impactdev.impactor.api.scoreboard.lines.types.ListeningLine;
import net.impactdev.impactor.api.scoreboard.lines.types.RefreshingLine;
import net.impactdev.impactor.api.scoreboard.lines.types.ConstantLine;
import net.kyori.adventure.text.Component;

/**
 * Represents a line on a scoreboard. This line is only ever queried one time, and makes no attempts at
 * refreshing the contents assigned to it.
 */
public interface ScoreboardLine {

    /**
     * Fetches the text currently assigned to the line. In the case of an {@link Updatable} line, this
     * value can change between multiple calls.
     *
     * @return The currently displayed text component
     */
    Component getText();

    /**
     * Indicates the score applied to the line. This will help determine the order of this line in
     * a scoreboard.
     *
     * @return The value of the score this line is represented by
     */
    int getScore();

    /**
     * Begins construction of a line for a scoreboard that makes no attempt to update the text
     * associated with the line.
     *
     * @return A builder for a line that makes no attempts to update its contents
     */
    static ConstantLine.ConstantLineBuilder constant() {
        return Impactor.getInstance().getRegistry().createBuilder(ConstantLine.ConstantLineBuilder.class);
    }

    /**
     * Begins construction of a line that refreshes its contents over a set amount of time. Unlike an
     * animated line, this type of line only has one particular frame
     *
     * @return
     */
    static RefreshingLine.RefreshingLineBuilder refreshing() {
        return Impactor.getInstance().getRegistry().createBuilder(RefreshingLine.RefreshingLineBuilder.class);
    }

    static AnimatedLine.AnimatedBuilder animated() {
        return Impactor.getInstance().getRegistry().createBuilder(AnimatedLine.AnimatedBuilder.class);
    }

    static ListeningLine.ListeningBuilder listening() {
        return Impactor.getInstance().getRegistry().createBuilder(ListeningLine.ListeningBuilder.class);
    }

    interface LineBuilder<B extends LineBuilder<B>> {

        /**
         * Applies the score for the line to follow. This will be used for determining order of appearance
         * on a scoreboard, with higher values appearing above lines with lower values.
         *
         * @param score The score representing the line
         * @return The updated builder
         */
        B score(int score);

    }

}
