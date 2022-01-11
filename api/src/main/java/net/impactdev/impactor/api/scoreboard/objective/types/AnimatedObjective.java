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

package net.impactdev.impactor.api.scoreboard.objective.types;

import net.impactdev.impactor.api.scoreboard.components.TimeConfiguration;
import net.impactdev.impactor.api.scoreboard.components.Updatable;
import net.impactdev.impactor.api.scoreboard.frames.ScoreboardFrame;
import net.impactdev.impactor.api.scoreboard.lines.types.AnimatedLine;
import net.impactdev.impactor.api.scoreboard.objective.ScoreboardObjective;
import net.impactdev.impactor.api.utilities.Builder;
import net.impactdev.impactor.api.utilities.lists.CircularLinkedList;

import java.util.concurrent.TimeUnit;

public interface AnimatedObjective extends ScoreboardObjective, Updatable {

    CircularLinkedList<ScoreboardFrame> getFrames();

    TimeConfiguration getTimingConfig();

    int getUpdateAmount();

    interface AnimatedObjectiveBuilder extends Builder<AnimatedObjective, AnimatedObjectiveBuilder> {

        AnimatedObjectiveBuilder frame(ScoreboardFrame frame);

        AnimatedObjectiveBuilder frames(Iterable<ScoreboardFrame> frames);

        /**
         * Sets the line to update after a set amount of ticks have elapsed. If this is used in conjunction
         * with the async protocol, the scheduler will apply a multiplication of 50, then assign it to a
         * unit interval of milliseconds. This is in an attempt to best replicate the actual game clock.
         *
         * @param ticks The amount of ticks
         * @return The updated builder
         */
        AnimatedObjectiveBuilder interval(long ticks);

        /**
         * Sets the line to refresh/update its contents after waiting for the amount of time given by the value
         * and unit.
         *
         * @param interval The amount of time before a refresh/update in conjunction with the given unit
         * @param unit The unit indicating the measure of time for the interval
         * @return The updated builder
         */
        AnimatedObjectiveBuilder interval(long interval, TimeUnit unit);

        /**
         * Indicates the number of times the active frame will update before being swapped with the next frame
         * in the queue. If the set of frames only contains 1 frame, then this value will be ignored.
         *
         * @param amount The amount of times to refresh the content.
         * @return The updated builder
         */
        AnimatedObjectiveBuilder updates(int amount);

        AnimatedObjectiveBuilder async();

    }

}
