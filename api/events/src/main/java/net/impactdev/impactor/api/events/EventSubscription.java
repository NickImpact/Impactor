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

package net.impactdev.impactor.api.events;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import java.util.UUID;

/**
 * Represents a subscription to an event. The subscription contains details such as the subscriber and
 * its bound configuration. This also maintains a callback responsible for processing cancelled subscriptions.
 *
 * @param <E> The event type
 * @since 6.0.0
 */
public class EventSubscription<E> {

    private final UUID uuid = UUID.randomUUID();

    private final EventSubscriber<E> subscriber;
    private final EventConfig<E> config;

    @MonotonicNonNull
    SubscriptionCallback callback;

    EventSubscription(final EventSubscriber<E> subscriber, final EventConfig<E> config) {
        this.subscriber = subscriber;
        this.config = config;
    }

    /**
     * The subscriber which created this subscription.
     *
     * @return The bound subscriber.
     * @since 6.0.0
     */
    public EventSubscriber<E> subscriber() {
        return this.subscriber;
    }

    /**
     * Configuration detailing how the {@link #subscriber()} should be invoked during event processing.
     *
     * @return The subscribers configuration
     * @since 6.0.0
     */
    public EventConfig<E> config() {
        return this.config;
    }

    /**
     * Unsubscribes the subscription from the event. Once unsubscribed, the bound subscriber will no
     * longer receive posted events.
     *
     * @since 6.0.0
     */
    public void unsubscribe() {
        this.callback.unsubscribe();
    }

    @FunctionalInterface
    interface SubscriptionCallback {

        void unsubscribe();

    }
}
