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

package net.impactdev.impactor.core.economy.placeholders;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.base.Suppliers;
import net.impactdev.impactor.api.economy.EconomyService;
import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.platform.sources.PlatformSource;
import net.impactdev.impactor.api.text.placeholders.PlaceholderArguments;
import net.impactdev.impactor.api.text.placeholders.PlaceholderParser;
import net.impactdev.impactor.api.utility.Context;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class AccountPlaceholderParser implements PlaceholderParser {

    private static final Supplier<EconomyService> service = Suppliers.memoize(EconomyService::instance);

    private record AccountRequest(UUID uuid, Currency currency) {}

    private final AsyncLoadingCache<AccountRequest, Account> cache = Caffeine.newBuilder()
            .expireAfterAccess(1, TimeUnit.MINUTES)
            .buildAsync(request -> service.get().account(request.currency, request.uuid).join());

    @Override
    public @NotNull Component parse(@Nullable PlatformSource viewer, @NotNull Context context) {
        PlaceholderArguments arguments = context.require(PlaceholderArguments.class);
        Currency currency = this.currency(arguments);

        if(viewer == null) {
            return Component.empty();
        }

        Account account = this.cache.get(new AccountRequest(viewer.uuid(), currency)).getNow(null);
        if(account != null) {
            return currency.format(account.balance());
        }

        return Component.text("Fetching balance...");
    }

    @SuppressWarnings("PatternValidation")
    private Currency currency(PlaceholderArguments arguments) {
        if(arguments.hasNext()) {
            String namespace = arguments.popOrDefault();
            String value = arguments.popOrDefault();

            if(namespace == null || value == null) {
                return service.get().currencies().primary();
            }

            return service.get().currencies().currency(Key.key(namespace, value)).orElse(service.get().currencies().primary());
        }

        return service.get().currencies().primary();
    }
}
