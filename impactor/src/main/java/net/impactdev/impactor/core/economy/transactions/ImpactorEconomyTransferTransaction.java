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

package net.impactdev.impactor.core.economy.transactions;

import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.economy.transactions.details.EconomyResultType;
import net.impactdev.impactor.api.economy.transactions.EconomyTransferTransaction;
import net.impactdev.impactor.api.utility.builders.Builder;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.function.Supplier;

public final class ImpactorEconomyTransferTransaction implements EconomyTransferTransaction {

    private final Currency currency;
    private final Account from;
    private final Account to;
    private final BigDecimal amount;
    private final EconomyResultType result;
    private final Supplier<Component> message;

    public ImpactorEconomyTransferTransaction(TransactionBuilder builder) {
        this.currency = builder.currency;
        this.from = builder.from;
        this.to = builder.to;
        this.amount = builder.amount;
        this.result = builder.result;
        this.message = builder.message;
    }

    @Override
    public Currency currency() {
        return this.currency;
    }

    @Override
    public Account from() {
        return this.from;
    }

    @Override
    public Account to() {
        return this.to;
    }

    @Override
    public BigDecimal amount() {
        return this.amount;
    }

    @Override
    public EconomyResultType result() {
        return this.result;
    }

    @Override
    public @Nullable Supplier<Component> message() {
        return this.message;
    }

    public static TransactionBuilder builder() {
        return new TransactionBuilder();
    }

    public static final class TransactionBuilder implements Builder<EconomyTransferTransaction> {

        private Currency currency;
        private Account from;
        private Account to;
        private BigDecimal amount;
        private EconomyResultType result;
        private Supplier<Component> message;

        public TransactionBuilder currency(final Currency currency) {
            this.currency = currency;
            return this;
        }

        public TransactionBuilder from(final Account account) {
            this.from = account;
            return this;
        }

        public TransactionBuilder to(final Account account) {
            this.to = account;
            return this;
        }

        public TransactionBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public TransactionBuilder result(EconomyResultType result) {
            this.result = result;
            return this;
        }

        public TransactionBuilder message(Supplier<Component> message) {
            this.message = message;
            return this;
        }

        @Override
        public EconomyTransferTransaction build() {
            return new ImpactorEconomyTransferTransaction(this);
        }
    }
}
