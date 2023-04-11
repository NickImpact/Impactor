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

import net.impactdev.impactor.api.translations.repository.TranslationEndpoint;
import net.impactdev.impactor.api.translations.repository.TranslationRepository;
import net.impactdev.impactor.core.translations.repository.ImpactorTranslationRepository;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class ImpactorTranslationRepositoryBuilder implements TranslationRepository.RepositoryBuilder {

    public final Map<TranslationEndpoint, String> urls = new HashMap<>();
    public Supplier<Boolean> rule;
    public long maxBundleSize = 1048576L; // 1mb
    public long maxCacheAge = TimeUnit.HOURS.toMillis(24);

    @Override
    public TranslationRepository.RepositoryBuilder endpoint(@NotNull TranslationEndpoint endpoint, @NotNull String baseURL) {
        this.urls.put(endpoint, baseURL);
        return this;
    }

    @Override
    public TranslationRepository.RepositoryBuilder refreshRule(@NotNull Supplier<Boolean> rule) {
        this.rule = rule;
        return this;
    }

    @Override
    public TranslationRepository.RepositoryBuilder maxBundleSize(long size) {
        this.maxBundleSize = size;
        return this;
    }

    @Override
    public TranslationRepository.RepositoryBuilder maxCacheAge(long age) {
        this.maxCacheAge = age;
        return this;
    }

    @Override
    public TranslationRepository build() {
        return new ImpactorTranslationRepository(this);
    }
}
