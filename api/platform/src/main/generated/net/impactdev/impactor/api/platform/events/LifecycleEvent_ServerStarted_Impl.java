package net.impactdev.impactor.api.platform.events;

import java.lang.Override;
import java.lang.String;
import java.util.Objects;
import javax.annotation.processing.Generated;
import net.impactdev.impactor.api.platform.Platform;
import net.impactdev.impactor.api.platform.Server;
import org.spongepowered.eventgen.annotations.internal.GeneratedEvent;

@Generated("org.spongepowered.eventimplgen.processor.EventImplGenProcessor")
@GeneratedEvent(
        source = net.impactdev.impactor.api.platform.events.lifecycle.LifecycleEvent.ServerStarted.class,
        version = "8.0.0"
)
final class LifecycleEvent_ServerStarted_Impl implements net.impactdev.impactor.api.platform.events.lifecycle.LifecycleEvent.ServerStarted {
    private Platform platform;

    private Server server;

    LifecycleEvent_ServerStarted_Impl(final Platform platform, final Server server) {
        this.platform = Objects.requireNonNull(platform, "The property 'platform' was not provided!");
        this.server = Objects.requireNonNull(server, "The property 'server' was not provided!");
    }

    @Override
    public Platform platform() {
        return this.platform;
    }

    @Override
    public Server server() {
        return this.server;
    }

    @Override
    public String toString() {
        return "ServerStarted{"
            + "platform=" + this.platform()
            + ", server=" + this.server()
            + '}';
        }
    }
