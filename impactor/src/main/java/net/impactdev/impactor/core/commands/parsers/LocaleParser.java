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

import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Lists;
import net.impactdev.impactor.api.commands.CommandSource;
import net.impactdev.impactor.api.translations.TranslationManager;
import net.impactdev.impactor.api.translations.metadata.LanguageInfo;
import net.impactdev.impactor.core.translations.internal.ImpactorTranslations;
import org.apache.commons.lang3.LocaleUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class LocaleParser implements ArgumentParser<CommandSource, Locale> {

    @Override
    public @NonNull ArgumentParseResult<@NonNull Locale> parse(@NonNull CommandContext<@NonNull CommandSource> context, @NonNull Queue<@NonNull String> args) {
        String input = args.peek();
        if (input == null) {
            return ArgumentParseResult.failure(new NoInputProvidedException(
                    LocaleParser.class,
                    context
            ));
        }

        Locale locale = TranslationManager.parseLocale(input);
        if(!LocaleUtils.isAvailableLocale(locale)) {
            return ArgumentParseResult.failure(new IllegalArgumentException("Invalid locale: " + input));
        }

        args.remove();
        return ArgumentParseResult.success(locale);
    }

    @Override
    public @NonNull List<@NonNull String> suggestions(@NonNull CommandContext<CommandSource> context, @NonNull String input) {
        List<String> options = Lists.newArrayList();
        Set<LanguageInfo> available = ImpactorTranslations.MANAGER.repository().available().join();
        available.forEach(info -> {
            if(info.id().toLowerCase().startsWith(input.toLowerCase())) {
                options.add(info.id());
            }
        });

        return options;
    }
}
