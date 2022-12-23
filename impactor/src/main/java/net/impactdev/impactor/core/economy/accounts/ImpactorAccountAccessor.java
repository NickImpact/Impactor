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

package net.impactdev.impactor.core.economy.accounts;

import com.google.common.collect.Maps;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.economy.EconomyService;
import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.api.economy.accounts.AccountAccessor;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.core.economy.ImpactorEconomyService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class ImpactorAccountAccessor implements AccountAccessor {

    private final UUID uuid;

    public ImpactorAccountAccessor(final UUID uuid) {
        this.uuid = uuid;
    }

    public UUID uuid() {
        return this.uuid;
    }

    @Override
    public CompletableFuture<Account> account(Currency currency) {
        ImpactorEconomyService service = (ImpactorEconomyService) Impactor.instance().services().provide(EconomyService.class);
        return service.storage().account(this.uuid, currency);
    }

    @Override
    public CompletableFuture<Map<Currency, Account>> accounts() {
        return CompletableFuture.supplyAsync(() ->
                Impactor.instance().services().provide(EconomyService.class).currencies().registered()
        ).thenApply(currencies -> {
            Map<Currency, Account> accounts = Maps.newHashMap();
            currencies.forEach(currency -> accounts.put(currency, this.account(currency).join()));

            return accounts;
        });
    }
}
