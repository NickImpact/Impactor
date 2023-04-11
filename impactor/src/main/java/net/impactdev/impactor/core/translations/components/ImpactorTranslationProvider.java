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

package net.impactdev.impactor.core.translations.components;

import net.impactdev.impactor.api.platform.audience.LocalizedAudience;
import net.impactdev.impactor.api.translations.TranslationManager;
import net.impactdev.impactor.api.translations.TranslationProvider;
import net.impactdev.impactor.api.utility.Context;
import net.impactdev.impactor.core.translations.ImpactorTranslationManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class ImpactorTranslationProvider<T> implements TranslationProvider<T> {

    private final TranslationManager manager;
    private final String key;

    public ImpactorTranslationProvider(TranslationManager manager, String key) {
        this.manager = manager;
        this.key = key;
    }

    @Override
    public T resolve(@NotNull Locale locale, @NotNull Context context) {
        return this.translation(locale).build(this.manager.processor(), context);
    }

    @Override
    public void send(@NotNull Audience audience, @NotNull Context context) {
        Locale target = this.manager.defaultLocale();
        if(audience instanceof LocalizedAudience) {
            target = ((LocalizedAudience) audience).locale();
        }

        Translation<T> resolved = this.translation(target);
        if(resolved == null) {
            audience.sendMessage(Component.text("Invalid translation: ").color(NamedTextColor.GRAY).append(Component.text(this.key).color(NamedTextColor.RED)));
            return;
        }
        resolved.send(audience, this.manager.processor(), context);
    }

    private Translation<T> translation(final @NotNull Locale locale) {
        return ((ImpactorTranslationManager) this.manager).fetch(locale).translation(this.key);
    }

    public static class TranslationProviderFactory implements Factory {

        @Override
        public <T> TranslationProvider<T> create(@NotNull TranslationManager manager, @NotNull String key) {
            return new ImpactorTranslationProvider<>(manager, key);
        }
    }

}
