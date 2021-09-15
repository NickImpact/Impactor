package net.impactdev.impactor.sponge.scoreboard;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import net.impactdev.impactor.api.scoreboard.ImpactorScoreboard;
import net.impactdev.impactor.api.scoreboard.components.Updatable;
import net.impactdev.impactor.api.scoreboard.lines.ScoreboardLine;
import net.impactdev.impactor.api.scoreboard.objective.ScoreboardObjective;
import net.impactdev.impactor.sponge.scoreboard.lines.AbstractSpongeSBLine;
import net.impactdev.impactor.sponge.scoreboard.objective.AbstractSpongeObjective;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlots;
import org.spongepowered.api.scoreboard.objective.Objective;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class SpongeScoreboard implements ImpactorScoreboard {

    private final AbstractSpongeObjective objective;
    private final List<ScoreboardLine> lines;

    public SpongeScoreboard(SpongeScoreboardBuilder builder) {
        this.objective = (AbstractSpongeObjective) builder.objective;
        this.lines = builder.lines;
    }

    @Override
    public ScoreboardObjective getTitle() {
        return this.objective;
    }

    @Override
    public List<ScoreboardLine> getLines() {
        return this.lines;
    }

    @Override
    public void applyFor(UUID user) {
        ServerPlayer player = Sponge.server().player(user).orElseThrow(() -> new IllegalArgumentException("No player found with UUID: " + user));
        this.createFor(player);
    }

    public void createFor(ServerPlayer target) {
        Scoreboard scoreboard = Scoreboard.builder().build();
        Objective objective = this.objective.create(target);
        scoreboard.addObjective(objective);
        scoreboard.updateDisplaySlot(objective, DisplaySlots.SIDEBAR);

        if(this.objective instanceof Updatable) {
            ((Updatable) this.objective).start();
        }

        for(ScoreboardLine line : this.lines) {
            ((AbstractSpongeSBLine) line).setup(scoreboard, objective, target);

            if(line instanceof Updatable) {
                ((Updatable) line).start();
            }
        }

        target.setScoreboard(scoreboard);
    }

    public static SpongeScoreboardBuilder builder() {
        return new SpongeScoreboardBuilder();
    }

    public static class SpongeScoreboardBuilder implements ScoreboardBuilder {

        private ScoreboardObjective objective;
        private List<ScoreboardLine> lines = Lists.newArrayList();

        @Override
        public SpongeScoreboardBuilder objective(ScoreboardObjective objective) {
            Preconditions.checkArgument(objective instanceof AbstractSpongeObjective);
            this.objective = objective;
            return this;
        }

        @Override
        public SpongeScoreboardBuilder line(ScoreboardLine line) {
            this.lines.add(line);
            return this;
        }

        @Override
        public ScoreboardBuilder lines(ScoreboardLine... lines) {
            this.lines.addAll(Arrays.asList(lines));
            return this;
        }

        @Override
        public ScoreboardBuilder lines(Iterable<ScoreboardLine> lines) {
            for(ScoreboardLine line : lines) {
                this.lines.add(line);
            }

            return this;
        }

        @Override
        public SpongeScoreboardBuilder from(ImpactorScoreboard input) {
            this.objective = input.getTitle();
            this.lines = input.getLines();
            return this;
        }

        @Override
        public SpongeScoreboard build() {
            Preconditions.checkNotNull(this.objective);

            return new SpongeScoreboard(this);
        }
    }

}
