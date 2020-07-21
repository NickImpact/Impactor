package com.nickimpact.impactor.bungee.api;

import com.nickimpact.impactor.api.event.EventBus;
import com.nickimpact.impactor.api.scheduler.SchedulerAdapter;
import com.nickimpact.impactor.common.api.ImpactorAPIProvider;

public class BungeeImpactorAPIProvider extends ImpactorAPIProvider {

    private final EventBus eventBus;

    private final SchedulerAdapter scheduler;

    public BungeeImpactorAPIProvider(EventBus eventBus, SchedulerAdapter adapter) {
        this.eventBus = eventBus;
        this.scheduler = adapter;
    }

    @Override
    public EventBus getEventBus() {
        return this.eventBus;
    }

    @Override
    public SchedulerAdapter getScheduler() {
        return this.scheduler;
    }

}
