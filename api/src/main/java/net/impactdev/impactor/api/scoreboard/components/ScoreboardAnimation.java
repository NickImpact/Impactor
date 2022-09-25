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

package net.impactdev.impactor.api.scoreboard.components;

import net.impactdev.impactor.api.builders.Builder;
import net.impactdev.impactor.api.scoreboard.frames.ScoreboardFrame;
import net.impactdev.impactor.api.scoreboard.lines.types.AnimatedLine;
import net.impactdev.impactor.api.scoreboard.objective.types.AnimatedObjective;
import net.impactdev.impactor.api.utilities.lists.CircularLinkedList;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Represents a component of a scoreboard that is capable of running an animation. This is meant to cover
 * the fact that the animated objective and line both essentially run the same code, so this interface is
 * a bridge to both instances as a means to avoid duplicate code.
 *
 * @see AnimatedObjective
 * @see AnimatedLine
 */
public interface ScoreboardAnimation {

    /**
     * Represents the list of frames attached to the animation.
     *
     * @return The circularly linked set of frames
     */
    CircularLinkedList<ScoreboardFrame> getFrames();

    /**
     * Represents the timing configuration for the line, indicating how it should update
     * over time.
     *
     * @return The configuration for the timings of the line
     */
    TimeConfiguration getTimingConfig();

    /**
     * Indicates the amount of iterations a line must go through in order for the animation
     * to be able to transition to the next frame of the animation, if it has any additional frames.
     *
     * @return The amount of iterations required for a frame transition
     */
    int getIterationCount();

    interface AnimationBuilder<E extends ScoreboardComponent<?>> extends Builder<E> {

        /**
         * Appends a frame to the animation, placing it at the tail of the animation.
         *
         * @param frame The frame to append to the animation
         * @return The builder
         */
        AnimationBuilder<E> frame(ScoreboardFrame frame);

        /**
         * Appends a collection to the animation in the order the frames are queried from
         * the collection.
         *
         * @param frames The collection of frames to append to the animation
         * @return The builder
         */
        AnimationBuilder<E> frames(Collection<ScoreboardFrame> frames);

        /**
         * Sets the line to update after a set amount of ticks have elapsed. If this is used in conjunction
         * with the async protocol, the scheduler will apply a multiplication of 50, then assign it to a
         * unit interval of milliseconds. This is in an attempt to best replicate the actual game clock.
         *
         * @param ticks The amount of ticks
         * @return The updated builder
         */
        AnimationBuilder<E> interval(long ticks);

        /**
         * Sets the line to refresh/update its contents after waiting for the amount of time given by the value
         * and unit.
         *
         * @param interval The amount of time before a refresh/update in conjunction with the given unit
         * @param unit The unit indicating the measure of time for the interval
         * @return The updated builder
         */
        AnimationBuilder<E> interval(long interval, TimeUnit unit);

        /**
         * Indicates the number of times the active frame will update before being swapped with the next frame
         * in the queue. If the set of frames only contains 1 frame, then this value will be ignored.
         *
         * @param amount The amount of times to refresh the content.
         * @return The updated builder
         */
        AnimationBuilder<E> iterations(int amount);

        /**
         * Marks that the animation should run asynchronously, as well as attempt to update its frames
         * asynchronously. This will only update frames asynchronously if they meet the condition considered
         * by {@link ScoreboardFrame#shouldUpdateOnTick()}.
         *
         * @return The updated builder
         */
        AnimationBuilder<E> async();

    }

}
