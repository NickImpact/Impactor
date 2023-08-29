package net.impactdev.impactor.minecraft.scheduler;

import net.impactdev.impactor.api.scheduler.Ticks;

public class DelayedTask extends AbstractTask {

    private final Ticks delay;
    private long ticks;

    protected DelayedTask(Runnable action, Ticks ticks) {
        super(action);
        this.delay = ticks;
    }

    @Override
    public void tick() {
        this.ticks++;
        if(this.delay.ticks() <= this.ticks) {
            this.execute();
        }
    }

    @Override
    public boolean valid() {
        return this.delay.ticks() < this.ticks;
    }
}
