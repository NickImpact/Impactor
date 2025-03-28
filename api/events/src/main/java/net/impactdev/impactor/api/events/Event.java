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

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

public final class Event<T> {

    private final PriorityBlockingQueue<EventSubscription<T>> subscriptions = new PriorityBlockingQueue<>(
            1,
            Comparator.comparingInt(a -> a.config().order())
    );

    /**
     * Creates a new event capable of processing the expected data typing.
     *
     * @return A new event capable of publishing the designated typing
     * @param <T> The type of data this event should publish
     */
    public static <T> Event<T> create() {
        return new Event<>();
    }

    /**
     * Registers a new subscription to this particular event with default configurations.
     *
     * @param subscriber A subscriber of this event
     * @return A new subscription receipt
     * @since 6.0.0
     */
    public EventSubscription<T> subscribe(final @NotNull EventSubscriber<T> subscriber) {
        return this.subscribe(subscriber, EventConfig.defaults());
    }

    /**
     * Registers a new subscription to this particular event. Subscribers are made up of a general event handler,
     * as well as a configuration indicating how they are expected to run when packaged with other subscribers.
     *
     * @param subscriber A subscriber of this event
     * @param config The configuration of the subscriber
     * @return A new subscription receipt
     * @since 6.0.0
     */
    public EventSubscription<T> subscribe(final @NotNull EventSubscriber<T> subscriber, final @NotNull EventConfig<T> config) {
        Preconditions.checkNotNull(subscriber);
        Preconditions.checkNotNull(config);

        EventSubscription<T> subscription = new EventSubscription<>(subscriber, config);
        subscription.callback = () -> {
            if(!this.subscriptions.remove(subscription)) {
                throw new RuntimeException("Subscription could not be removed");
            }
        };

        this.subscriptions.add(subscription);
        return subscription;
    }

    /**
     * Publishes the given event context to all current subscribers.
     *
     * @param context The event data actually being posted by the event
     * @return A publication result indicating the success of the event
     * @since 6.0.0
     */
    public PublicationResult publish(T context) {
        final Map<EventSubscription<?>, Throwable> exceptions = new HashMap<>();

        for (final EventSubscription<T> subscription : this.subscriptions) {
            EventConfig<T> config = subscription.config();
            Accepts<T> accepts = config.acceptRules();

            if (accepts.accepts(context)) {
                try {
                    EventSubscriber<T> subscriber = subscription.subscriber();
                    subscriber.on(context);
                } catch (Throwable e) {
                    exceptions.put(subscription, e);
                }
            }
        }

        if (exceptions.isEmpty()) {
            return PublicationResult.successful();
        }

        return PublicationResult.failure(exceptions);
    }

}
