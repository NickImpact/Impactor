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
import net.impactdev.impactor.api.utilities.printing.PrettyPrinter;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public final class ContextualMapping implements PrettyPrinter.IPrettyPrintable {

    private final Map<TypeToken<?>, Provider<?>> context = Maps.newHashMap();

    public <T> ContextualMapping put(Class<T> type, T instance) {
        return this.put(TypeToken.get(type), instance);
    }

    public <T> ContextualMapping put(TypeToken<T> type, T instance) {
        this.context.put(type, new Provider<>(instance));
        return this;
    }

    public boolean containsKey(Class<?> type) {
        return this.containsKey(TypeToken.get(type));
    }

    public boolean containsKey(TypeToken<?> type) {
        return this.context.containsKey(type);
    }

    public <T> Optional<T> get(Class<T> type) {
        return this.get(TypeToken.get(type));
    }

    public <T> Optional<T> get(TypeToken<T> type) {
        return Optional.ofNullable(this.context.get(type))
                .map(provider -> (Provider<T>) provider)
                .map(Provider::instance);
    }

    public <T> T require(Class<T> type) {
        return this.get(type).orElseThrow(NoSuchElementException::new);
    }

    public <T> T require(TypeToken<T> type) {
        return this.get(type).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public void print(PrettyPrinter printer) {
        for(TypeToken<?> typing : this.context.keySet()) {
            printer.add(typing.getType().getTypeName());
        }
    }

}
