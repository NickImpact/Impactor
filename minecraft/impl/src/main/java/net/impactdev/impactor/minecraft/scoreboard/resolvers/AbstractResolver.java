package net.impactdev.impactor.minecraft.scoreboard.resolvers;

import net.impactdev.impactor.api.scoreboards.resolvers.Updatable;
import net.impactdev.impactor.api.scoreboards.resolvers.updaters.resolver.ComponentResolver;

public abstract class AbstractResolver implements ComponentResolver {

    protected final Updatable.Viewable displayable;

    protected AbstractResolver(Updatable.Viewable displayable) {
        this.displayable = displayable;
    }

    protected Updatable.Viewable viewed() {
        return this.displayable;
    }

}
