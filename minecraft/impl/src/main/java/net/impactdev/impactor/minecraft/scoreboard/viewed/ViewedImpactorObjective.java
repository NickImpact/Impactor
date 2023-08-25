package net.impactdev.impactor.minecraft.scoreboard.viewed;

import net.impactdev.impactor.scoreboards.objectives.Objective;
import net.impactdev.impactor.scoreboards.viewed.ViewedObjective;
import net.kyori.adventure.text.Component;

public class ViewedImpactorObjective implements ViewedObjective {

    public static ViewedImpactorObjective create(Objective objective) {
        return new ViewedImpactorObjective();
    }

    @Override
    public Objective delegate() {
        return null;
    }

    @Override
    public Component text() {
        return null;
    }

}
