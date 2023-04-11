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

package net.impactdev.impactor.core.translations.builders;

import net.impactdev.impactor.api.platform.plugins.PluginMetadata;
import net.impactdev.impactor.api.text.TextProcessor;
import net.impactdev.impactor.api.translations.TranslationManager;
import net.impactdev.impactor.api.translations.repository.TranslationRepository;
import net.impactdev.impactor.core.translations.ImpactorTranslationManager;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Locale;
import java.util.function.Supplier;

public class ImpactorTranslationManagerBuilder implements TranslationManager.TranslationManagerBuilder {

    public TranslationRepository repository;
    public Locale defaultLocale;
    public Path path;
    public TextProcessor processor;
    public PluginMetadata metadata;
    public Supplier<InputStream> supplier;

    @Override
    public TranslationManager.TranslationManagerBuilder repository(@NotNull TranslationRepository repository) {
        this.repository = repository;
        return this;
    }

    @Override
    public TranslationManager.TranslationManagerBuilder defaultLocale(@NotNull Locale locale) {
        this.defaultLocale = locale;
        return this;
    }

    @Override
    public TranslationManager.TranslationManagerBuilder path(@NotNull Path path) {
        this.path = path;
        return this;
    }

    @Override
    public TranslationManager.TranslationManagerBuilder processor(@NotNull TextProcessor processor) {
        this.processor = processor;
        return this;
    }

    @Override
    public TranslationManager.TranslationManagerBuilder provided(@NotNull Supplier<InputStream> supplier) {
        this.supplier = supplier;
        return this;
    }

    @Override
    public TranslationManager build() {
        return new ImpactorTranslationManager(this);
    }
}
