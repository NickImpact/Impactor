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

package net.impactdev.impactor.api.scoreboard.frames.types;

import net.impactdev.impactor.api.builders.Builder;
import net.impactdev.impactor.api.scoreboard.components.Updatable;
import net.impactdev.impactor.api.scoreboard.events.Bus;
import net.impactdev.impactor.api.scoreboard.frames.ScoreboardFrame;
import net.impactdev.impactor.api.utilities.context.Context;

import java.util.UUID;

/**
 * Creates a frame that is unique to the event bus of the particular platform. This frame will
 * only update via the event handler, if specified to update.
 *
 * @param <L> The event represented by this frame
 */
public interface ListeningFrame<L> extends ScoreboardFrame.UpdatableFrame {

    /**
     * A {@link Class} that resembles the type of the event for the frame
     *
     * @return A {@link Class} based on the event type
     */
    Class<L> getListenerType();

    /**
     * Represents the handler being used by this frame for listening to its target event.
     *
     * @return A handler for listening to the target event of this frame
     */
    EventHandler<L> getEventHandler();

    interface ListeningFrameBuilder<L> extends Builder<ListeningFrame<L>> {

        /**
         * Sets the frame to listen against events of the following type. This is normally set during
         * construction of the builder via {@link ScoreboardFrame#listening(Class)}, so you can avoid using
         * this method.
         *
         * @param event The event type this frame should represent
         * @param <E> A reference to the type of event
         * @return This builder
         */
        <E> ListeningFrameBuilder<E> type(Class<E> event);

        /**
         * Specifies the bus to be used by this frame
         *
         * @param bus The bus
         * @return This builder
         */
        ListeningFrameBuilder<L> bus(Bus<? super L> bus);

        /**
         * Specifies the text that should be translated per update cast by the event handler. This is also
         * parsed for the first instance should the event take a while to fire.
         *
         * @param raw The raw text to be translated per update
         * @return This builder
         */
        ListeningFrameBuilder<L> text(String raw);

        /**
         * Specifies the handler this frame will use to listen to events of its given event typing. This handler
         * should tell the updatable instance when to update for this frame to work correctly!
         *
         * @param handler The event handler
         * @return This builder
         */
        ListeningFrameBuilder<L> handler(EventHandler<L> handler);

        /**
         * Represents a list of sources that placeholders in the raw text will use for replacement
         *
         * @param context The set of sources
         * @return This builder
         */
        ListeningFrameBuilder<L> context(Context context);

    }

    /**
     * Represents a means of processing an event with the updatable instance the event should update if it
     * meets its criteria.
     *
     * @param <L> The type of event being processed
     */
    @FunctionalInterface
    interface EventHandler<L> {

        /**
         * Processes the event with the updatable instance this processor should update when the correct
         * criteria is met.
         *
         * @param updatable The updatable instance to update
         * @param assignee The player assigned to the scoreboard this line is listening to
         * @param event The event that was posted to the event bus
         * @return <code>true</code> if the line should update, false otherwise
         * @throws RuntimeException If any component of the processor fails
         */
        boolean process(Updatable updatable, UUID assignee, L event) throws RuntimeException;

    }

}
