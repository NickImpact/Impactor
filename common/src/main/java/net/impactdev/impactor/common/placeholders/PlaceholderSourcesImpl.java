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

package net.impactdev.impactor.common.placeholders;

import com.google.common.collect.Maps;
import io.leangen.geantyref.TypeToken;
import net.impactdev.impactor.api.placeholders.PlaceholderSources;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class PlaceholderSourcesImpl implements PlaceholderSources {

    private final Map<TypeToken<?>, Supplier<?>> sources = Maps.newHashMap();

    private PlaceholderSourcesImpl(PlaceholderSourcesBuilderImpl builder) {
        this.sources.putAll(builder.sources);
    }

    @Override
    public <T> Optional<T> getSource(Class<T> type) {
        return this.getSource(TypeToken.get(type));
    }

    @Override
    public <T> Optional<T> getSource(TypeToken<T> type) {
        return Optional.ofNullable((T) this.sources.get(type).get());
    }

    @Override
    public Collection<Supplier<?>> suppliers() {
        return this.sources.values();
    }

    public static class PlaceholderSourcesBuilderImpl implements SourceBuilder {

        private final Map<TypeToken<?>, Supplier<?>> sources = Maps.newHashMap();

        @Override
        public <T> SourceBuilder append(Class<T> type, Supplier<T> supplier) {
            return this.append(TypeToken.get(type), supplier);
        }

        @Override
        public <T> SourceBuilder append(TypeToken<T> type, Supplier<T> supplier) {
            this.sources.put(type, supplier);
            return this;
        }

        @Override
        public <T> SourceBuilder appendIfAbsent(Class<T> type, Supplier<T> supplier) {
            this.sources.putIfAbsent(TypeToken.get(type), supplier);
            return this;
        }

        @Override
        public SourceBuilder from(PlaceholderSources input) {
            this.sources.putAll(((PlaceholderSourcesImpl) input).sources);
            return this;
        }

        @Override
        public PlaceholderSources build() {
            return new PlaceholderSourcesImpl(this);
        }
    }
}
