package net.impactdev.impactor.minecraft.utility.adventure;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalListener;
import net.impactdev.impactor.core.plugin.BaseImpactorPlugin;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.event.ClickCallback;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class ClickCallbackRegistry {

    public static final ClickCallbackRegistry INSTANCE = new ClickCallbackRegistry();

    private final Cache<UUID, CallbackRegistration> registrations = Caffeine.newBuilder()
            .expireAfterWrite(24, TimeUnit.HOURS)
            .maximumSize(1024)
            .removalListener((RemovalListener<UUID, CallbackRegistration>) (key, value, cause) -> BaseImpactorPlugin.instance()
                    .logger()
                    .debug("Removing callback " + key + " from cache with reason: " + cause.name())
            )
            .build();

    record CallbackRegistration(ClickCallback.Options options, ClickCallback<Audience> callback, Instant expiration, AtomicInteger useCount) {}

    private ClickCallbackRegistry() {}

    public String register(final ClickCallback<Audience> callback, final ClickCallback.Options options) {
        final UUID id = UUID.randomUUID();
        final CallbackRegistration registration = new CallbackRegistration(
                options,
                callback,
                Instant.now().plus(options.lifetime()),
                new AtomicInteger()
        );

        this.registrations.put(id, registration);

        return "/" + ClickCallbackCommand.COMMAND_ID + " " + id;
    }

    Optional<CallbackRegistration> query(UUID uuid) {
        return Optional.ofNullable(this.registrations.getIfPresent(uuid));
    }

    void invalidate(UUID uuid) {
        this.registrations.invalidate(uuid);
    }
}
