package net.impactdev.impactor.minecraft.scheduler;

import com.google.common.collect.Maps;
import net.impactdev.impactor.api.scheduler.Scheduler;
import net.impactdev.impactor.api.scheduler.SchedulerTask;
import net.impactdev.impactor.api.scheduler.Ticks;
import net.impactdev.impactor.minecraft.platform.GamePlatform;
import net.kyori.adventure.key.Key;
import net.minecraft.server.MinecraftServer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class SyncScheduler implements Scheduler {

    private final Map<UUID, Task> tasks = Maps.newConcurrentMap();
    private final Executor sync;

    public SyncScheduler(GamePlatform platform) {
        this.sync = platform.server()::executeBlocking;
    }

    public void initialize(MinecraftServer server) {
        server.addTickable(this::tick);
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
    public SchedulerTask delayed(Runnable action, Ticks ticks) {
        UUID id = UUID.randomUUID();
        this.tasks.put(id, new DelayedTask(action, ticks));
        return () -> this.tasks.remove(id);
    }

    @Override
    public SchedulerTask delayed(Runnable action, long delay, TimeUnit unit) {
        return this.delayed(action, Ticks.ofWallClockTime(delay, unit.toChronoUnit()));
    }

    @Override
    public SchedulerTask repeating(Runnable action, Ticks ticks) {
        UUID id = UUID.randomUUID();
        this.tasks.put(id, new IntervalTask(action, ticks));
        return () -> this.tasks.remove(id);
    }

    @Override
    public SchedulerTask repeating(Runnable action, long interval, TimeUnit unit) {
        return this.repeating(action, Ticks.ofWallClockTime(interval, unit.toChronoUnit()));
    }

    @Override
    public SchedulerTask delayedAndRepeating(Runnable action, Ticks delay, Ticks interval) {
        UUID id = UUID.randomUUID();
        this.tasks.put(id, new DelayedAndRepeatingTask(action, delay, interval));
        return () -> this.tasks.remove(id);
    }

    @Override
    public SchedulerTask delayedAndRepeating(Runnable action, long delay, long interval, TimeUnit unit) {
        return this.delayedAndRepeating(
                action,
                Ticks.ofWallClockTime(delay, unit.toChronoUnit()),
                Ticks.ofWallClockTime(interval, unit.toChronoUnit())
        );
    }

    private void tick() {
        this.tasks.forEach((key, task) -> {
            task.tick();
            if(!task.valid()) {
                this.tasks.remove(key);
            }
        });
    }

}
