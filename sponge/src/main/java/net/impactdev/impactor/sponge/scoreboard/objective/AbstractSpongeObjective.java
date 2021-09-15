package net.impactdev.impactor.sponge.scoreboard.objective;

import net.impactdev.impactor.api.scoreboard.objective.ScoreboardObjective;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.scoreboard.criteria.Criteria;
import org.spongepowered.api.scoreboard.objective.Objective;

public abstract class AbstractSpongeObjective implements ScoreboardObjective {

    private Objective delegate;

    public Objective create(ServerPlayer player) {
        return this.delegate = Objective.builder()
                .criterion(Criteria.DUMMY)
                .name("O-" + player.uniqueId().toString().substring(0, 14))
                .displayName(this.getText())
                .build();
    }

    protected Objective getDelegate() {
        return this.delegate;
    }

}
