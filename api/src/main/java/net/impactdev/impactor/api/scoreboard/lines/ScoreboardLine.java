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

package net.impactdev.impactor.api.scoreboard.lines;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.scoreboard.components.ScoreboardComponent;
import net.impactdev.impactor.api.scoreboard.components.Updatable;
import net.impactdev.impactor.api.scoreboard.frames.ScoreboardFrame;
import net.impactdev.impactor.api.scoreboard.lines.types.AnimatedLine;
import net.impactdev.impactor.api.scoreboard.lines.types.ConstantLine;
import net.impactdev.impactor.api.scoreboard.lines.types.ListeningLine;
import net.impactdev.impactor.api.scoreboard.lines.types.RefreshingLine;
import net.kyori.adventure.text.Component;

/**
 * Represents a line on a scoreboard. This line is only ever queried one time, and makes no attempts at
 * refreshing the contents assigned to it.
 */
public interface ScoreboardLine extends ScoreboardComponent<ScoreboardLine> {

    /**
     * Fetches the text currently assigned to the line. In the case of an {@link Updatable} line, this
     * value can change between multiple calls.
     *
     * @return The currently displayed text component
     */
    Component getText();

    /**
     * Indicates the score applied to the line.
     *
     * @return The value of the score this line is represented by
     */
    int getScore();

    /**
     * Creates a constant line which represents an empty line. This line will have no displayable text,
     * and is meant to act as simply a spacer.
     *
     * @return A constant line with no displayable text
     */
    static ConstantLine empty() {
        return constant().text(Component.empty()).build();
    }

    /**
     * Begins construction of a line for a scoreboard that makes no attempt to update the text
     * associated with the line.
     *
     * @return A builder for a line that makes no attempts to update its contents
     */
    static ConstantLine.ConstantLineBuilder constant() {
        return Impactor.instance().builders().provide(ConstantLine.ConstantLineBuilder.class);
    }

    /**
     * Begins construction of a line that refreshes its contents over a set amount of time. Unlike an
     * animated line, this type of line only has one particular frame.
     *
     * @return A builder for a line that will refresh its text over an interval rate
     */
    static RefreshingLine.RefreshingLineBuilder refreshing() {
        return Impactor.instance().builders().provide(RefreshingLine.RefreshingLineBuilder.class);
    }

    /**
     * Begins construction of a line that has openings for multiple layers. In other words,
     * the line can feature a singular frame, or multiple types of frames that are cycled through
     * during its lifetime.
     *
     * @return A builder for a line that will cycle through a series of {@link ScoreboardFrame ScoreboardFrames}.
     */
    static AnimatedLine.AnimatedBuilder animated() {
        return Impactor.instance().builders().provide(AnimatedLine.AnimatedBuilder.class);
    }

    /**
     * Begins construction of a line that only updates based on an event handler's result. This allows for fine-tuned
     * updating of lines that are based on placeholders that might only update after an event rather than a
     * predictable rate. An example of this would be a player's rank via a permissions service.
     *
     * @return A builder for a line that listens to a particular event in order to update
     */
    static ListeningLine.ListeningBuilder listening() {
        return Impactor.instance().builders().provide(ListeningLine.ListeningBuilder.class);
    }

}
