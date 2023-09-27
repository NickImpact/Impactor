package net.impactdev.impactor.minecraft.scoreboard.relative;

import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.scoreboards.Scoreboard;
import net.impactdev.impactor.api.scoreboards.relative.PlayerScoreboard;
import net.impactdev.impactor.api.scoreboards.relative.RelativeObjective;
import net.impactdev.impactor.api.scoreboards.relative.RelativeScoreboardLine;

import java.util.List;

public class ImpactorPlayerScoreboard implements PlayerScoreboard {
    @Override
    public Scoreboard configuration() {
        return null;
    }

    @Override
    public PlatformPlayer viewer() {
        return null;
    }

    @Override
    public RelativeObjective objective() {
        return null;
    }

    @Override
    public List<RelativeScoreboardLine> lines() {
        return null;
    }

    @Override
    public void open() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void destroy() {

    }
}
