package net.impactdev.impactor.minecraft.scoreboard.assigned;

import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.scoreboards.AssignedScoreboard;
import net.impactdev.impactor.api.scoreboards.Scoreboard;
import net.impactdev.impactor.api.scoreboards.ScoreboardRenderer;
import net.impactdev.impactor.api.scoreboards.lines.ScoreboardLine;
import net.impactdev.impactor.api.scoreboards.objectives.Objective;

import java.util.List;

public final class AssignedScoreboardImpl implements AssignedScoreboard {

    private final Scoreboard config;
    private final PlatformPlayer viewer;

    private final ScoreboardRenderer renderer;

    public AssignedScoreboardImpl(Scoreboard config, PlatformPlayer viewer) {
        this.config = config;
        this.viewer = viewer;

        this.renderer = config.renderer();
    }

    @Override
    public Scoreboard configuration() {
        return this.config;
    }

    @Override
    public PlatformPlayer viewer() {
        return this.viewer;
    }

    @Override
    public Objective.Displayed objective() {
        return null;
    }

    @Override
    public List<ScoreboardLine.Displayed> lines() {
        return null;
    }

    @Override
    public void open() {
        this.renderer.show(this);
    }

    @Override
    public void hide() {
        this.renderer.hide(this);
    }

    @Override
    public void destroy() {
        this.hide();

    }

}
