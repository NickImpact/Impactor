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

import net.impactdev.impactor.api.text.TextProcessor;
import net.impactdev.impactor.api.translations.TranslationManager;
import net.impactdev.impactor.api.translations.TranslationProvider;
import net.impactdev.impactor.api.translations.repository.TranslationEndpoint;
import net.impactdev.impactor.api.translations.repository.TranslationRepository;
import net.impactdev.impactor.core.plugin.BaseImpactorPlugin;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Paths;
import java.util.Locale;

public interface ImpactorTranslations {

    TranslationManager MANAGER = TranslationManager.builder()
            .path(Paths.get("impactor").resolve("translations"))
            .defaultLocale(Locale.US)
            .processor(TextProcessor.mini())
            .repository(TranslationRepository.builder()
                    .endpoint(TranslationEndpoint.LANGUAGE_SET, "https://metadata.impactdev.net/impactor/translations")
                    .endpoint(TranslationEndpoint.DOWNLOADABLE_LANGUAGE, "https://metadata.impactdev.net/impactor/translation/%s")
                    .refreshRule(() -> true) // TODO - Config for auto install flag
                    .build()
            )
            .provided(() -> BaseImpactorPlugin.instance().resource(root -> root.resolve("en_us.json")))
            .build();

    TranslationProvider<Component> ECONOMY_BALANCE = TranslationProvider.create(MANAGER, "economy.account.balance");
    TranslationProvider<Component> ECONOMY_WITHDRAW = TranslationProvider.create(MANAGER, "economy.account.withdraw");
    TranslationProvider<Component> ECONOMY_DEPOSIT = TranslationProvider.create(MANAGER, "economy.account.deposit");
    TranslationProvider<Component> ECONOMY_TRANSFER = TranslationProvider.create(MANAGER, "economy.account.transfer");
    TranslationProvider<Component> ECONOMY_SET = TranslationProvider.create(MANAGER, "economy.account.set");
    TranslationProvider<Component> ECONOMY_RESET = TranslationProvider.create(MANAGER, "economy.account.reset");

    static <T> TranslationProvider<T> create(final @NotNull String key) {
        return TranslationProvider.create(MANAGER, key);
    }
}
