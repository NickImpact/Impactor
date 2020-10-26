/*
 * This file is part of LuckPerms, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.impactdev.impactor.api.event;

import com.google.gson.reflect.TypeToken;
import net.impactdev.impactor.api.event.listener.ImpactorEventListener;
import net.impactdev.impactor.api.event.annotations.Param;
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
     * <p>Supplied arguments should be in the order they are specified by the event's use of
     * {@link Param}. The only argument that does not need to be supplied is the boolean
     * value for the state of a cancellable event. If an event is cancellable, it'll insert
     * a false state indicating that the event is not cancelled.</p>
     *
     * @param event The type of event you wish to post
     * @param args The arguments that will be used to populate the event
     * @param <T> The type of the event
     */
    <T extends ImpactorEvent> boolean post(Class<T> event, Object... args);

    /**
     * Posts an event to the event bus with the given arguments.
     *
     * <p>Supplied arguments should be in the order they are specified by the event's use of
     * {@link Param}. The only argument that does not need to be supplied is the boolean
     * value for the state of a cancellable event. If an event is cancellable, it'll insert
     * a false state indicating that the event is not cancelled.</p>
     *
     * <p>This type of post expects a generic event type. This is meant to allow for dynamic events
     * in which a generic typing for the event can make work easier.</p>
     *
     * @param event The type of event you wish to post
     * @param args The arguments that will be used to populate the event
     * @param <T> The type of the event
     */
    <V, T extends ImpactorEvent.Generic<?>> boolean post(Class<T> event, TypeToken<V> generic, Object... args);

    /**
     * Posts an event to the event bus asynchronously with the given arguments.
     *
     * <p>Supplied arguments should be in the order they are specified by the event's use of
     * {@link Param}. Note: A cancellable event can not be fired async.</p>
     *
     * @param event The type of event you wish to post
     * @param args The arguments that will be used to populate the event
     * @param <T> The type of the event
     */
    <T extends ImpactorEvent> void postAsync(Class<T> event, Object... args);

    /**
     * Posts an event to the event bus asynchronously with the given arguments.
     *
     * <p>Supplied arguments should be in the order they are specified by the event's use of
     * {@link Param}. Note: A cancellable event can not be fired async.</p>
     *
     * <p>This type of post expects a generic event type. This is meant to allow for dynamic events
     * in which a generic typing for the event can make work easier.</p>
     *
     * @param event The type of event you wish to post
     * @param args The arguments that will be used to populate the event
     * @param <T> The type of the event
     */
    <V, T extends ImpactorEvent.Generic<?>> void postAsync(Class<T> event, TypeToken<V> generic, Object... args);

    /**
     * Delegates the process of subscribing to events to the listener passed to this call.
     *
     * @param listener The instance that'll subscribe to Impactor based events
     */
    void subscribe(@NonNull ImpactorEventListener listener);

    /**
     * Registers a new subscription to the given event.
     *
     * <p>The returned {@link EventSubscription} instance encapsulates the subscription state. It has
     * methods which can be used to terminate the subscription, or view stats about the nature of
     * the subscription.</p>
     *
     * @param eventClass the event class
     * @param handler    the event handler
     * @param <T>        the event class
     * @return an event handler instance representing this subscription
     */
    @NonNull <T extends ImpactorEvent> EventSubscription<T> subscribe(@NonNull Class<T> eventClass, @NonNull Consumer<? super T> handler);

    /**
     * Registers a new subscription to the given event.
     *
     * <p>The returned {@link EventSubscription} instance encapsulates the subscription state. It has
     * methods which can be used to terminate the subscription, or view stats about the nature of
     * the subscription.</p>
     *
     * <p>Unlike {@link #subscribe(Class, Consumer)}, this method accepts an additional parameter
     * for {@code plugin}. This object must be a "plugin" instance on the platform, and is used to
     * automatically {@link EventSubscription#close() unregister} the subscription when the
     * corresponding plugin is disabled.</p>
     *
     * @param <T>        the event class
     * @param plugin     a plugin instance to bind the subscription to.
     * @param eventClass the event class
     * @param handler    the event handler
     * @return an event handler instance representing this subscription
     */
    @NonNull <T extends ImpactorEvent> EventSubscription<T> subscribe(Object plugin, @NonNull Class<T> eventClass, @NonNull Consumer<? super T> handler);

    /**
     * Gets a set of all registered handlers for a given event.
     *
     * @param eventClass the event to find handlers for
     * @param <T>        the event class
     * @return an immutable set of event handlers
     */
    @NonNull <T extends ImpactorEvent> Set<EventSubscription<T>> getSubscriptions(@NonNull Class<T> eventClass);

}
