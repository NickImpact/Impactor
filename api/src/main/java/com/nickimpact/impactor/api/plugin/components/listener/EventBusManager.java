package com.nickimpact.impactor.api.plugin.components.listener;

import java.util.function.Consumer;

public interface EventBusManager<T> {

    T getEventBus();

    void register(Object object);

    Consumer<T> handleUnregistration(Object object);

}
