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

package net.impactdev.impactor.api.utilities.context;

import com.google.common.collect.Maps;
import io.leangen.geantyref.TypeToken;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.utilities.printing.PrettyPrinter;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public final class Context implements PrettyPrinter.IPrettyPrintable {

    private final Map<TypeToken<?>, Object> context = Maps.newHashMap();

    public static Context empty() {
        return new Context();
    }

    public Context with(Context child) {
        this.context.putAll(child.context);
        return this;
    }

    /**
     * Appends the associated value alongside its class typing to this context. This will override any
     * currently provided values with the same typing.
     *
     * @param type The type to associate with this context
     * @param value The actual value represented by the typing
     * @param <T> The type of the field being assigned to the context
     * @return The updated context
     */
    public <T> Context append(Class<T> type, T value) {
        return this.append(Impactor.instance().factories().provide(TypeTokenFactory.class).get(type), value);
    }

    /**
     * Appends the associated value alongside its class typing to this context. This will override any
     * currently provided values with the same typing.
     *
     * @param type The type to associate with this context
     * @param value The actual value represented by the typing
     * @param <T> The type of the field being assigned to the context
     * @return The updated context
     */
    public <T> Context append(TypeToken<T> type, T value) {
        this.context.put(type, value);
        return this;
    }

    /**
     * Checks to see if the context carries a particular typing. This is mostly used for data querying, and can
     * be used to avoid a possible {@link NoSuchElementException} from use of invoking {@link #require(Class)}.
     *
     * @param type The type to request from the context
     * @return <code>true</code> if the value is available in this context, <code>false</code> otherwise
     */
    public boolean has(Class<?> type) {
        return this.has(Impactor.instance().factories().provide(TypeTokenFactory.class).get(type));
    }

    /**
     * Checks to see if the context carries a particular typing. This is mostly used for data querying, and can
     * be used to avoid a possible {@link NoSuchElementException} from use of invoking {@link #require(Class)}.
     *
     * @param type The type to request from the context
     * @return <code>true</code> if the value is available in this context, <code>false</code> otherwise
     */
    public boolean has(TypeToken<?> type) {
        return this.context.containsKey(type);
    }

    /**
     * Attempts to locate and provide a value from the context. If the context does not contain
     * the requested typing, this will return an empty Optional.
     *
     * @param type The type to request from the context
     * @param <T> The actual typing to provide from the request
     * @return An optionally wrapped instance representing the provided type, or empty if not available
     */
    public <T> Optional<T> request(Class<T> type) {
        return this.request(Impactor.instance().factories().provide(TypeTokenFactory.class).get(type));
    }

    /**
     * Attempts to locate and provide a value from the context. If the context does not contain
     * the requested typing, this will return an empty Optional.
     *
     * @param type The type to request from the context
     * @param <T> The actual typing to provide from the request
     * @return An optionally wrapped instance representing the provided type, or empty if not available
     */
    public <T> Optional<T> request(TypeToken<T> type) {
        return Optional.ofNullable(this.context.get(type)).map(value -> (T) value);
    }

    /**
     * Enacts to the context that a field should be required for event consuming. If the requested value
     * is not found, this will trigger a {@link NoSuchElementException}.
     *
     * @param type The type to request from the context
     * @param <T> The actual typing to provide from the request
     * @return The value provided by the typing as assigned to this context
     * @throws NoSuchElementException If no value actually exists for the required typing.
     */
    public <T> T require(Class<T> type) throws NoSuchElementException {
        return this.require(Impactor.instance().factories().provide(TypeTokenFactory.class).get(type));
    }

    /**
     * Enacts to the context that a field should be required for event consuming. If the requested value
     * is not found, this will trigger a {@link NoSuchElementException}.
     *
     * @param type The type to request from the context
     * @param <T> The actual typing to provide from the request
     * @return The value provided by the typing as assigned to this context
     * @throws NoSuchElementException If no value actually exists for the required typing.
     */
    public <T> T require(TypeToken<T> type) throws NoSuchElementException {
        return this.request(type).orElseThrow(() -> {
            PrettyPrinter debugger = new PrettyPrinter(80);
            debugger.title("Context Mapping").hr();
            debugger.add(this.context);
            debugger.print(System.out);

            return new NoSuchElementException("Missing Type: " + type.getType().getTypeName());
        });
    }

    public int size() {
        return this.context.size();
    }

    public boolean isEmpty() {
        return this.context.isEmpty();
    }

    @Override
    public void print(PrettyPrinter printer) {
        for(TypeToken<?> typing : this.context.keySet()) {
            printer.add(typing.getType().getTypeName());
        }
    }

}
