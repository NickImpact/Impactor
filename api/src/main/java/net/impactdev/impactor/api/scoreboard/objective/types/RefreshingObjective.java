package net.impactdev.impactor.api.scoreboard.objective.types;

import net.impactdev.impactor.api.placeholders.PlaceholderSources;
import net.impactdev.impactor.api.scoreboard.components.TimeConfiguration;
import net.impactdev.impactor.api.scoreboard.components.Updatable;
import net.impactdev.impactor.api.scoreboard.objective.ScoreboardObjective;
import net.impactdev.impactor.api.utilities.Builder;

import java.util.concurrent.TimeUnit;

public interface RefreshingObjective extends ScoreboardObjective, Updatable {

    TimeConfiguration getTimingConfig();

    interface RefreshingObjectiveBuilder extends Builder<RefreshingObjective, RefreshingObjectiveBuilder> {

        RefreshingObjectiveBuilder text(String raw);

        RefreshingObjectiveBuilder rate(long ticks);

        RefreshingObjectiveBuilder rate(long duration, TimeUnit unit);

        RefreshingObjectiveBuilder async();

        RefreshingObjectiveBuilder sources(PlaceholderSources sources);

    }

}
