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
