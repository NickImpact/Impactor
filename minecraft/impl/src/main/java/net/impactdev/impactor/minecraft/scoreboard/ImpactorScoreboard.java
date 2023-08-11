package net.impactdev.impactor.minecraft.scoreboard;

import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.scoreboards.Scoreboard;
import net.impactdev.impactor.scoreboards.ScoreboardImplementation;
import net.impactdev.impactor.scoreboards.lines.ScoreboardLine;
import net.impactdev.impactor.scoreboards.objectives.ScoreboardObjective;

import java.util.ArrayList;
import java.util.List;

public final class ImpactorScoreboard implements Scoreboard {

    private final ScoreboardImplementation implementation;
    private final ScoreboardObjective objective;
    private final List<ScoreboardLine> lines = new ArrayList<>();

    public ImpactorScoreboard() {
        this.implementation = null;
        this.objective = null;
    }

    @Override
    public ScoreboardImplementation implementation() {
        return this.implementation;
    }

    @Override
    public ScoreboardObjective objective() {
        return this.objective;
    }

    @Override
    public List<ScoreboardLine> lines() {
        return this.lines;
    }

    @Override
    public void show(PlatformPlayer player) {
        this.implementation.show(player, this);
    }

    @Override
    public void hide(PlatformPlayer player) {
        this.implementation.hide(player, this);
    }
}
