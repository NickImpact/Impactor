package net.impactdev.impactor.sponge.scoreboard.lines;

import net.impactdev.impactor.api.scoreboard.lines.ScoreboardLine;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.objective.Objective;

import java.util.UUID;
import java.util.function.Supplier;

public abstract class AbstractSpongeSBLine implements ScoreboardLine {

    protected final int score;

    public AbstractSpongeSBLine(int score) {
        this.score = score;
    }

    @Override
    public int getScore() {
        return this.score;
    }

    public abstract void setup(Scoreboard scoreboard, Objective objective, ServerPlayer target);

    protected Supplier<ServerPlayer> player(UUID target) {
        return () -> Sponge.server().player(target).orElseThrow(() -> new IllegalStateException("Unable to locate target player"));
    }

}
