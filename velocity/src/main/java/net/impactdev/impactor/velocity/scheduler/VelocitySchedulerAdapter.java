package net.impactdev.impactor.velocity.scheduler;

import com.velocitypowered.api.scheduler.ScheduledTask;
import net.impactdev.impactor.api.scheduler.SchedulerAdapter;
import net.impactdev.impactor.api.scheduler.SchedulerTask;
import net.impactdev.impactor.velocity.VelocityImpactorBootstrap;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class VelocitySchedulerAdapter implements SchedulerAdapter {

    private final VelocityImpactorBootstrap bootstrap;

    private final Executor executor;
    private final Set<ScheduledTask> tasks = Collections.newSetFromMap(new WeakHashMap<>());

    public VelocitySchedulerAdapter(VelocityImpactorBootstrap bootstrap) {
        this.bootstrap = bootstrap;
        this.executor = r -> bootstrap.getProxy().getScheduler().buildTask(this.bootstrap, r).schedule();
    }

    @Override
    public Executor async() {
        return this.executor;
    }

    @Override
    public Executor sync() {
        return this.executor;
    }

    @Override
    public SchedulerTask asyncLater(Runnable task, long delay, TimeUnit unit) {
        ScheduledTask t = this.bootstrap.getProxy().getScheduler().buildTask(this.bootstrap, task)
                .delay((int) delay, unit)
                .schedule();

        this.tasks.add(t);
        return t::cancel;
    }

    @Override
    public SchedulerTask asyncRepeating(Runnable task, long interval, TimeUnit unit) {
        ScheduledTask t = this.bootstrap.getProxy().getScheduler().buildTask(this.bootstrap, task)
                .delay((int) interval, unit)
                .repeat((int) interval, unit)
                .schedule();

        this.tasks.add(t);
        return t::cancel;
    }

    @Override
    public SchedulerTask asyncDelayedAndRepeating(Runnable task, long delay, TimeUnit dUnit, long interval, TimeUnit iUnit) {
        ScheduledTask t = this.bootstrap.getProxy().getScheduler().buildTask(this.bootstrap, task)
                .delay((int) delay, dUnit)
                .repeat((int) interval, iUnit)
                .schedule();

        this.tasks.add(t);
        return t::cancel;
    }

    @Override
    public void shutdownScheduler() {
        for(ScheduledTask task : this.tasks) {
            try {
                task.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void shutdownExecutor() {
        // do nothing
    }
}
