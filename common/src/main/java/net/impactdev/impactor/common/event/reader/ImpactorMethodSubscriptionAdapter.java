package net.impactdev.impactor.common.event.reader;

import com.google.common.base.MoreObjects;
import net.impactdev.impactor.api.event.ImpactorEvent;
import net.impactdev.impactor.api.event.annotations.Subscribe;
import net.impactdev.impactor.api.event.listener.ImpactorEventListener;
import net.kyori.event.EventBus;
import net.kyori.event.EventSubscriber;
import net.kyori.event.method.EventExecutor;
import net.kyori.event.method.MethodScanner;
import net.kyori.event.method.MethodSubscriptionAdapter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.function.BiConsumer;

public class ImpactorMethodSubscriptionAdapter implements MethodSubscriptionAdapter<ImpactorEventListener> {

    private final EventBus<ImpactorEvent> bus;
    private final EventExecutor.Factory<ImpactorEvent, ImpactorEventListener> factory;
    private final MethodScanner<ImpactorEventListener> scanner;

    public ImpactorMethodSubscriptionAdapter(final @NonNull EventBus<ImpactorEvent> bus, final EventExecutor.@NonNull Factory<ImpactorEvent, ImpactorEventListener> factory) {
        this(bus, factory, new ImpactorMethodScanner());
    }

    public ImpactorMethodSubscriptionAdapter(final @NonNull EventBus<ImpactorEvent> bus, final EventExecutor.@NonNull Factory<ImpactorEvent, ImpactorEventListener> factory, final @NonNull MethodScanner<ImpactorEventListener> scanner) {
        this.bus = bus;
        this.factory = factory;
        this.scanner = scanner;
    }

    @Override
    public void register(@NonNull ImpactorEventListener listener) {
        this.locateSubscribers(listener, this.bus::register);
    }

    @Override
    public void unregister(@NonNull ImpactorEventListener listener) {
        this.bus.unregister(h -> h instanceof MethodEventSubscriber && ((MethodEventSubscriber) h).listener() == listener);
    }

    @SuppressWarnings("unchecked")
    private void locateSubscribers(@NonNull final ImpactorEventListener listener, final BiConsumer<Class<? extends ImpactorEvent>, EventSubscriber<ImpactorEvent>> consumer) {
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
                consumer.accept((Class<? extends ImpactorEvent>) methodParameterType, new MethodEventSubscriber((Class<? extends ImpactorEvent>) methodParameterType, method, executor, listener, order, consumeCancelled));
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
        public boolean shouldRegister(@NonNull ImpactorEventListener listener, @NonNull Method method) {
            return method.getAnnotation(Subscribe.class) != null;
        }

        @Override
        public int postOrder(@NonNull ImpactorEventListener listener, @NonNull Method method) {
            return method.getAnnotation(Subscribe.class).order();
        }

        @Override
        public boolean consumeCancelledEvents(@NonNull ImpactorEventListener listener, @NonNull Method method) {
            return method.getAnnotation(Subscribe.class).ignoreCallcelled();
        }

    }

    private static final class MethodEventSubscriber implements EventSubscriber<ImpactorEvent> {

        private final Class<? extends ImpactorEvent> event;

        @Nullable private final Type generic;
        private final EventExecutor<ImpactorEvent, ImpactorEventListener> executor;
        private final ImpactorEventListener listener;
        private final int postOrder;
        private final boolean includeCancelled;

        MethodEventSubscriber(final Class<? extends ImpactorEvent> eventClass, @NonNull final Method method, @NonNull final EventExecutor<ImpactorEvent, ImpactorEventListener> executor, @NonNull final ImpactorEventListener listener, final int postOrder, final boolean includeCancelled) {
            this.event = eventClass;
            this.generic = ImpactorEvent.Generic.class.isAssignableFrom(this.event) ? genericType(method.getGenericParameterTypes()[0]) : null;
            this.executor = executor;
            this.listener = listener;
            this.postOrder = postOrder;
            this.includeCancelled = includeCancelled;
        }

        @Nullable
        private static Type genericType(final Type type) {
            return type instanceof ParameterizedType ? ((ParameterizedType)type).getActualTypeArguments()[0] : null;
        }

        @NonNull
        ImpactorEventListener listener() {
            return this.listener;
        }

        @Override
        public void invoke(@NonNull ImpactorEvent event) throws Throwable {
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
        public Type genericType() {
            return this.generic;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MethodEventSubscriber that = (MethodEventSubscriber) o;
            return postOrder == that.postOrder &&
                    includeCancelled == that.includeCancelled &&
                    event.equals(that.event) &&
                    Objects.equals(generic, that.generic) &&
                    executor.equals(that.executor) &&
                    listener.equals(that.listener);
        }

        @Override
        public int hashCode() {
            return Objects.hash(event, generic, executor, listener, postOrder, includeCancelled);
        }

        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("event", this.event)
                    .add("generic", this.generic)
                    .add("executor", this.executor)
                    .add("listener", this.listener)
                    .add("priority", this.postOrder)
                    .add("includeCancelled", this.includeCancelled)
                    .toString();
        }
    }
}
