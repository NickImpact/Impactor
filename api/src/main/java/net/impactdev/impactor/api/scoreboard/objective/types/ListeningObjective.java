package net.impactdev.impactor.api.scoreboard.objective.types;

import net.impactdev.impactor.api.scoreboard.components.Updatable;
import net.impactdev.impactor.api.scoreboard.frames.types.ListeningFrame;
import net.impactdev.impactor.api.scoreboard.objective.ScoreboardObjective;
import net.impactdev.impactor.api.utilities.Builder;

public interface ListeningObjective extends ScoreboardObjective, Updatable {

    ListeningFrame.EventHandler<?> getEventHandler();

    interface ListeningObjectiveBuilder extends Builder<ListeningObjective, ListeningObjectiveBuilder> {

        ListeningObjectiveBuilder frame(ListeningFrame<?> frame);

    }

}
