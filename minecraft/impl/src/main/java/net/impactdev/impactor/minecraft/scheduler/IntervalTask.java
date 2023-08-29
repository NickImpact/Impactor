package net.impactdev.impactor.minecraft.scheduler;

import net.impactdev.impactor.api.scheduler.Ticks;

public final class IntervalTask extends AbstractTask {

    private final Ticks interval;
    private long tick;

    public IntervalTask(Runnable action, Ticks interval) {
        super(action);
        this.interval = interval;
    }

    @Override
    public void tick() {
        this.tick++;
        if(this.tick >= this.interval.ticks()) {
            this.execute();
            this.tick = 0;
        }
    }

    @Override
    public boolean valid() {
        return true;
    }
}
