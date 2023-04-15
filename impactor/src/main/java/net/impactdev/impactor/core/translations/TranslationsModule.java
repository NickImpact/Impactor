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

package net.impactdev.impactor.core.translations;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.configuration.Config;
import net.impactdev.impactor.api.logging.PluginLogger;
import net.impactdev.impactor.api.providers.BuilderProvider;
import net.impactdev.impactor.api.providers.FactoryProvider;
import net.impactdev.impactor.api.translations.TranslationManager;
import net.impactdev.impactor.api.translations.TranslationProvider;
import net.impactdev.impactor.api.translations.repository.TranslationRepository;
import net.impactdev.impactor.core.modules.ImpactorModule;
import net.impactdev.impactor.core.plugin.BaseImpactorPlugin;
import net.impactdev.impactor.core.translations.builders.ImpactorTranslationManagerBuilder;
import net.impactdev.impactor.core.translations.builders.ImpactorTranslationRepositoryBuilder;
import net.impactdev.impactor.core.translations.components.ImpactorTranslationProvider;
import net.impactdev.impactor.core.translations.internal.TranslationsConfig;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import java.nio.file.Paths;

public final class TranslationsModule implements ImpactorModule {

    @MonotonicNonNull
    public static Config config;

    @Override
    public void factories(FactoryProvider provider) {
        provider.register(TranslationProvider.Factory.class, new ImpactorTranslationProvider.TranslationProviderFactory());
    }

    @Override
    public void builders(BuilderProvider provider) {
        provider.register(TranslationManager.TranslationManagerBuilder.class, ImpactorTranslationManagerBuilder::new);
        provider.register(TranslationRepository.RepositoryBuilder.class, ImpactorTranslationRepositoryBuilder::new);
    }

    @Override
    public void init(Impactor impactor, PluginLogger logger) throws Exception {
        config = Config.builder()
                .provider(TranslationsConfig.class)
                .path(Paths.get("config").resolve("impactor").resolve("translations").resolve("translations.conf"))
                .provideIfMissing(() -> BaseImpactorPlugin.instance().resource(root -> root.resolve("configs").resolve("translations.conf")))
                .build();
    }
}
