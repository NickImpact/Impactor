package net.impactdev.impactor.minecraft.scoreboard.viewed;

import net.impactdev.impactor.scoreboards.objectives.Objective;
import net.impactdev.impactor.scoreboards.viewed.ViewedObjective;
import net.kyori.adventure.text.Component;

public class ViewedImpactorObjective implements ViewedObjective {

    private final Objective delegate;

    private ViewedImpactorObjective(Objective delegate) {
        this.delegate = delegate;
    }

    public static ViewedImpactorObjective create(Objective objective) {
        return new ViewedImpactorObjective(objective);
    }

    @Override
    public Objective delegate() {
        return this.delegate;
    }

    @Override
    public Component text() {
        return this.delegate.resolver().resolve(this.delegate);
    }

}
