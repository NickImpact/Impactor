package net.impactdev.impactor.fabric.scheduler;

import net.impactdev.impactor.api.scheduler.Scheduler;
import net.impactdev.impactor.api.scheduler.SchedulerTask;
import net.impactdev.impactor.fabric.FabricImpactorBootstrap;
import net.kyori.adventure.key.Key;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;


class FabricScheduler implements Scheduler {

    private final Executor sync;

    public FabricScheduler(FabricImpactorBootstrap bootstrap) {
        this.sync = r -> bootstrap.server()
                .orElseThrow(() -> new IllegalStateException("Server not yet available"))
                .submit(r)
                .join();
    }

    @Override
    public Key key() {
        return Scheduler.SYNCHRONOUS;
    }

    @Override
    public Executor executor() {
        return this.sync;
    }

    @Override
    public void publish(Runnable runnable) {
        this.sync.execute(runnable);
    }

    @Override
    public SchedulerTask delayed(Runnable action, long delay, TimeUnit unit) {
        return null;
    }

    @Override
    public SchedulerTask repeating(Runnable action, long interval, TimeUnit unit) {
        return null;
    }

    @Override
    public SchedulerTask delayedAndRepeating(Runnable action, long delay, long interval, TimeUnit unit) {
        return null;
    }

}
