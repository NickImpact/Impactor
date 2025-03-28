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

package net.impactdev.impactor.api.economy.accounts.banks;

import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.api.economy.accounts.AccountLike;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Represents a special type of account which enables an entity to store money with extended benefits.
 * <br/><br/>
 * <h3>Banks vs Accounts</h3>
 * Banks are meant to be a secondary location for money stores, which can optionally provide interest
 * overtime for storing money within. Effectively, currency stored within a bank is meant to allow
 * for a backup store. Should an account attempt to make a transaction without enough liquid currency available,
 * an account can optionally poll from the bank's reserves.
 * <p>Following, storing currency within a bank allows it to optionally gain interest over time, growing in value
 * as it remains within. Effectively, banks can provide a means of passive income such that implementation limits
 * apply.</p>
 * <br/>
 * <h3>Credit</h3>
 * Banks additionally can provide a credit line, where currency is outright provided to the end user, with a specific
 * limit. However, this cash is expected to be paid back overtime, and can have interest imposed on the amount in
 * debt. So, while a bank account can receive funds passively, credit can impose fees passively. How credit is
 * provided is up to the implementation.
 *
 * @since 6.0.0
 */
public interface Bank extends AccountLike {

    /**
     * Represents the account this bank is linked to. Meant to allow for a direct callback to the source
     * of the bank should it ever be necessary.
     *
     * @return The source account
     * @since 6.0.0
     */
    Account parent();

    /**
     * Specifies the interest, if any, a bank's balance should receive over time for passively maintaining
     * a balance. Interest is applied on a time interval, and will be based on the balance of the account
     * at the time interest is to be executed.
     *
     * @return An interest manipulator, if enabled
     * @since 6.0.0
     */
    Optional<Interest> interest();

    /**
     * Represents the minimum balance the bank will permit. This is meant to potentially differentiate
     * from the currency's imposed limits.
     *
     * @return The minimum balance of the bank
     * @since 6.0.0
     */
    BigDecimal minimum();

    /**
     * Represents the maximum balance the bank will permit. This is meant to potentially differentiate from the
     * currency's imposed limits.
     *
     * @return The maximum balance of the bank
     * @since 6.0.0
     */
    BigDecimal maximum();

}
