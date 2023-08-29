package net.impactdev.impactor.minecraft.scheduler;

public abstract class AbstractTask implements Task {

    protected final Runnable action;

    protected AbstractTask(Runnable action) {
        this.action = action;
    }

    @Override
    public Runnable action() {
        return this.action;
    }

    public void execute() {

    }

}
