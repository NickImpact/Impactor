/*
 * This file is part of Impactor, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018-2021 NickImpact
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

package net.impactdev.impactor.common.registry;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import net.impactdev.impactor.api.registry.Registry;
import net.impactdev.impactor.api.utilities.Builder;

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
