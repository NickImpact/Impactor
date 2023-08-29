package net.impactdev.impactor.minecraft.scoreboard.viewed;

import net.impactdev.impactor.scoreboards.lines.ScoreboardLine;
import net.impactdev.impactor.scoreboards.viewed.ViewedLine;
import net.kyori.adventure.text.Component;

public class ViewedImpactorScoreboardLine implements ViewedLine {

    private final ScoreboardLine delegate;

    private ViewedImpactorScoreboardLine(ScoreboardLine delegate) {
        this.delegate = delegate;
    }

    public static ViewedImpactorScoreboardLine create(ScoreboardLine line) {
        return new ViewedImpactorScoreboardLine(line);
    }

    @Override
    public ScoreboardLine delegate() {
        return this.delegate;
    }

    @Override
    public Component text() {
        return this.delegate.resolver().resolve(this.delegate);
    }

}
