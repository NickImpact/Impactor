package com.nickimpact.impactor.api.registry;

import com.nickimpact.impactor.api.utilities.Builder;

import java.util.function.Supplier;

public interface Registry {

    <T> void register(Class<T> type, T value);

    <T> T get(Class<T> type);

    <T extends Builder<?, ?>> void registerBuilderSupplier(Class<T> type, Supplier<? extends T> builder);

    <T extends Builder<?, ?>> T createBuilder(Class<T> type);

    class Provider<T> {

        private final T instance;

        public Provider(T instance) {
            this.instance = instance;
        }

        public T getInstance() {
            return instance;
        }

    }

}
