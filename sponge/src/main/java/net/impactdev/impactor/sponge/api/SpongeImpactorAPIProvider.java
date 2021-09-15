package net.impactdev.impactor.sponge.api;

import net.impactdev.impactor.api.event.EventBus;
import net.impactdev.impactor.api.platform.Platform;
import net.impactdev.impactor.api.scheduler.SchedulerAdapter;
import net.impactdev.impactor.common.api.ImpactorAPIProvider;
import net.impactdev.impactor.sponge.SpongePlatform;

public class SpongeImpactorAPIProvider extends ImpactorAPIProvider {

    private final SchedulerAdapter scheduler;

    public SpongeImpactorAPIProvider(SchedulerAdapter adapter) {
        this.scheduler = adapter;
    }

    @Override
    public Platform getPlatform() {
        return new SpongePlatform();
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
