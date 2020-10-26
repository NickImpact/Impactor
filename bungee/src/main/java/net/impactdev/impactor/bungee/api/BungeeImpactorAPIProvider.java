package net.impactdev.impactor.bungee.api;

import net.impactdev.impactor.api.event.EventBus;
import net.impactdev.impactor.api.scheduler.SchedulerAdapter;
import net.impactdev.impactor.common.api.ImpactorAPIProvider;

public class BungeeImpactorAPIProvider extends ImpactorAPIProvider {

    private final SchedulerAdapter scheduler;

    public BungeeImpactorAPIProvider(SchedulerAdapter adapter) {
        this.scheduler = adapter;
    }

    @Override
    public EventBus getEventBus() {
        return this.getRegistry().get(EventBus.class);
    }

    @Override
    public SchedulerAdapter getScheduler() {
        return this.scheduler;
    }

}
