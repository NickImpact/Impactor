package net.impactdev.impactor.api.scoreboard.objective.types;

import net.impactdev.impactor.api.placeholders.PlaceholderSources;
import net.impactdev.impactor.api.scoreboard.objective.ScoreboardObjective;
import net.impactdev.impactor.api.utilities.Builder;
import net.kyori.adventure.text.Component;

public interface ConstantObjective extends ScoreboardObjective {

    interface ConstantObjectiveBuilder extends Builder<ConstantObjective, ConstantObjectiveBuilder> {

        ConstantObjectiveBuilder raw(String raw, PlaceholderSources sources);

        ConstantObjectiveBuilder text(Component text);

    }

}
