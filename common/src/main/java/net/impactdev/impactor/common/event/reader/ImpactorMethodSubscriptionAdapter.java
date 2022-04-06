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

package net.impactdev.impactor.common.event.reader;

import com.google.common.base.MoreObjects;
import net.impactdev.impactor.api.event.ImpactorEvent;
import net.impactdev.impactor.api.event.annotations.Subscribe;
import net.impactdev.impactor.api.event.listener.ImpactorEventListener;
import net.kyori.event.EventBus;
import net.kyori.event.EventSubscriber;
import net.kyori.event.ReifiedEvent;
import net.kyori.event.method.EventExecutor;
import net.kyori.event.method.MethodScanner;
import net.kyori.event.method.MethodSubscriptionAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.function.BiConsumer;

public class ImpactorMethodSubscriptionAdapter implements MethodSubscriptionAdapter<ImpactorEventListener> {

    private final EventBus<ImpactorEvent> bus;
    private final EventExecutor.Factory<ImpactorEvent, ImpactorEventListener> factory;
    private final MethodScanner<ImpactorEventListener> scanner;

    public ImpactorMethodSubscriptionAdapter(final @NotNull EventBus<ImpactorEvent> bus, final EventExecutor.@NotNull Factory<ImpactorEvent, ImpactorEventListener> factory) {
        this(bus, factory, new ImpactorMethodScanner());
    }

    public ImpactorMethodSubscriptionAdapter(final @NotNull EventBus<ImpactorEvent> bus, final EventExecutor.@NotNull Factory<ImpactorEvent, ImpactorEventListener> factory, final @NotNull MethodScanner<ImpactorEventListener> scanner) {
        this.bus = bus;
        this.factory = factory;
        this.scanner = scanner;
    }

    @Override
    public void register(@NotNull ImpactorEventListener listener) {
        this.locateSubscribers(listener, this.bus::register);
    }

    @Override
    public void unregister(@NotNull ImpactorEventListener listener) {
        this.bus.unregister(h -> h instanceof MethodEventSubscriber && ((MethodEventSubscriber) h).listener() == listener);
    }

    @SuppressWarnings("unchecked")
    private void locateSubscribers(@NotNull final ImpactorEventListener listener, final BiConsumer<Class<? extends ImpactorEvent>, EventSubscriber<ImpactorEvent>> consumer) {
        Method[] methods = listener.getClass().getDeclaredMethods();

        for(Method method : methods) {
            if(this.scanner.shouldRegister(listener, method)) {
                if(method.getParameterCount() != 1) {
                    throw new EventSubscriptionFailedException("Method must have only one parameter");
                }

                Class<?> methodParameterType = method.getParameterTypes()[0];
                if(!ImpactorEvent.class.isAssignableFrom(methodParameterType)) {
                    throw new EventSubscriptionFailedException("Method parameter type does not extend ImpactorEvent (" + methodParameterType + ")");
                }

                EventExecutor<ImpactorEvent, ImpactorEventListener> executor;
                try {
                    executor = this.factory.create(listener, method);
                } catch (Exception e) {
                    throw new EventSubscriptionFailedException("Error whilst creating an event subscriber for method '" + method + "'", e);
                }

                int order = this.scanner.postOrder(listener, method);
                boolean consumeCancelled = this.scanner.consumeCancelledEvents(listener, method);
                consumer.accept(
                        (Class<? extends ImpactorEvent>) methodParameterType,
                        new MethodEventSubscriber(
                                (Class<? extends ImpactorEvent>) methodParameterType,
                                executor,
                                method,
                                listener,
                                order,
                                consumeCancelled
                        )
                );
            }
        }
    }

    public static class EventSubscriptionFailedException extends RuntimeException {

        public EventSubscriptionFailedException(String message) {
            super(message);
        }

        public EventSubscriptionFailedException(String message, Throwable cause) {
            super(message, cause);
        }

    }

    private static final class ImpactorMethodScanner implements MethodScanner<ImpactorEventListener> {

        @Override
        public boolean shouldRegister(@NotNull ImpactorEventListener listener, @NotNull Method method) {
            return method.getAnnotation(Subscribe.class) != null;
        }

        @Override
        public int postOrder(@NotNull ImpactorEventListener listener, @NotNull Method method) {
            return method.getAnnotation(Subscribe.class).order().ordinal();
        }

        @Override
        public boolean consumeCancelledEvents(@NotNull ImpactorEventListener listener, @NotNull Method method) {
            return method.getAnnotation(Subscribe.class).ignoreCancelled();
        }

    }

    private static final class MethodEventSubscriber implements EventSubscriber<ImpactorEvent> {

        private final Class<? extends ImpactorEvent> event;
        private final @Nullable Type generic;

        private final EventExecutor<ImpactorEvent, ImpactorEventListener> executor;
        private final Method method;
        private final ImpactorEventListener listener;
        private final int postOrder;
        private final boolean includeCancelled;

        MethodEventSubscriber(final Class<? extends ImpactorEvent> eventClass, @NotNull final EventExecutor<ImpactorEvent, ImpactorEventListener> executor, @NotNull Method method, @NotNull final ImpactorEventListener listener, final int postOrder, final boolean includeCancelled) {
            this.event = eventClass;
            this.executor = executor;
            this.method = method;
            this.listener = listener;
            this.postOrder = postOrder;
            this.includeCancelled = includeCancelled;

            if(ReifiedEvent.class.isAssignableFrom(eventClass)) {
                Type generic = method.getGenericParameterTypes()[0];
                if(generic instanceof ParameterizedType) {
                    this.generic = ((ParameterizedType) generic).getActualTypeArguments()[0];
                } else {
                    this.generic = null;
                }
            } else {
                this.generic = null;
            }
        }

        @NotNull
        ImpactorEventListener listener() {
            return this.listener;
        }

        @Override
        public @Nullable Type genericType() {
            return this.generic;
        }

        @Override
        public void invoke(@NotNull ImpactorEvent event) throws Throwable {
            this.executor.invoke(this.listener, event);
        }

        @Override
        public int postOrder() {
            return this.postOrder;
        }

        @Override
        public boolean consumeCancelledEvents() {
            return this.includeCancelled;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MethodEventSubscriber that = (MethodEventSubscriber) o;
            return postOrder == that.postOrder &&
                    includeCancelled == that.includeCancelled &&
                    event.equals(that.event) &&
                    executor.equals(that.executor) &&
                    listener.equals(that.listener);
        }

        @Override
        public int hashCode() {
            return Objects.hash(event, executor, listener, postOrder, includeCancelled);
        }

        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("event", this.event)
                    .add("executor", this.executor)
                    .add("listener", this.listener)
                    .add("priority", this.postOrder)
                    .add("includeCancelled", this.includeCancelled)
                    .toString();
        }
    }
}
