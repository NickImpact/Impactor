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

package com.nickimpact.impactor.common.event;

import com.google.gson.reflect.TypeToken;
import com.nickimpact.impactor.api.Impactor;
import com.nickimpact.impactor.api.event.EventBus;
import com.nickimpact.impactor.api.event.EventSubscription;
import com.nickimpact.impactor.api.event.ImpactorEvent;
import com.nickimpact.impactor.api.event.listener.ImpactorEventListener;
import com.nickimpact.impactor.api.event.type.Cancellable;
import com.nickimpact.impactor.common.event.gen.EventGenerator;
import com.nickimpact.impactor.common.event.reader.ImpactorMethodSubscriptionAdapter;
import net.kyori.event.SimpleEventBus;
import net.kyori.event.method.MethodSubscriptionAdapter;
import net.kyori.event.method.asm.ASMEventExecutorFactory;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class AbstractEventBus<P> implements EventBus, AutoCloseable {

    private final EventGenerator generator = new EventGenerator();

    private final Bus bus = new Bus();
    private final MethodSubscriptionAdapter<ImpactorEventListener> adapter = new ImpactorMethodSubscriptionAdapter(this.bus, new ASMEventExecutorFactory<>());

    protected abstract P checkPlugin(Object plugin) throws IllegalArgumentException;

    private void post(ImpactorEvent event) {
        this.bus.post(event);
    }

    @Override
    public <T extends ImpactorEvent> void post(Class<T> event, Object... args) {
       this.post(generator.generate(event, args));
    }

    @Override
    public <V, T extends ImpactorEvent.Generic<?>> void post(Class<T> event, TypeToken<V> generic, Object... args) {
        this.post(generator.generate(event, generic.getType(), args));
    }

    @Override
    public <T extends ImpactorEvent> void postAsync(Class<T> event, Object... args) {
        if(Cancellable.class.isAssignableFrom(event)) {
            throw new RuntimeException("Cancellable events cannot be posted async (" + event.getName() + ")");
        }

        final Supplier<T> provider = () -> this.generator.generate(event, args);
        Impactor.getInstance().getScheduler().executeAsync(() -> {
            if(!shouldPost(event)) {
                return;
            }

            T implementation = provider.get();
            this.post(implementation);
        });
    }

    @Override
    public <V, T extends ImpactorEvent.Generic<?>> void postAsync(Class<T> event, TypeToken<V> generic, Object... args) {
        if(Cancellable.class.isAssignableFrom(event)) {
            throw new RuntimeException("Cancellable events cannot be posted async (" + event.getName() + ")");
        }

        final Supplier<T> provider = () -> this.generator.generate(event, generic.getType(), args);
        Impactor.getInstance().getScheduler().executeAsync(() -> {
            if(!shouldPost(event)) {
                return;
            }

            T implementation = provider.get();
            this.post(implementation);
        });
    }

    public boolean shouldPost(Class<? extends ImpactorEvent> event) {
        return this.bus.hasSubscribers(event);
    }

    @Override
    public void subscribe(@NonNull ImpactorEventListener listener) {
        Objects.requireNonNull(listener, "Listener was null");
        this.adapter.register(listener);
    }

    @Override
    public @NonNull <T extends ImpactorEvent> EventSubscription<T> subscribe(@NonNull Class<T> event, @NonNull Consumer<? super T> handler) {
        Objects.requireNonNull(event, "event");
        Objects.requireNonNull(handler, "handler");
        return registerSubscription(event, handler, null);
    }

    @Override
    public @NonNull <T extends ImpactorEvent> EventSubscription<T> subscribe(Object plugin, @NonNull Class<T> eventClass, @NonNull Consumer<? super T> handler) {
        Objects.requireNonNull(plugin, "plugin");
        Objects.requireNonNull(eventClass, "eventClass");
        Objects.requireNonNull(handler, "handler");
        return registerSubscription(eventClass, handler, checkPlugin(plugin));
    }

    private @NonNull <T extends ImpactorEvent> EventSubscription<T> registerSubscription(Class<T> event, Consumer<? super T> handler, Object plugin) {
        if(!event.isInterface()) {
            throw new IllegalArgumentException("Class " + event.getName() + " is not an interface");
        }

        if(!ImpactorEvent.class.isAssignableFrom(event)) {
            throw new IllegalArgumentException("Class " + event.getName() + " does not implement ImpactorEvent");
        }

        ImpactorEventSubscription<T> processor = new ImpactorEventSubscription<>(this, event, handler, plugin);
        this.bus.register(event, processor);

        return processor;
    }

    @Override
    public @NonNull <T extends ImpactorEvent> Set<EventSubscription<T>> getSubscriptions(@NonNull Class<T> event) {
        return this.bus.getHandlers(event);
    }

    /**
     * Removes a specific handler from the bus
     *
     * @param handler the handler to remove
     */
    public void unregisterHandler(ImpactorEventSubscription<?> handler) {
        this.bus.unregister(handler);
    }

    /**
     * Removes all handlers for a specific plugin
     *
     * @param plugin the plugin
     */
    protected void unregisterHandlers(P plugin) {
        this.bus.unregister(sub -> ((ImpactorEventSubscription<?>) sub).getPlugin() == plugin);
    }

    @Override
    public void close() {
        this.bus.unregisterAll();
    }

    private static final class Bus extends SimpleEventBus<ImpactorEvent> {

        Bus() {
            super(ImpactorEvent.class);
        }

        @Override
        @SuppressWarnings("UnstableApiUsage")
        protected @Nullable Type eventGenericType(final @NonNull ImpactorEvent event) {
            return event instanceof ImpactorEvent.Generic ? ((ImpactorEvent.Generic<?>) event).type().getType() : null;
        }

        @SuppressWarnings("unchecked")
        public <T extends ImpactorEvent> Set<EventSubscription<T>> getHandlers(Class<T> eventClass) {
            return super.subscribers().values().stream()
                    .filter(s -> s instanceof EventSubscription && ((EventSubscription<?>) s).getEventClass().isAssignableFrom(eventClass))
                    .map(s -> (EventSubscription<T>)s)
                    .collect(Collectors.toSet());
        }

    }
}
