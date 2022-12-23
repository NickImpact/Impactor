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

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.api.economy.accounts.AccountAccessor;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.storage.Storage;
import net.impactdev.impactor.api.utility.ExceptionPrinter;
import net.impactdev.impactor.api.utility.printing.PrettyPrinter;
import net.impactdev.impactor.core.plugin.BaseImpactorPlugin;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public final class EconomyStorage implements Storage {

    private final EconomyStorageImplementation implementation;

    public EconomyStorage(EconomyStorageImplementation implementation) {
        this.implementation = implementation;
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
    public CompletableFuture<PrettyPrinter.IPrettyPrintable> meta() {
        return CompletableFuture.supplyAsync(this.implementation::meta);
    }

    public CompletableFuture<Account> account(UUID uuid, Currency currency) {
        return supply(() -> this.implementation.account(uuid, currency));
    }

    public CompletableFuture<Boolean> saveAccount(UUID uuid, Account account) {
        return supply(() -> this.implementation.saveAccount(uuid, account));
    }

    public CompletableFuture<List<AccountAccessor>> accessors() {
        return supply(this.implementation::accessors);
    }

    public CompletableFuture<Boolean> purge() {
        return supply(this.implementation::purge);
    }

    @FunctionalInterface
    private interface ThrowingSupplier<T> {
        T supply() throws Exception;
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
}
