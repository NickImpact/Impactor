package net.impactdev.impactor.api.scoreboard.objective;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.scoreboard.objective.types.AnimatedObjective;
import net.impactdev.impactor.api.scoreboard.objective.types.ConstantObjective;
import net.impactdev.impactor.api.scoreboard.objective.types.ListeningObjective;
import net.impactdev.impactor.api.scoreboard.objective.types.RefreshingObjective;
import net.kyori.adventure.text.Component;

public interface ScoreboardObjective {

    Component getText();

    static ConstantObjective.ConstantObjectiveBuilder constant() {
        return Impactor.getInstance().getRegistry().createBuilder(ConstantObjective.ConstantObjectiveBuilder.class);
    }

    static RefreshingObjective.RefreshingObjectiveBuilder refreshing() {
        return Impactor.getInstance().getRegistry().createBuilder(RefreshingObjective.RefreshingObjectiveBuilder.class);
    }

    static ListeningObjective.ListeningObjectiveBuilder listening() {
        return Impactor.getInstance().getRegistry().createBuilder(ListeningObjective.ListeningObjectiveBuilder.class);
    }

    static AnimatedObjective.AnimatedObjectiveBuilder animated() {
        return Impactor.getInstance().getRegistry().createBuilder(AnimatedObjective.AnimatedObjectiveBuilder.class);
    }

}
