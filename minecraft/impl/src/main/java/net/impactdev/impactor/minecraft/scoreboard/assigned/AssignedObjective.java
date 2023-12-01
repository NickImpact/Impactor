package net.impactdev.impactor.minecraft.scoreboard.assigned;

import net.impactdev.impactor.api.scoreboards.objectives.Objective;

public class AssignedObjective {

    private final Objective delegate;

    public AssignedObjective(Objective delegate) {
        this.delegate = delegate;
    }



}
