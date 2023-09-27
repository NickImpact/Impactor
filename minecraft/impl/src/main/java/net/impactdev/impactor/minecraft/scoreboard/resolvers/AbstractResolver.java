package net.impactdev.impactor.minecraft.scoreboard.resolvers;

import net.impactdev.impactor.api.scoreboards.ConfigurableScoreboardComponent;
import net.impactdev.impactor.api.scoreboards.updaters.ComponentResolver;

public abstract class AbstractResolver implements ComponentResolver {

    protected final ConfigurableScoreboardComponent.Viewable displayable;

    protected AbstractResolver(ConfigurableScoreboardComponent.Viewable displayable) {
        this.displayable = displayable;
    }

    protected ConfigurableScoreboardComponent.Viewable viewed() {
        return this.displayable;
    }

}
