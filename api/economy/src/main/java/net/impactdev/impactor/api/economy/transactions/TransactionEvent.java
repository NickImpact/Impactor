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

package net.impactdev.impactor.api.economy.transactions;

import net.impactdev.impactor.api.core.events.Cancellable;
import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.economy.transactions.details.TransactionType;

import java.math.BigDecimal;

public interface TransactionEvent {

    final class Pre extends Cancellable {
        private final Currency currency;
        private final Account account;
        private final BigDecimal amount;
        private final TransactionType type;

        public Pre(final Currency currency, final Account account, final BigDecimal amount, final TransactionType type) {
            this.currency = currency;
            this.account = account;
            this.amount = amount;
            this.type = type;
        }

        public Currency currency() {
            return this.currency;
        }

        public Account account() {
            return this.account;
        }

        public BigDecimal amount() {
            return this.amount;
        }

        public TransactionType type() {
            return this.type;
        }
    }

    record Post(EconomyTransaction transaction) {}



}
