package net.impactdev.impactor.minecraft.scheduler;

import net.impactdev.impactor.api.scheduler.Ticks;

public class DelayedAndRepeatingTask extends AbstractTask {

    private final Ticks delay;
    private final Ticks interval;
    private long tick;

    protected DelayedAndRepeatingTask(Runnable action, Ticks delay, Ticks interval) {
        super(action);
        this.delay = delay;
        this.interval = interval;
    }

    @Override
    public void tick() {
        this.tick++;
        if(this.delay.ticks() <= this.tick) {
            if(this.tick == this.delay.ticks() + 1) {
                this.execute();
            }

            if(this.interval.ticks() <= this.tick + this.delay.ticks()) {
                this.tick = this.delay.ticks();
            }
        }
    }

    @Override
    public boolean valid() {
        return true;
    }
}
