package com.nickimpact.impactor.sponge.api;

import com.nickimpact.impactor.api.event.EventBus;
import com.nickimpact.impactor.api.scheduler.SchedulerAdapter;
import com.nickimpact.impactor.sponge.event.SpongeEventBus;
import com.nickimpact.impactor.common.api.ImpactorAPIProvider;

public class SpongeImpactorAPIProvider extends ImpactorAPIProvider {

    private final EventBus eventBus = new SpongeEventBus();

    private final SchedulerAdapter scheduler;

    public SpongeImpactorAPIProvider(SchedulerAdapter adapter) {
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
