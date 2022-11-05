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

package net.impactdev.impactor.economy.transactions;

import net.impactdev.impactor.api.builders.Builder;
import net.impactdev.impactor.api.services.economy.accounts.Account;
import net.impactdev.impactor.api.services.economy.currency.Currency;
import net.impactdev.impactor.api.services.economy.transactions.EconomyResultType;
import net.impactdev.impactor.api.services.economy.transactions.EconomyTransaction;
import net.impactdev.impactor.api.services.economy.transactions.EconomyTransactionType;

import java.math.BigDecimal;

public class ImpactorEconomyTransaction implements EconomyTransaction {

    private final Account account;
    private final Currency currency;
    private final BigDecimal amount;
    private final EconomyTransactionType type;
    private final EconomyResultType result;

    public ImpactorEconomyTransaction(Account account, Currency currency, BigDecimal amount, EconomyTransactionType type, EconomyResultType result) {
        this.account = account;
        this.currency = currency;
        this.amount = amount;
        this.type = type;
        this.result = result;
    }

    @Override
    public Account account() {
        return this.account;
    }

    @Override
    public Currency currency() {
        return this.currency;
    }

    @Override
    public BigDecimal amount() {
        return this.amount;
    }

    @Override
    public EconomyTransactionType type() {
        return this.type;
    }

    @Override
    public EconomyResultType result() {
        return this.result;
    }

    public static TransactionBuilder builder() {
        return new TransactionBuilder();
    }

    public static class TransactionBuilder implements Builder<ImpactorEconomyTransaction> {

        private Account account;
        private Currency currency;
        private BigDecimal amount;
        private EconomyTransactionType type;
        private EconomyResultType result;

        public TransactionBuilder account(Account account) {
            this.account = account;
            return this;
        }

        public TransactionBuilder currency(Currency currency) {
            this.currency = currency;
            return this;
        }

        public TransactionBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public TransactionBuilder type(EconomyTransactionType type) {
            this.type = type;
            return this;
        }

        public TransactionBuilder result(EconomyResultType result) {
            this.result = result;
            return this;
        }

        @Override
        public ImpactorEconomyTransaction build() {
            return new ImpactorEconomyTransaction(this.account, this.currency, this.amount, this.type, this.result);
        }
    }
}
