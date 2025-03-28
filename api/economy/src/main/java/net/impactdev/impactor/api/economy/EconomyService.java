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

package net.impactdev.impactor.api.economy;

import com.google.common.collect.ImmutableList;
import net.impactdev.impactor.api.core.ServiceProvider;
import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.api.economy.accounts.AccountManager;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.economy.currency.CurrencyProvider;
import net.impactdev.impactor.loader.Version;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Represents the service responsible for processing monetary transactions across multiple types of currencies.
 *
 * @since 5.0.0
 */
public interface EconomyService {

    static EconomyService service() {
        return ServiceProvider.instance().provide(EconomyService.class);
    }

    /**
     * Provides details on the service. This allows a user to get immediate feedback as to which
     * service is actually acting on Impactor's economy API, while additionally detailing what
     * other possible services are available to them.
     *
     * @return Details regarding this service
     * @since 6.0.0
     */
    ServiceDetails details();

    /**
     * Acts as the general currency provider for the EconomyService. By nature, a currency
     * provider may support any number of currencies, ideally with at least one.
     *
     * <p>How currencies are registered is completely up to the implementation
     *
     * @return The provider for all registered currencies
     * @since 5.0.0
     */
    CurrencyProvider currencies();

    /**
     * Provides a manager responsible for processing accounts on the service. This acts as the forefront
     * to account access to perform any actions against a target. How implementations provide and update
     * accounts is entirely up to the implementation.
     *
     * @return A global account manager
     * @since 6.0.0
     */
    AccountManager accounts();

    /**
     * Checks if the given target owns an account for the given Currency.
     *
     * @param currency The currency the account is based on
     * @param target   The UUID of the target entity
     * @return A future with a computed result indicating if the target account exists
     * @since 5.0.0
     * @deprecated Replaced with {@link AccountManager#exists(UUID, Currency)}
     */
    @Deprecated(since = "6.0.0")
    default CompletableFuture<Boolean> hasAccount(Currency currency, UUID target) {
        return this.accounts().exists(target, currency);
    }

    /**
     * Fetches or creates an account based on the given currency for the target user.
     *
     * @param currency
     * @param target
     * @return
     * @since 5.0.0
     * @deprecated Replaced with {@link AccountManager#getOrCreate(UUID, Currency)}
     */
    @Deprecated(since = "6.0.0")
    default CompletableFuture<Account> account(Currency currency, UUID target) {
        return this.accounts().getOrCreate(target, currency);
    }

    /**
     * @param currency
     * @param target
     * @return
     * @since 5.0.0
     * @deprecated Replaced with {@link AccountManager#delete(UUID, Currency)}
     */
    @Deprecated(since = "6.0.0")
    default CompletableFuture<Boolean> deleteAccount(Currency currency, UUID target) {
        return this.accounts().delete(target, currency).thenApply(ignore -> true).exceptionally(ignore -> false);
    }

    /**
     * Gets all accounts using the given currency.
     *
     * <p>This method is useful for commands like baltop, </p>
     *
     * @param currency
     * @return
     * @since 5.0.0
     * @deprecated Replaced with {@link AccountManager#performing(Currency, int)}
     */
    @Deprecated(since = "6.0.0")
    default CompletableFuture<List<Account>> getAllAccounts(Currency currency) {
        return this.accounts().performing(currency, 100).thenApply(ImmutableList::copyOf);
    }

    /**
     * Details information on an economy service implementation. This carries an identifier used to differentiate
     * your service from others, alongside a version tag to detail the version of the implementation.
     *
     * @since 6.0.0
     */
    interface ServiceDetails {

        /**
         * Specifies the identifier bound to a given economy service implementation. This should be unique
         * to best identify your service from possible others.
         *
         * @return The identifier of the service
         * @since 6.0.0
         */
        String identifier();

        /**
         * Provides a version regarding the implementation.
         *
         * @return The version of this service
         * @since 6.0.0
         */
        Version version();

    }

}
