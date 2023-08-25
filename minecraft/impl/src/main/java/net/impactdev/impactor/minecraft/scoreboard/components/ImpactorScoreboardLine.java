package net.impactdev.impactor.minecraft.scoreboard.components;

import net.impactdev.impactor.scoreboards.lines.ScoreboardLine;

public final class ImpactorScoreboardLine extends ImpactorScoreboardDisplayable implements ScoreboardLine {

    private int score;

    @Override
    public int score() {
        return this.score;
    }

    @Override
    public void score(int score) {
        this.score = score;
    }

}
