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
import net.impactdev.impactor.api.scoreboard.components.Updatable;
import net.impactdev.impactor.api.scoreboard.frames.types.ListeningFrame;
import net.impactdev.impactor.api.scoreboard.lines.ScoreboardLine;

/**
 * A listening line is a line capable of updating its viewable text based on the execution of an
 * event. The line is composed of an event handler, which is registered to its provided event bus
 * at time of initialization. This is processed through the child {@link ListeningFrame}, which
 * holds the information such as the event processor, its bus, the text to parse, and any
 * additional sources outside of the viewing player.
 */
public interface ListeningLine extends ScoreboardLine, Updatable {

    /**
     * Represents the event handler assigned to the frame of this line. This is simply
     * provided for convenience should it be necessary to be fetched.
     *
     * @return The event handler for the line
     */
    ListeningFrame.EventHandler<?> getEventHandler();

    interface ListeningBuilder extends Builder<ListeningLine> {

        /**
         * Attaches a listening frame that will belong to the line. This will control the viewable
         * text of the line, and how exactly the line will update when its required condition
         * is met.
         *
         * @param frame The frame to associate with the listening line
         * @return The builder
         */
        ListeningBuilder content(ListeningFrame<?> frame);

    }

}
