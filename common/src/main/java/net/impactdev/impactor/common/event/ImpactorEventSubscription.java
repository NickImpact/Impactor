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

import net.impactdev.impactor.api.event.EventSubscription;
import net.impactdev.impactor.api.event.ImpactorEvent;
import net.impactdev.impactor.api.plugin.PluginMetadata;
import net.kyori.event.EventSubscriber;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class ImpactorEventSubscription<T extends ImpactorEvent> implements EventSubscription<T>, EventSubscriber<T> {

    /** The event bus which created this handler */
    private final ImpactorEventBus eventBus;

    private final PluginMetadata metadata;

    /** The event's class type */
    private final Class<T> eventClass;

    /** The delegate event handler */
    private final Consumer<? super T> consumer;

    private final AtomicBoolean active = new AtomicBoolean(true);

    public ImpactorEventSubscription(ImpactorEventBus eventBus, PluginMetadata metadata, Class<T> eventClass, Consumer<? super T> consumer) {
        this.eventBus = eventBus;
        this.metadata = metadata;
        this.eventClass = eventClass;
        this.consumer = consumer;
    }

    @Override
    public boolean active() {
        return this.active.get();
    }

    @Override
    public void close() {
        if(!this.active.getAndSet(false)) {
            return;
        }

        this.eventBus.unregisterHandler(this);
    }

    @Override
    public @NonNull Class<T> eventClass() {
        return this.eventClass;
    }

    @Override
    public @NonNull PluginMetadata metadata() {
        return this.metadata;
    }

    @Override
    public @NonNull Consumer<? super T> handler() {
        return this.consumer;
    }

    @Override
    public void invoke(@NonNull T event) throws Throwable {
        this.consumer.accept(event);
    }

}
