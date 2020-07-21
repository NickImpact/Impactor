package com.nickimpact.impactor.api;

import com.nickimpact.impactor.api.event.EventBus;
import com.nickimpact.impactor.api.registry.Registry;
import com.nickimpact.impactor.api.scheduler.SchedulerAdapter;

public interface Impactor {

    static Impactor getInstance() {
        return ImpactorServiceProvider.get();
    }

    Registry getRegistry();

    EventBus getEventBus();

    SchedulerAdapter getScheduler();

}
