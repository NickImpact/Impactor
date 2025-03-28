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

import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.economy.transactions.details.TransactionResult;
import net.impactdev.impactor.api.economy.transactions.details.TransactionSource;
import net.impactdev.impactor.api.economy.transactions.details.TransactionType;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public interface EconomyTransaction {

    /**
     * Specifies the currency used in the transaction.
     *
     * @return The currency used in the transaction
     * @since 6.0.0
     */
    @NotNull
    Currency currency();

    /**
     * Specifies the account the transaction was run against.
     *
     * @return The account the transaction was run against
     * @since 6.0.0
     */
    @NotNull
    Account account();

    /**
     * Specifies the amount of money this transaction worked against. In the case of
     * a reset, this value should typically be equivalent to {@link BigDecimal#ZERO}.
     *
     * @return The amount of money processed by the transaction
     * @since 6.0.0
     */
    @NotNull
    BigDecimal amount();

    /**
     * Details the sources of currency across the account that satisfied the transaction. If the
     * transaction is to fail, this should be an empty list as to best describe the result.
     *
     * @return The list of transaction sources
     * @since 6.0.0
     */
    List<TransactionSource> sources();

    /**
     * Specifies the type of operation this transaction represents
     *
     * @return The type of transaction
     */
    @NotNull
    TransactionType type();

    /**
     * Specifies the end result of this transaction. This should effectively detail
     * if the transaction was successful, or why it failed.
     *
     * @return The result of the transaction
     */
    @NotNull
    TransactionResult result();

    /**
     * Specifies the time a transaction occurred. This is primarily useful for logic
     * which may wish to keep track of transactions performed on an account.
     *
     * @return The instant this transaction occurred
     */
    @NotNull
    Instant timestamp();

    /**
     * Specifies if the result of this transaction was successful. Any other transaction
     * result will ensure this method returns false.
     *
     * @return <code>true</code> if successful, <code>false</code> otherwise
     */
    default boolean successful() {
        return this.result() == TransactionResult.SUCCESS;
    }

    /**
     * Attempts to cast this transaction down to its Transfer typing. Should the
     * type of transaction not be caused by a transfer, this will fail exceptionally.
     *
     * @return The transaction cast to a Transfer transaction
     */
    default Transfer asTransfer() {
        if(this.type() == TransactionType.TRANSFER) {
            return (Transfer) this;
        }

        throw new IllegalStateException("Transaction not a transfer");
    }

    interface Transfer extends EconomyTransaction {

        /**
         * Specifies the recipient of funds performed by a transaction.
         *
         * @return The recipient
         */
        Account recipient();

    }
}
