package net.impactdev.impactor.minecraft.scoreboard.components;

import net.impactdev.impactor.scoreboards.ScoreboardDisplayable;
import net.impactdev.impactor.scoreboards.updaters.ComponentResolver;
import net.kyori.adventure.text.Component;

abstract class ImpactorScoreboardDisplayable implements ScoreboardDisplayable {

    @Override
    public Component text() {
        return null;
    }

    @Override
    public ComponentResolver resolver() {
        return null;
    }

}
