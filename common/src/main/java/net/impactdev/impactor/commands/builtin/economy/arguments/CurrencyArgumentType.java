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

package net.impactdev.impactor.commands.builtin.economy.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.services.economy.EconomyService;
import net.impactdev.impactor.api.services.economy.currency.Currency;
import net.impactdev.impactor.commands.builtin.economy.exceptions.EconomyExceptionTypes;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class CurrencyArgumentType implements ArgumentType<String> {

    @Override
    @SuppressWarnings("PatternValidation")
    public String parse(StringReader reader) throws CommandSyntaxException {
        EconomyService service = Impactor.instance().services().provide(EconomyService.class);
        String input = reader.readString().toLowerCase();
        Optional<Currency> target = service.currencies().currency(Key.key("impactor", input));
        if(!target.isPresent()) {
            throw EconomyExceptionTypes.INVALID_CURRENCY.apply(input).create();
        }

        return input;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        EconomyService service = Impactor.instance().services().provide(EconomyService.class);
        Set<Currency> currencies = service.currencies().registered();

        for(Currency currency : currencies) {
            String input = builder.getRemaining().toLowerCase();
            PlainTextComponentSerializer serializer = PlainTextComponentSerializer.plainText();

            String singular = serializer.serialize(currency.name());
            if(singular.startsWith(input)) {
                builder.suggest(singular);
            }

            String plural = serializer.serialize(currency.plural());
            if(plural.startsWith(input)) {
                builder.suggest(plural);
            }
        }

        return builder.buildFuture();
    }
}
