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

package net.impactdev.impactor.api.economy.accounts;

import net.impactdev.impactor.api.economy.accounts.banks.Bank;
import net.impactdev.impactor.api.economy.currency.Currency;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents a link between a {@link Currency} and a {@link BigDecimal} value.
 */
public interface Account extends AccountLike {

    /**
     * The owner of the account, which need not be a player. Each account is linked to a singular currency,
     * versus a set of currencies. This helps in two ways, direct data management and allowing data to not
     * have to be ignored in memory due to a currency definition disappearing.
     *
     * @return The UUID of the owner for this account
     * @since 6.0.0
     */
    @NotNull UUID owner();

    /**
     * Specifies a bank attached to this account. Banks may not always be available per account, and can be
     * scoped to the type of holder or conditionally provided. Should an account not have enough funds available
     * to complete a transaction, implementations can optionally allow accounts to pull from their banks
     * should the need be necessary.
     *
     * @return A bank account, if available to the account
     * @since 6.0.0
     */
    Optional<Bank> bank();

}
