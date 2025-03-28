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

package net.impactdev.impactor.api.core;

import com.google.common.base.Suppliers;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Represents a general provider responsible for providing instances of classes based on type request. The primary
 * use for this is down to exposed elements of APIs, where class details might not be publicly available
 */
public final class ServiceProvider {

    private static final ServiceProvider INSTANCE = new ServiceProvider();
    private final Map<Class<?>, Supplier<?>> registrations = new HashMap<>();

    @NotNull
    public static ServiceProvider instance() {
        return INSTANCE;
    }

    /**
     * Provides an instance of the given type, such that it has been registered to the overall provider. Attempting
     * to access a target which is not yet available will result in an IllegalArgumentException.
     *
     * @param target The target class to have provided
     * @return An instance of the requested target
     * @param <T> The type of the target
     * @throws IllegalArgumentException If the target has not been registered to the provider
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public <T> T provide(Class<T> target) {
        Supplier<?> supplier = this.registrations.get(target);
        if (supplier == null) {
            throw new IllegalArgumentException("Provider does not contain a supplier for type: " + target);
        }

        return (T) supplier.get();
    }

    /**
     * Registers a singleton to the provider. While stored as a supplier, the overall system will store it in a way
     * that ensures only the first get runs the internal supplier.
     *
     * @param target The target typing to register the instance under
     * @param instance An instance represented by the registration type
     * @param <T> The shared type between the registration and instance
     */
    public <T> void register(Class<T> target, T instance) {
        this.registrations.put(target, Suppliers.memoize(() -> instance));
    }

    /**
     * Registers a supplier to the provider. Each provision from this registration type provides the instance as
     * determined by the given supplier. Useful for things like builders.
     *
     * @param target The target typing to register the instance under
     * @param supplier The supplier for the typing
     * @param <T> The shared type between the registration and supplier
     */
    public <T> void register(Class<T> target, Supplier<T> supplier) {
        this.registrations.put(target, supplier);
    }

}
