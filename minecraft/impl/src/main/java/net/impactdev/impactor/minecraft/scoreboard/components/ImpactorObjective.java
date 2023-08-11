package net.impactdev.impactor.minecraft.scoreboard.components;

import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.scoreboards.objectives.ScoreboardObjective;
import net.kyori.adventure.text.Component;

public final class ImpactorObjective implements ScoreboardObjective, Cloneable {

    private final boolean template;

    @Override
    public Component text() {
        return null;
    }

    ImpactorObjective copy() {
        return this.clone();
    }

}
