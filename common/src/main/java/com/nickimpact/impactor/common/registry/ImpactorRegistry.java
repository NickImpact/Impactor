package com.nickimpact.impactor.common.registry;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.nickimpact.impactor.api.registry.Registry;
import com.nickimpact.impactor.api.utilities.Builder;

import java.util.Map;
import java.util.function.Supplier;

public final class ImpactorRegistry implements Registry {

    private static final Map<Class<?>, Supplier<?>> builders = Maps.newHashMap();
    private static final Map<Class<?>, Provider<?>> bindings = Maps.newHashMap();

    @Override
    public <T> void register(Class<T> type, T value) {
        Preconditions.checkNotNull(type, "Input type was null");
        Preconditions.checkNotNull(value, "Input value type was null");
        bindings.put(type, new Provider<>(value));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> type) {
        Preconditions.checkArgument(bindings.containsKey(type), "Could not locate a matching registration for type: " + type.getCanonicalName());
        return (T) bindings.get(type).getInstance();
    }

    @Override
    public <T extends Builder<?, ?>> void registerBuilderSupplier(Class<T> type, Supplier<? extends T> builder) {
        Preconditions.checkArgument(!builders.containsKey(type), "Already registered a builder supplier for: " + type.getCanonicalName());
        builders.put(type, builder);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Builder<?, ?>> T createBuilder(Class<T> type) {
        Preconditions.checkNotNull(type, "Input builder type was null");
        final Supplier<?> supplier = builders.get(type);
        Preconditions.checkNotNull(supplier, "Could not find a Supplier for the provided builder type: " + type.getCanonicalName());
        return (T) supplier.get();
    }

}
