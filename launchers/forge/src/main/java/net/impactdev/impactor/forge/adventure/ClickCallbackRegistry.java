/*
 * This file is part of Impactor, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018-2022 NickImpact
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package net.impactdev.impactor.forge.adventure;

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
