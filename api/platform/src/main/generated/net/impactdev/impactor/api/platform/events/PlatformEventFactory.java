package net.impactdev.impactor.api.platform.events;

import javax.annotation.processing.Generated;
import net.impactdev.impactor.api.platform.Platform;
import net.impactdev.impactor.api.platform.Server;
import net.impactdev.impactor.api.platform.events.lifecycle.LifecycleEvent;
import org.spongepowered.eventgen.annotations.internal.GeneratedFactory;

@GeneratedFactory(
        version = "8.0.0"
)
@Generated("org.spongepowered.eventimplgen.processor.EventImplGenProcessor")
public final class PlatformEventFactory {
    private PlatformEventFactory() {
    }

    public static LifecycleEvent.ServerStarted createLifecycleEventServerStarted(
            final Platform platform, final Server server) {
        return new LifecycleEvent_ServerStarted_Impl(platform, server);
    }

    public static LifecycleEvent.ServerStarting createLifecycleEventServerStarting(
            final Platform platform, final Server server) {
        return new LifecycleEvent_ServerStarting_Impl(platform, server);
    }
}
