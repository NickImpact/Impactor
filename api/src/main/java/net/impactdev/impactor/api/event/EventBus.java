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

package net.impactdev.impactor.api.event;

import net.impactdev.impactor.api.event.listener.ImpactorEventListener;
import net.impactdev.impactor.api.plugin.PluginMetadata;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Set;
import java.util.function.Consumer;

/**
 * The Impactor event bus.
 *
 * <p>Used to subscribe (or "listen") to Impactor based events.</p>
 */
public interface EventBus {

    /**
     * Posts an event to the event bus with the given arguments.
     *
     * @param event The type of event you wish to post
     * @param <T> The type of the event
     */
    <T extends ImpactorEvent> boolean post(T event);

    /**
     * Posts an event to the event bus asynchronously with the given arguments.
     *
     * @param event The type of event you wish to post
     * @param <T> The type of the event
     */
    <T extends ImpactorEvent> void postAsync(T event);

    /**
     * Delegates the process of subscribing to events to the listener passed to this call.
     *
     * @param listener The instance that'll subscribe to Impactor based events
     */
    void subscribe(@NonNull PluginMetadata metadata, @NonNull ImpactorEventListener listener);

    /**
     * Registers a new subscription to the given event.
     *
     * <p>The returned {@link EventSubscription} instance encapsulates the subscription state. It has
     * methods which can be used to terminate the subscription, or view stats about the nature of
     * the subscription.</p>
     *
     * @param metadata   the plugin providing the listener
     * @param eventClass the event class
     * @param handler    the event handler
     * @param <T>        the event class
     * @return an event handler instance representing this subscription
     */
    @NonNull <T extends ImpactorEvent> EventSubscription<T> subscribe(@NonNull PluginMetadata metadata, @NonNull Class<T> eventClass, @NonNull Consumer<? super T> handler);

    /**
     * Indicates if the event bus has any events registered at all.
     *
     * @return <code>true</code> if any subscriptions exist, <code>false</code> otherwise
     */
    boolean hasSubscriptions();

    /**
     * Gets a set of all registered handlers for a given event.
     *
     * @param eventClass the event to find handlers for
     * @param <T>        the event class
     * @return an immutable set of event handlers
     */
    @NonNull <T extends ImpactorEvent> Set<EventSubscription<T>> getSubscriptions(@NonNull Class<T> eventClass);

}
