package net.impactdev.impactor.sponge;

import net.impactdev.impactor.api.platform.Platform;
import net.impactdev.impactor.api.scoreboard.events.PlatformBus;
import net.impactdev.impactor.sponge.scoreboard.SpongeBus;

public class SpongePlatform implements Platform {

    @Override
    public PlatformBus<?> createPlatformBus() {
        return new SpongeBus();
    }

}
