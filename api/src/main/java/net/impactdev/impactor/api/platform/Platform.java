package net.impactdev.impactor.api.platform;

import net.impactdev.impactor.api.scoreboard.events.PlatformBus;

public interface Platform {

    <L> PlatformBus<L> createPlatformBus();

}
