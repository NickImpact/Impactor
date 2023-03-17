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

package net.impactdev.impactor.core.economy.events;

import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.economy.events.EconomyTransferTransactionEvent;
import net.impactdev.impactor.api.economy.transactions.EconomyTransferTransaction;

import java.math.BigDecimal;

public class ImpactorEconomyTransferTransactionEvent implements EconomyTransferTransactionEvent {

    private final Currency currency;
    private final Account from;
    private final Account to;

    public ImpactorEconomyTransferTransactionEvent(Currency currency, Account from, Account to) {
        this.currency = currency;
        this.from = from;
        this.to = to;
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

    public static final class Pre extends ImpactorEconomyTransferTransactionEvent implements EconomyTransferTransactionEvent.Pre {

        private final BigDecimal amount;
        private boolean cancelled;

        public Pre(Currency currency, Account from, Account to, BigDecimal amount) {
            super(currency, from, to);
            this.amount = amount;
        }

        @Override
        public BigDecimal amount() {
            return this.amount;
        }

        @Override
        public boolean cancelled() {
            return this.cancelled;
        }

        @Override
        public void cancelled(boolean cancelled) {
            this.cancelled = true;
        }
    }

    public static final class Post extends ImpactorEconomyTransferTransactionEvent implements EconomyTransferTransactionEvent.Post {

        private final EconomyTransferTransaction transaction;

        public Post(EconomyTransferTransaction transaction) {
            super(transaction.currency(), transaction.from(), transaction.to());
            this.transaction = transaction;
        }

        @Override
        public EconomyTransferTransaction transaction() {
            return this.transaction;
        }
    }
}
