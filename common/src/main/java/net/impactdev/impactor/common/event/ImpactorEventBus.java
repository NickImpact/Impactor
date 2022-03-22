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

package net.impactdev.impactor.common.event;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.impactdev.impactor.api.event.EventBus;
import net.impactdev.impactor.api.event.EventSubscription;
import net.impactdev.impactor.api.event.ImpactorEvent;
import net.impactdev.impactor.api.event.listener.ImpactorEventListener;
import net.impactdev.impactor.api.event.type.Cancellable;
import net.impactdev.impactor.api.plugin.PluginMetadata;
import net.impactdev.impactor.common.event.factory.ImpactorASMExecutorFactory;
import net.impactdev.impactor.common.event.reader.ImpactorMethodSubscriptionAdapter;
import net.kyori.event.PostResult;
import net.kyori.event.SimpleEventBus;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class ImpactorEventBus implements EventBus, AutoCloseable {

    private final Bus bus = new Bus();
    private final ImpactorMethodSubscriptionAdapter adapter = new ImpactorMethodSubscriptionAdapter(this.bus, new ImpactorASMExecutorFactory<>());

    private ExecutorService service;
    private boolean enabled = false;

    public void enable() {
        if(this.enabled) {
            return;
        }

        this.service = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors(),
                new ThreadFactoryBuilder()
                        .setNameFormat("Impactor Network Executor - #%d")
                        .setDaemon(true)
                        .build()
        );
        this.enabled = true;
    }

    public void disable() {
        if(!this.enabled) {
            return;
        }

        this.service.shutdownNow();
        this.bus.unregisterAll();
    }

    @Override
    public <T extends ImpactorEvent> boolean post(T event) {
       return this.bus.send(event);
    }

    @Override
    public <T extends ImpactorEvent> void postAsync(T event) {
        if(Cancellable.class.isAssignableFrom(event.getClass())) {
            throw new RuntimeException("Cancellable events cannot be posted async");
        }

        this.service.execute(() -> {
            if(!shouldPost(event.getClass())) {
                return;
            }

            this.bus.send(event);
        });
    }

    public boolean shouldPost(Class<? extends ImpactorEvent> event) {
        return this.bus.hasSubscribers(event);
    }

    @Override
    public void subscribe(@NonNull PluginMetadata metadata, @NonNull ImpactorEventListener listener) {
        Objects.requireNonNull(listener, "listener");
        this.adapter.register(listener);
    }

    @Override
    public @NonNull <T extends ImpactorEvent> EventSubscription<T> subscribe(@NonNull PluginMetadata metadata, @NonNull Class<T> event, @NonNull Consumer<? super T> handler) {
        Objects.requireNonNull(event, "event");
        Objects.requireNonNull(handler, "handler");

        if(!event.isInterface()) {
            throw new IllegalArgumentException("Class " + event.getName() + " is not an interface");
        }

        if(!ImpactorEvent.class.isAssignableFrom(event)) {
            throw new IllegalArgumentException("Class " + event.getName() + " does not implement ImpactorEvent");
        }

        ImpactorEventSubscription<T> processor = new ImpactorEventSubscription<>(this, metadata, event, handler);
        this.bus.register(event, processor);

        return processor;
    }

    @Override
    public boolean hasSubscriptions() {
        return !this.bus.subscribers().isEmpty();
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
     * @param metadata the plugin metadata
     */
    protected void unregisterHandlers(PluginMetadata metadata) {
        this.bus.unregister(sub -> ((ImpactorEventSubscription<?>) sub).metadata() == metadata);
    }

    @Override
    public void close() {
        this.bus.unregisterAll();
    }

    private static final class Bus extends SimpleEventBus<ImpactorEvent> {

        Bus() {
            super(ImpactorEvent.class);
        }

        @SuppressWarnings("unchecked")
        public <T extends ImpactorEvent> Set<EventSubscription<T>> getHandlers(Class<T> eventClass) {
            return super.subscribers().values().stream()
                    .filter(s -> s instanceof EventSubscription && ((EventSubscription<?>) s).eventClass().isAssignableFrom(eventClass))
                    .map(s -> (EventSubscription<T>)s)
                    .collect(Collectors.toSet());
        }

        public boolean send(final @NonNull ImpactorEvent event) {
            PostResult result = this.post(event);
            if(result.wasSuccessful()) {
                return this.eventCancelled(event);
            } else {
                try {
                    result.raise();
                } catch (PostResult.CompositeException e) {
                    e.printAllStackTraces();
                }

                return false;
            }
        }

        @Override
        protected boolean eventCancelled(@NonNull ImpactorEvent event) {
            return event instanceof Cancellable && ((Cancellable) event).isCancelled();
        }
    }
}
