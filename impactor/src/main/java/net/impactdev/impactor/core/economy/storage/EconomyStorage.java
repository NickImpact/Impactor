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

package net.impactdev.impactor.core.economy.storage;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Multimap;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.storage.Storage;
import net.impactdev.impactor.api.utility.ExceptionPrinter;
import net.impactdev.impactor.api.utility.printing.PrettyPrinter;
import net.impactdev.impactor.core.plugin.BaseImpactorPlugin;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

public final class EconomyStorage implements Storage {

    private final EconomyStorageImplementation implementation;
    private final Cache<AccountKey, Account> accounts;

    public EconomyStorage(EconomyStorageImplementation implementation) {
        this.implementation = implementation;
        this.accounts = Caffeine.newBuilder()
                .expireAfterAccess(1, TimeUnit.HOURS)
                .build();
    }

    @Override
    public void init() throws Exception {
        this.implementation.init();
    }

    @Override
    public void shutdown() throws Exception {
        this.implementation.shutdown();
    }

    @Override
    public CompletableFuture<Void> meta(PrettyPrinter printer) {
        return run(() -> this.implementation.meta(printer));
    }

    public CompletableFuture<Account> account(Currency currency, UUID uuid, Account.AccountModifier modifier) {
        Account account = this.accounts.getIfPresent(new AccountKey(currency, uuid));
        if(account != null) {
            return CompletableFuture.completedFuture(account);
        }

        return supply(() -> this.implementation.account(currency, uuid, modifier));
    }

    public CompletableFuture<Boolean> save(Account account) {
        return supply(() -> this.implementation.save(account));
    }

    public CompletableFuture<Multimap<Currency, Account>> accounts() {
        return supply(this.implementation::accounts);
    }

    public CompletableFuture<Boolean> purge() {
        return supply(this.implementation::purge);
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }

    @FunctionalInterface
    private interface ThrowingSupplier<T> {
        T supply() throws Exception;
    }

    private static CompletableFuture<Void> run(ThrowingRunnable runnable) {
        return CompletableFuture.runAsync(() -> {
            try {
                runnable.run();
            } catch (Exception e) {
                ExceptionPrinter.print(BaseImpactorPlugin.instance().logger(), e);
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new CompletionException(e);
            }
        }, Impactor.instance().scheduler().async());
    }

    private static <T> CompletableFuture<T> supply(ThrowingSupplier<T> supplier) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return supplier.supply();
            } catch (Exception e) {
                ExceptionPrinter.print(BaseImpactorPlugin.instance().logger(), e);
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new CompletionException(e);
            }
        }, Impactor.instance().scheduler().async());
    }

    private static final class AccountKey {
        private final Currency currency;
        private final UUID owner;

        public AccountKey(Currency currency, UUID owner) {
            this.currency = currency;
            this.owner = owner;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AccountKey that = (AccountKey) o;
            return currency.equals(that.currency) && owner.equals(that.owner);
        }

        @Override
        public int hashCode() {
            return Objects.hash(currency, owner);
        }
    }
}
