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

package net.impactdev.impactor.api.placeholders;

import io.leangen.geantyref.TypeToken;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.builders.Builder;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;

public interface PlaceholderSources {

    static PlaceholderSources empty() {
        return PlaceholderSources.builder().build();
    }

    <T> PlaceholderSources append(Class<T> type, Supplier<T> supplier);

    <T> PlaceholderSources append(TypeToken<T> type, Supplier<T> supplier);

    <T> PlaceholderSources appendIfAbsent(Class<T> type, Supplier<T> supplier);

    <T> PlaceholderSources appendIfAbsent(TypeToken<T> type, Supplier<T> supplier);

    <T> Optional<T> getSource(Class<T> type);

    <T> Optional<T> getSource(TypeToken<T> type);

    Collection<Supplier<?>> suppliers();

    static SourceBuilder builder() {
        return Impactor.getInstance().getRegistry().createBuilder(SourceBuilder.class);
    }

    interface SourceBuilder extends Builder<PlaceholderSources> {

        <T> SourceBuilder append(Class<T> type, Supplier<T> supplier);

        <T> SourceBuilder append(TypeToken<T> type, Supplier<T> supplier);

        <T> SourceBuilder appendIfAbsent(Class<T> type, Supplier<T> supplier);

        <T> SourceBuilder appendIfAbsent(TypeToken<T> type, Supplier<T> supplier);

        SourceBuilder from(PlaceholderSources sources);

    }

}
