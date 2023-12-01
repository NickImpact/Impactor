package net.impactdev.impactor.minecraft.scoreboard.display.resolvers.scheduled;

import net.impactdev.impactor.api.scheduler.SchedulerTask;
import net.impactdev.impactor.api.scheduler.v2.Scheduler;
import net.impactdev.impactor.api.scoreboards.display.Display;
import net.impactdev.impactor.api.scoreboards.display.resolvers.AbstractComponentResolver;
import net.impactdev.impactor.api.scoreboards.display.resolvers.scheduled.ScheduledResolver;
import net.impactdev.impactor.api.scoreboards.display.resolvers.scheduled.ScheduledResolverConfiguration;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

public class ScheduledResolverImpl extends AbstractComponentResolver implements ScheduledResolver {

    private final ScheduledResolverConfiguration configuration;

    private final Scheduler scheduler;
    private final ScheduledResolverConfigurationImpl.ScheduledTaskProvider provider;

    @MonotonicNonNull
    private SchedulerTask task;

    public ScheduledResolverImpl(ScheduledResolverConfigurationImpl config) {
        super(config.provider(), config.formatter());

        this.configuration = config;
        this.scheduler = config.scheduler();
        this.provider = config.task();
    }

    @Override
    public ScheduledResolverConfiguration configuration() {
        return this.configuration;
    }

    @Override
    public SchedulerTask task() {
        return this.task;
    }

    @Override
    public void start(Display displayable) {
        this.task = this.provider.schedule(this.scheduler, displayable::resolve);
    }

    @Override
    public void shutdown(Display displayable) {
        this.task.cancel();
    }


}
