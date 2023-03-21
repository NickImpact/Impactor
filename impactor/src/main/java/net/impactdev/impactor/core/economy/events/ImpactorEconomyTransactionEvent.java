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
import net.impactdev.impactor.api.economy.events.EconomyTransactionEvent;
import net.impactdev.impactor.api.economy.transactions.EconomyTransaction;
import net.impactdev.impactor.api.economy.transactions.details.EconomyTransactionType;

import java.math.BigDecimal;

public abstract class ImpactorEconomyTransactionEvent implements EconomyTransactionEvent {

    private final Currency currency;
    private final Account account;

    public ImpactorEconomyTransactionEvent(Currency currency, Account account) {
        this.currency = currency;
        this.account = account;
    }

    @Override
    public Currency currency() {
        return this.currency;
    }

    @Override
    public Account account() {
        return this.account;
    }

    public static final class Pre extends ImpactorEconomyTransactionEvent implements EconomyTransactionEvent.Pre {

        private final BigDecimal amount;
        private final EconomyTransactionType type;

        private boolean cancelled = false;

        public Pre(Currency currency, Account account, BigDecimal amount, EconomyTransactionType type) {
            super(currency, account);
            this.amount = amount;
            this.type = type;
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
        public boolean cancelled() {
            return this.cancelled;
        }

        @Override
        public void cancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }
    }

    public static final class Post extends ImpactorEconomyTransactionEvent implements EconomyTransactionEvent.Post {

        private final EconomyTransaction transaction;

        public Post(EconomyTransaction transaction) {
            super(transaction.currency(), transaction.account());
            this.transaction = transaction;
        }

        @Override
        public EconomyTransaction transaction() {
            return this.transaction;
        }
    }
}
