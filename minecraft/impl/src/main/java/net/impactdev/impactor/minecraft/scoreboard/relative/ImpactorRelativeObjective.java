package net.impactdev.impactor.minecraft.scoreboard.relative;

import net.impactdev.impactor.api.scoreboards.resolvers.objectives.Objective;
import net.impactdev.impactor.api.scoreboards.players.RelativeObjective;
import net.impactdev.impactor.api.scoreboards.resolvers.updaters.resolver.ComponentResolver;
import net.kyori.adventure.text.Component;

public class ImpactorRelativeObjective implements RelativeObjective {

    @Override
    public void update() {

    }

    @Override
    public Objective delegate() {
        return null;
    }

    @Override
    public ComponentResolver resolver() {
        return null;
    }

    @Override
    public Component text() {
        return null;
    }

}
