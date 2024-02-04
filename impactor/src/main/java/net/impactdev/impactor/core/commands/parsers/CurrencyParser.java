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
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.commands.CommandSource;
import net.impactdev.impactor.api.economy.EconomyService;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.economy.currency.CurrencyProvider;
import net.impactdev.impactor.core.utility.future.Futures;
import net.kyori.adventure.key.Key;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class CurrencyParser implements ArgumentParser<CommandSource, Currency>, SuggestionProvider<CommandSource> {

    @Override
    public @NonNull ArgumentParseResult<@NonNull Currency> parse(@NonNull CommandContext<@NonNull CommandSource> context, @NonNull CommandInput input) {
        final String value = input.peekString();

        try {
            final Key key;
            if(value.contains(":")) {
                key = Key.key(value);
            } else {
                key = Key.key("impactor", value);
            }

            return Impactor.instance().services()
                    .provide(EconomyService.class)
                    .currencies()
                    .currency(key)
                    .map(currency -> {
                        input.readString();
                        return currency;
                    })
                    .map(ArgumentParseResult::success)
                    .orElseGet(() -> ArgumentParseResult.failure(
                            new IllegalArgumentException("Bad input: " + input)
                    ));

        } catch (Exception e) {
            return ArgumentParseResult.failure(new IllegalArgumentException("Bad input: " + input));
        }
    }

    @Override
    public @NonNull CompletableFuture<@NonNull Iterable<@NonNull Suggestion>> suggestionsFuture(@NonNull CommandContext<CommandSource> context, @NonNull CommandInput input) {
        String argument = input.peekString();
        return Futures.execute(() -> {
            List<String> results = Lists.newArrayList();

            CurrencyProvider currencies = Impactor.instance().services().provide(EconomyService.class).currencies();
            currencies.registered().forEach(currency -> {
                if(currency.key().value().startsWith(argument)) {
                    results.add(currency.key().value());
                    results.add(currency.key().asString());
                } else if(currency.key().asString().startsWith(argument)) {
                    results.add(currency.key().asString());
                }
            });

            return results.stream().map(Suggestion::simple).toList();
        });
    }
}
