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

package net.impactdev.impactor.core.economy.transactions.composers;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.api.economy.transactions.composer.TransactionComposer;
import net.impactdev.impactor.api.economy.transactions.details.EconomyResultType;
import net.impactdev.impactor.api.economy.transactions.EconomyTransaction;
import net.impactdev.impactor.api.economy.transactions.details.EconomyTransactionType;
import net.impactdev.impactor.core.economy.accounts.ImpactorAccount;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public final class BaseTransactionComposer implements TransactionComposer {

    private Account account;
    private BigDecimal amount;
    private EconomyTransactionType type;
    private final Map<EconomyResultType, Component> messages = Maps.newHashMap();

    public BigDecimal amount() {
        return this.amount;
    }

    public Map<EconomyResultType, Component> messages() {
        return this.messages;
    }

    @Override
    public TransactionComposer account(@NotNull Account account) {
        this.account = account;
        return this;
    }

    @Override
    public TransactionComposer amount(@NotNull BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    @Override
    public TransactionComposer type(@NotNull EconomyTransactionType type) {
        this.type = type;
        return this;
    }

    @Override
    public TransactionComposer message(@NotNull EconomyResultType type, @NotNull Component message) {
        this.messages.put(type, message);
        return this;
    }

    @Override
    public @NotNull CompletableFuture<@NotNull EconomyTransaction> send() {
        Preconditions.checkNotNull(this.account, "account");
        Preconditions.checkNotNull(this.type, "type");

        if(this.type != EconomyTransactionType.RESET) {
            Preconditions.checkNotNull(this.amount, "amount");
        }

        ImpactorAccount account = (ImpactorAccount) this.account;
        return Executors.of(this.type).transact(account, this);
    }
    
    @FunctionalInterface
    private interface Executor {

        CompletableFuture<EconomyTransaction> transact(ImpactorAccount account, BaseTransactionComposer composer);

    }

    private enum Executors {
        SET(EconomyTransactionType.SET, ImpactorAccount::set),
        WITHDRAW(EconomyTransactionType.WITHDRAW, ImpactorAccount::withdraw),
        DEPOSIT(EconomyTransactionType.DEPOSIT, ImpactorAccount::deposit),
        RESET(EconomyTransactionType.RESET, ImpactorAccount::reset);

        private final EconomyTransactionType type;
        private final Executor executor;

        Executors(final EconomyTransactionType type, Executor executor) {
            this.type = type;
            this.executor = executor;
        }

        public static Executors of(EconomyTransactionType type) {
            return Arrays.stream(values()).filter(executor -> executor.type == type)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("No valid executor registered"));
        }

        public CompletableFuture<EconomyTransaction> transact(ImpactorAccount account, BaseTransactionComposer composer) {
            return this.executor.transact(account, composer);
        }
    }
}
