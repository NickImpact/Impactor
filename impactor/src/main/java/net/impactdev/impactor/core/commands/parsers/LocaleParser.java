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

package net.impactdev.impactor.core.commands.parsers;

import com.google.common.collect.Lists;
import net.impactdev.impactor.api.commands.CommandSource;
import net.impactdev.impactor.api.translations.TranslationManager;
import net.impactdev.impactor.api.translations.metadata.LanguageInfo;
import net.impactdev.impactor.core.translations.internal.ImpactorTranslations;
import org.apache.commons.lang3.LocaleUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider;
import org.incendo.cloud.suggestion.Suggestion;

import java.util.List;
import java.util.Locale;
import java.util.Set;

public class LocaleParser implements ArgumentParser<CommandSource, Locale>, BlockingSuggestionProvider<CommandSource> {

    @Override
    public @NonNull ArgumentParseResult<@NonNull Locale> parse(@NonNull CommandContext<@NonNull CommandSource> context, @NonNull CommandInput input) {
        String argument = input.peekString();
        Locale locale = TranslationManager.parseLocale(argument);
        if(!LocaleUtils.isAvailableLocale(locale)) {
            return ArgumentParseResult.failure(new IllegalArgumentException("Invalid locale: " + input));
        }

        input.readString();
        return ArgumentParseResult.success(locale);
    }

    @Override
    public @NonNull Iterable<@NonNull Suggestion> suggestions(@NonNull CommandContext<CommandSource> context, @NonNull CommandInput input) {
        List<Suggestion> options = Lists.newArrayList();
        Set<LanguageInfo> available = ImpactorTranslations.MANAGER.repository().available().join();
        available.forEach(info -> {
            if(info.id().toLowerCase().startsWith(input.peekString().toLowerCase())) {
                options.add(Suggestion.simple(info.id()));
            }
        });

        return options;
    }
}
