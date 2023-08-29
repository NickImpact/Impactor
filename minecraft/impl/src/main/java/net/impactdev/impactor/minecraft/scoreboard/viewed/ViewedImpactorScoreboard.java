package net.impactdev.impactor.minecraft.scoreboard.viewed;

import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.scoreboards.Scoreboard;
import net.impactdev.impactor.scoreboards.viewed.ViewedLine;
import net.impactdev.impactor.scoreboards.viewed.ViewedObjective;
import net.impactdev.impactor.scoreboards.viewed.ViewedScoreboard;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public final class ViewedImpactorScoreboard implements ViewedScoreboard {

    private final PlatformPlayer viewer;

    private final Scoreboard delegate;
    private final ViewedObjective objective;
    private final List<ViewedLine> lines;

    public ViewedImpactorScoreboard(Scoreboard delegate, PlatformPlayer viewer) {
        this.viewer = viewer;
        this.delegate = delegate;

        this.objective = ViewedImpactorObjective.create(this.delegate.objective());
        this.lines = delegate.lines()
                .stream()
                .map(ViewedImpactorScoreboardLine::create)
                .collect(Collectors.toList());
    }

    @Override
    public Scoreboard delegate() {
        return this.delegate;
    }

    @Override
    public PlatformPlayer viewer() {
        return this.viewer;
    }

    @Override
    public ViewedObjective objective() {
        return this.objective;
    }

    @Override
    public List<ViewedLine> lines() {
        return this.lines;
    }

    @Override
    public void open() {
        this.delegate.implementation().show(this.viewer(), this.delegate());
    }

    @Override
    public void hide() {
        this.delegate.implementation().hide(this.viewer(), this.delegate());
    }

    @Override
    public void destroy() {
        this.hide();
        this.objective.delegate().resolver().shutdown();
        this.lines.forEach(line -> line.delegate().resolver().shutdown());
    }

    public static final class ViewedScoreboardFactory implements Factory {

        @Override
        public ViewedScoreboard create(@NotNull Scoreboard parent, @NotNull PlatformPlayer viewer) {
            return new ViewedImpactorScoreboard(parent, viewer);
        }

    }

}
