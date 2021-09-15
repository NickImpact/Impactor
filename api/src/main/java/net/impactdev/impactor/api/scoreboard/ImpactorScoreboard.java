package net.impactdev.impactor.api.scoreboard;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.scoreboard.lines.ScoreboardLine;
import net.impactdev.impactor.api.scoreboard.objective.ScoreboardObjective;
import net.impactdev.impactor.api.utilities.Builder;

import java.util.List;
import java.util.UUID;

public interface ImpactorScoreboard {

    ScoreboardObjective getTitle();

    List<ScoreboardLine> getLines();

    void applyFor(UUID user);

    static ScoreboardBuilder builder() {
        return Impactor.getInstance().getRegistry().createBuilder(ScoreboardBuilder.class);
    }

    interface ScoreboardBuilder extends Builder<ImpactorScoreboard, ScoreboardBuilder> {

        ScoreboardBuilder objective(ScoreboardObjective objective);

        ScoreboardBuilder line(ScoreboardLine line);

        ScoreboardBuilder lines(ScoreboardLine... lines);

        ScoreboardBuilder lines(Iterable<ScoreboardLine> lines);

    }

}
