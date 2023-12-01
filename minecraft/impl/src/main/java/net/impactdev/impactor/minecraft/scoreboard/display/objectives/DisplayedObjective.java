package net.impactdev.impactor.minecraft.scoreboard.display.objectives;

import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.scoreboards.AssignedScoreboard;
import net.impactdev.impactor.api.scoreboards.display.resolvers.ComponentResolver;
import net.impactdev.impactor.api.scoreboards.objectives.Objective;
import net.impactdev.impactor.api.utility.Context;
import net.kyori.adventure.text.Component;

public final class DisplayedObjective implements Objective.Displayed {

    private final AssignedScoreboard scoreboard;
    private final Objective delegate;
    private final ComponentResolver resolver;

    private Component text;

    public DisplayedObjective(AssignedScoreboard parent, Objective delegate) {
        this.scoreboard = parent;
        this.delegate = delegate;

        this.resolver = this.delegate.resolver().create();
    }

    @Override
    public ComponentResolver resolver() {
        return this.resolver;
    }

    @Override
    public Objective delegate() {
        return this.delegate;
    }

    @Override
    public void resolve() {
        Context context = Context.of(PlatformPlayer.class, this.scoreboard.viewer());
        this.text = this.resolver().resolve(context);
    }

    @Override
    public Component text() {
        return this.text;
    }
}
