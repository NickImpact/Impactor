package net.impactdev.impactor.api.scoreboard.lines.types;

import net.impactdev.impactor.api.scoreboard.components.Updatable;
import net.impactdev.impactor.api.scoreboard.frames.types.ListeningFrame;
import net.impactdev.impactor.api.scoreboard.lines.ScoreboardLine;
import net.impactdev.impactor.api.utilities.Builder;

public interface ListeningLine extends ScoreboardLine, Updatable {

    ListeningFrame.EventHandler<?> getEventHandler();

    interface ListeningBuilder extends LineBuilder<ListeningBuilder>, Builder<ListeningLine, ListeningBuilder> {

        ListeningBuilder content(ListeningFrame<?> frame);

    }

}
