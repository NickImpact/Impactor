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

package net.impactdev.impactor.api.scoreboard.lines.types;

import net.impactdev.impactor.api.builders.Builder;
import net.impactdev.impactor.api.scoreboard.lines.ScoreboardLine;
import net.impactdev.impactor.api.utilities.context.Context;
import net.kyori.adventure.text.Component;

/**
 * Represents a line on a scoreboard that makes no attempt to ever update. This line uses a component
 * that is either available at construction, or parsed when a player is assigned to the line.
 */
public interface ConstantLine extends ScoreboardLine {

    interface ConstantLineBuilder extends Builder<ConstantLine> {

        /**
         * Creates a line that is lazily constructed, using raw input as its constant text
         * that is parsed once and only once upon player assignment.
         *
         * @param raw The text to parse when assigned to a player
         * @return The builder
         */
        default ConstantLineBuilder text(String raw) {
            return this.text(raw, Context.empty());
        }

        /**
         * Creates a line that is lazily constructed, using raw input as its constant text
         * that is parsed once and only once upon player assignment. The additional
         * sources parameter allows for placeholder parsing outside of just the viewing player,
         * should another source for a placeholder be necessary.
         *
         * @param raw The text to parse when assigned to a player
         * @param context A set of context that can aid in placeholder parsing.
         * @return The builder
         */
        ConstantLineBuilder text(String raw, Context context);

        /**
         * Creates a line that will do no parsing at all, but rather use the already generated
         * component as its viewable text.
         *
         * @param text The text the line should display
         * @return The builder
         */
        ConstantLineBuilder text(Component text);

    }

}
