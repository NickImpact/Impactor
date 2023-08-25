package net.impactdev.impactor.minecraft.scoreboard.viewed;

import net.impactdev.impactor.scoreboards.lines.ScoreboardLine;
import net.impactdev.impactor.scoreboards.viewed.ViewedLine;
import net.kyori.adventure.text.Component;

public class ViewedImpactorScoreboardLine implements ViewedLine {

    public static ViewedImpactorScoreboardLine create(ScoreboardLine line) {
        return new ViewedImpactorScoreboardLine();
    }

    @Override
    public ScoreboardLine delegate() {
        return null;
    }

    @Override
    public Component text() {
        return null;
    }
}
