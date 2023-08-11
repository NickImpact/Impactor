package net.impactdev.impactor.minecraft.scoreboard;

import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.scoreboards.Scoreboard;
import net.impactdev.impactor.scoreboards.ScoreboardImplementation;
import net.impactdev.impactor.scoreboards.lines.ScoreboardLine;
import net.impactdev.impactor.scoreboards.objectives.ScoreboardObjective;

import java.util.List;

public final class IndividualScoreboard implements Scoreboard {

    private final PlatformPlayer viewer;
    private final ScoreboardImplementation implementation;

    public IndividualScoreboard(PlatformPlayer viewer, Scoreboard template) {
        this.viewer = viewer;
        this.implementation = template.implementation();
    }

    @Override
    public ScoreboardImplementation implementation() {
        return this.implementation;
    }

    @Override
    public ScoreboardObjective objective() {
        return null;
    }

    @Override
    public List<ScoreboardLine> lines() {
        return null;
    }

    @Override
    public void show(PlatformPlayer player) {

    }

    @Override
    public void hide(PlatformPlayer player) {

    }

}
