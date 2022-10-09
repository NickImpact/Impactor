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

package net.impactdev.impactor.commands.sources;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.impactdev.impactor.api.commands.executors.CommandSource;
import net.impactdev.impactor.api.services.Service;
import org.jetbrains.annotations.NotNull;

public final class SourceTranslator implements Service {

    private final Cache<Class<?>, Translator<?>> translators = Caffeine.newBuilder().build();

    @Override
    public String getServiceName() {
        return "Source Translator";
    }

    public <T> void register(Class<T> type, Translator<T> translator) {
        this.translators.put(type, translator);
    }

    public <T> CommandSource translate(T delegate) throws IllegalArgumentException {
        Translator<T> translator = (Translator<T>) this.translators.getIfPresent(delegate.getClass());
        if(translator == null) {
            throw new IllegalArgumentException("Unable to locate a valid translator for type: " + delegate.getClass().getSimpleName());
        }

        return translator.translate(delegate);
    }

    @FunctionalInterface
    public interface Translator<S> {
        /**
         * Attempts to translate the given source into an Impactor {@link CommandSource}.
         *
         * @param delegate The delegate source we are translating
         * @return The resulting command source that represents the delegate
         */
        @NotNull CommandSource translate(S delegate);
    }
}
