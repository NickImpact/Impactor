package net.impactdev.impactor.api;

import net.impactdev.impactor.api.event.EventBus;
import net.impactdev.impactor.api.platform.Platform;
import net.impactdev.impactor.api.registry.Registry;
import net.impactdev.impactor.api.scheduler.SchedulerAdapter;
import net.impactdev.impactor.api.scoreboard.events.PlatformBus;

public interface Impactor {

    static Impactor getInstance() {
        return ImpactorServiceProvider.get();
    }

    Platform getPlatform();

    Registry getRegistry();

    EventBus getEventBus();

    SchedulerAdapter getScheduler();

}
