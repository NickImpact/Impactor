package net.impactdev.impactor.api.scoreboard.events;

import net.impactdev.impactor.api.Impactor;

import java.util.Optional;

/**
 * Represents the event bus that coincides with the target platform of the server.
 */
public abstract class PlatformBus<L> implements Bus<L> {

    private static PlatformBus<?> instance;

    public static <L> PlatformBus<L> getOrCreate() {
        return Optional.ofNullable((PlatformBus<L>) instance).orElseGet(() -> {
            PlatformBus<L> result = Impactor.getInstance().getPlatform().createPlatformBus();
            instance = result;
            return result;
        });
    }

    @Override
    public String getID() {
        return "Platform - " + this.getPlatformType();
    }

    protected abstract String getPlatformType();

}
