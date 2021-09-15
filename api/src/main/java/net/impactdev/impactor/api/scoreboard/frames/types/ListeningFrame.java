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

package net.impactdev.impactor.api.scoreboard.frames.types;

import io.leangen.geantyref.TypeToken;
import net.impactdev.impactor.api.placeholders.PlaceholderSources;
import net.impactdev.impactor.api.scoreboard.components.Updatable;
import net.impactdev.impactor.api.scoreboard.events.Bus;
import net.impactdev.impactor.api.scoreboard.frames.ScoreboardFrame;
import net.impactdev.impactor.api.scoreboard.lines.ScoreboardLine;
import net.impactdev.impactor.api.utilities.Builder;

/**
 * Creates a frame that is unique to the event bus of the particular platform.
 *
 * @param <L>
 */
public interface ListeningFrame<L> extends ScoreboardFrame.UpdatableFrame {

    TypeToken<L> getListenerType();

    EventHandler<L> getEventHandler();

    interface ListeningFrameBuilder<L> extends Builder<ListeningFrame<L>, ListeningFrameBuilder<L>> {

        /**
         * Sets the frame to listen against events of the following type. This is normally set during
         * construction of the builder via {@link ScoreboardFrame#listening(TypeToken)}, so you can avoid using
         * this method.
         *
         * @param event
         * @param <E>
         * @return
         */
        <E> ListeningFrameBuilder<E> type(TypeToken<E> event);

        ListeningFrameBuilder<L> bus(Bus<? super L> bus);

        ListeningFrameBuilder<L> text(String raw);

        ListeningFrameBuilder<L> handler(EventHandler<L> handler);

        ListeningFrameBuilder<L> sources(PlaceholderSources sources);

    }

    @FunctionalInterface
    interface EventHandler<L> {

        void process(Updatable updatable, L event) throws RuntimeException;

    }

}
