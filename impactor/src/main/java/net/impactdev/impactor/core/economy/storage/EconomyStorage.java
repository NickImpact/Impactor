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
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.platform.Platform;
import net.impactdev.impactor.api.platform.players.PlatformPlayerService;
import net.impactdev.impactor.api.storage.Storage;
import net.impactdev.impactor.api.utility.ExceptionPrinter;
import net.impactdev.impactor.api.utility.printing.PrettyPrinter;
import net.impactdev.impactor.core.plugin.BaseImpactorPlugin;
import net.impactdev.impactor.core.utility.future.ThrowingRunnable;
import net.impactdev.impactor.core.utility.future.ThrowingSupplier;

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

    @CanIgnoreReturnValue
    public CompletableFuture<Boolean> hasAccount(Currency currency, UUID uuid) {
        if(this.accounts.getIfPresent(AccountKey.of(currency, uuid)) != null) {
            return CompletableFuture.completedFuture(true);
        }

        return supply(() -> this.implementation.hasAccount(currency, uuid));
    }

    @CanIgnoreReturnValue
    public CompletableFuture<Account> account(Currency currency, UUID uuid, Account.AccountModifier modifier) {
        Account account = this.accounts.getIfPresent(AccountKey.of(currency, uuid));
        if(account != null) {
            return CompletableFuture.completedFuture(account);
        }

        return supply(() -> {
            Account result = this.implementation.account(currency, uuid, modifier);
            this.accounts.put(AccountKey.of(currency, uuid), result);

            return result;
        });
    }

    @CanIgnoreReturnValue
    public CompletableFuture<Void> save(Account account) {
        return run(() -> this.implementation.save(account));
    }

    @CanIgnoreReturnValue
    public CompletableFuture<Multimap<Currency, Account>> accounts() {
        Multimap<Currency, Account> results = ArrayListMultimap.create();
        this.accounts.asMap().forEach((key, account) -> results.put(key.currency, account));

        return run(() -> this.implementation.accounts(results)).thenApply(ignore -> results);
    }

    @CanIgnoreReturnValue
    public CompletableFuture<Void> delete(Currency currency, UUID uuid) {
        return run(() -> {
            this.implementation.delete(currency, uuid);
            this.accounts.invalidate(AccountKey.of(currency, uuid));
        });
    }

    @CanIgnoreReturnValue
    public CompletableFuture<Boolean> purge() {
        return supply(this.implementation::purge);
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

        public static AccountKey of(Currency currency, UUID owner) {
            return new AccountKey(currency, owner);
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
