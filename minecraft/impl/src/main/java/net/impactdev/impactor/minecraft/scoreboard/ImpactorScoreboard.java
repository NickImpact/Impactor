package net.impactdev.impactor.minecraft.scoreboard;

import net.impactdev.impactor.scoreboards.Scoreboard;
import net.impactdev.impactor.scoreboards.ScoreboardImplementation;
import net.impactdev.impactor.scoreboards.lines.ScoreboardLine;
import net.impactdev.impactor.scoreboards.objectives.Objective;

import java.util.ArrayList;
import java.util.List;

public final class ImpactorScoreboard implements Scoreboard {

    private final ScoreboardImplementation implementation;
    private final Objective objective;
    private final List<ScoreboardLine> lines;

    private ImpactorScoreboard(ImpactorScoreboardBuilder builder) {
        this.implementation = builder.implementation;
        this.objective = builder.objective;
        this.lines = builder.lines;
    }

    @Override
    public ScoreboardImplementation implementation() {
        return this.implementation;
    }

    @Override
    public Objective objective() {
        return this.objective;
    }

    @Override
    public List<ScoreboardLine> lines() {
        return this.lines;
    }

    public static class ImpactorScoreboardBuilder implements ScoreboardBuilder {

        private ScoreboardImplementation implementation;
        private Objective objective;
        private final List<ScoreboardLine> lines = new ArrayList<>();

        @Override
        public ScoreboardBuilder implementation(ScoreboardImplementation implementation) {
            this.implementation = implementation;
            return this;
        }

        @Override
        public ScoreboardBuilder objective(Objective objective) {
            this.objective = objective;
            return this;
        }

        @Override
        public ScoreboardBuilder line(ScoreboardLine line) {
            this.lines.add(line);
            return this;
        }

        @Override
        public Scoreboard build() {
            return new ImpactorScoreboard(this);
        }
    }

}
