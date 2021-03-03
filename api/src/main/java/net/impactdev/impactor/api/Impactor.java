package net.impactdev.impactor.api;

import net.impactdev.impactor.api.event.EventBus;
import net.impactdev.impactor.api.registry.Registry;
import net.impactdev.impactor.api.scheduler.SchedulerAdapter;

public interface Impactor {

    static Impactor getInstance() {
        return ImpactorServiceProvider.get();
    }

    Registry getRegistry();

    EventBus getEventBus();

    SchedulerAdapter getScheduler();

}
