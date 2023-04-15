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

package net.impactdev.impactor.core.translations.internal;

import com.google.common.collect.Maps;
import net.impactdev.impactor.api.text.TextProcessor;
import net.impactdev.impactor.api.translations.TranslationManager;
import net.impactdev.impactor.api.translations.TranslationProvider;
import net.impactdev.impactor.api.translations.repository.TranslationEndpoint;
import net.impactdev.impactor.api.translations.repository.TranslationRepository;
import net.impactdev.impactor.api.utility.Time;
import net.impactdev.impactor.core.plugin.BaseImpactorPlugin;
import net.impactdev.impactor.core.translations.TranslationsModule;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Paths;
import java.util.Locale;
import java.util.Map;

public interface ImpactorTranslations {

    TranslationManager MANAGER = TranslationManager.builder()
            .path(Paths.get("impactor").resolve("translations"))
            .fallback(Locale.US)
            .processor(TextProcessor.mini())
            .repository(TranslationRepository.builder()
                    .endpoint(TranslationEndpoint.LANGUAGE_SET, "https://metadata.impactdev.net/impactor/translations")
                    .endpoint(TranslationEndpoint.DOWNLOADABLE_LANGUAGE, "https://metadata.impactdev.net/impactor/translation/%s")
//                    .refreshWhen(() -> TranslationsModule.config.get(TranslationsConfig.AUTO_INSTALL))
////                    .maxBundleSize(TranslationsModule.config.get(TranslationsConfig.MAX_BUNDLE_SIZE))
//                    .maxCacheAge(TranslationsModule.config.get(TranslationsConfig.MAX_CACHE_AGE))
                    .build()
            )
            .provided(() -> BaseImpactorPlugin.instance().resource(root -> root.resolve("en_us.json")))
            .build();

    Map<String, TranslationProvider<?>> REGISTERED = Maps.newHashMap();

    // Economy
    TranslationProvider<Component> ECONOMY_BALANCE = create("economy.account.balance");
    TranslationProvider<Component> ECONOMY_TRANSACTION = create("economy.account.transaction");
    TranslationProvider<Component> ECONOMY_TRANSFER = create("economy.account.transfer");

    // Translations
    TranslationProvider<Component> TRANSLATIONS_SEARCHING = create("translations.searching");
    TranslationProvider<Component> TRANSLATIONS_INFO_HEADER_FOOTER = create("translations.info.header-footer");
    TranslationProvider<Component> TRANSLATIONS_INFO_LANGUAGE = create("translations.info.language");
    TranslationProvider<Component> TRANSLATIONS_INFO_PROGRESS = create("translations.info.progress");
    TranslationProvider<Component> TRANSLATIONS_INFO_CONTRIBUTOR_HEADER = create("translations.info.contributor.header");
    TranslationProvider<Component> TRANSLATIONS_INFO_CONTRIBUTOR_NAME = create("translations.info.contributor.name");
    TranslationProvider<Component> TRANSLATIONS_INVALID_LOCALE = create("translations.info.invalid-locale");

    TranslationProvider<Component> TRANSLATIONS_INSTALLING = create("translations.installing");
    TranslationProvider<Component> TRANSLATIONS_INSTALLING_LANGUAGE = create("translations.installing-language");
    TranslationProvider<Component> TRANSLATIONS_INSTALL_COMPLETE = create("translations.install-complete");
    TranslationProvider<Component> TRANSLATIONS_INSTALL_FAILED = create("translations.install-failed");

    static <T> TranslationProvider<T> create(final @NotNull String key) {
        TranslationProvider<T> provider = TranslationProvider.create(MANAGER, key);
        REGISTERED.put(key, provider);

        return provider;
    }
}
