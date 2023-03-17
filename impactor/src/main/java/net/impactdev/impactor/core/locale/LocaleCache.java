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

package net.impactdev.impactor.core.locale;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import net.impactdev.impactor.api.utility.ExceptionPrinter;
import net.impactdev.impactor.core.plugin.BaseImpactorPlugin;
import org.apache.commons.lang3.LocaleUtils;

import java.util.Locale;
import java.util.function.Function;

public final class LocaleCache {

    private static final Function<String, Locale> LOCALE_TRANSLATOR = new Function<String, Locale>() {
        @Override
        public Locale apply(String key) {
            try {
                return LocaleUtils.toLocale(key);
            } catch (final IllegalArgumentException ignored) {
                // Ignore the first exception and try again, this time using a "fixed" key.
                final String fixedKey = this.fixLocaleKey(key);
                try {
                    return LocaleUtils.toLocale(fixedKey);
                } catch (final IllegalArgumentException e) {
                    ExceptionPrinter.print(BaseImpactorPlugin.instance().logger(), e);
                    throw e;
                }
            }
        }

        private String fixLocaleKey(final String key) {
            final String[] parts = key.split("_");
            if (parts.length == 2) {
                return parts[0] + '_' + parts[1].toUpperCase();
            }
            return key;
        }
    };

    private static final LoadingCache<String, Locale> LOCALE_CACHE = Caffeine.newBuilder().build(LOCALE_TRANSLATOR::apply);

    /**
     * Gets a locale from the cache.
     *
     * @param tag The locale tag
     * @return The locale
     */
    public static Locale getLocale(final String tag) {
        return LOCALE_CACHE.get(tag);
    }
}
