package net.impactdev.impactor.api.economy.accounts;

import net.impactdev.impactor.api.economy.currency.Currency;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Represents the method for serving {@link Account accounts} to requesting services. The idea behind
 * the manager is that it can provide this data in a way that not only served from a data store, but
 * with a maintained cache capable of being updated on a transaction, no matter the source.
 *
 * @since 6.0.0
 */
public interface AccountManager {

    /**
     * Validates if account data exists for the given Currency for the target identifier.
     *
     * @param uuid     The target ID of the account
     * @param currency The currency to query against
     * @return If account data exists with these identifiers
     * @since 6.0.0
     */
    CompletableFuture<Boolean> exists(UUID uuid, Currency currency);

    /**
     * Gets or creates an account for the given {@link UUID} and {@link Currency}.
     * Due to the nature of account fetching, this operation is expected to carry
     * some element of asynchronous loading on first request for an account. How
     * implementations handle accounts post first-load request is up to them.
     *
     * @param uuid     The UUID of the account holder
     * @param currency The currency typing of the account
     * @return A future wrapping the request for the target account
     */
    CompletableFuture<Account> getOrCreate(UUID uuid, Currency currency);

    /**
     * Marks the account matching the criteria given as deleted. Implementations
     * can choose how this functionality works. For instance, this could act as
     * an outright deletion, completely getting rid of the account. Or in another
     * case, the account could simply be marked as deleted, but not actually
     * removed from storage, should the need arise to restore said account.
     *
     * @param uuid     The UUID of the account holder
     * @param currency The currency typing of the account
     * @return A future indicating the status of the deletion
     */
    CompletableFuture<Void> delete(UUID uuid, Currency currency);

    /**
     * Collects a set of accounts which are currently performing the best in the specified currency,
     * with a max of the given limit. While limits can arbitrarily be set to values higher than accounts
     * available, implementations might also have an imposed limit that implies a different maximum response.
     *
     * @param currency The currency to filter against
     * @param limit    The user specified limit of accounts to fetch
     * @return A future representing the matching accounts
     */
    CompletableFuture<Set<Account>> performing(Currency currency, int limit);

}
