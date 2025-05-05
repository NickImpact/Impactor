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

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.economy.transactions.details.EconomyTransactionType;
import net.impactdev.impactor.core.economy.storage.EconomyStorage;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public final class AccountManager {

    private final AsyncLoadingCache<AccountKey, Account> accounts;

    public AccountManager(EconomyStorage storage) {
        this.accounts = Caffeine.newBuilder()
                .expireAfterAccess(10, TimeUnit.SECONDS)
                .buildAsync((key, executor) -> storage.account(key.currency, key.uuid, builder -> builder));
    }

    public Optional<Account> accountIfPresent(final UUID uuid, final Currency currency) {
        return Optional.ofNullable(this.accounts.synchronous().getIfPresent(new AccountKey(uuid, currency)));
    }

    public void update(ImpactorAccount account, @Nullable BigDecimal amount, EconomyTransactionType type) {
        account.setViaNetworking(amount, type);
    }

    public CompletableFuture<Account> account(final UUID uuid, final Currency currency) {
        return this.accounts.get(new AccountKey(uuid, currency));
    }

    public void invalidate(final UUID uuid, final Currency currency) {
        this.accounts.synchronous().invalidate(new AccountKey(uuid, currency));
    }

    private record AccountKey(UUID uuid, Currency currency) { }

}
