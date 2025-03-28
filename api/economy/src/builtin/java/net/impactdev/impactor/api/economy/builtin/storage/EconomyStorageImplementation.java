package net.impactdev.impactor.api.economy.builtin.storage;

import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.economy.transactions.EconomyTransaction;
import net.impactdev.impactor.api.storage.connections.StorageConnection;

import java.util.Set;
import java.util.UUID;

public interface EconomyStorageImplementation extends StorageConnection {

    /**
     * Checks if an account exists under the given owner UUID and currency definition.
     *
     * @param uuid     The ID of the account holder
     * @param currency The currency the account should manage
     * @return <code>true</code> if the account exists, <code>false</code> otherwise
     * @throws Exception If any unexpected transaction error occurs during the query
     * @since 6.0.0
     */
    boolean exists(UUID uuid, Currency currency) throws Exception;

    /**
     * Gets or creates an account using the given uuid and currency definition. The UUID
     * provided should be the ID of the target account holder.
     *
     * @param uuid     The unique ID of the account holder
     * @param currency The currency the account should manage
     * @return The existing account, or a freshly created account
     * @throws Exception If any unexpected transaction error occurs during the query/write process
     * @since 6.0.0
     */
    Account account(UUID uuid, Currency currency) throws Exception;

    /**
     * Saves an account to the data store.
     *
     * @param account The account to be saved
     * @throws Exception If any unexpected transaction error occurs during the write process
     * @since 6.0.0
     */
    void save(Account account) throws Exception;

    /**
     * Attempts to delete an account defined by the UUID and currency definitions. If an
     * account does not exist for the definition, this call will act as a no-op.
     *
     * @param uuid     The unique ID of the account holder
     * @param currency The currency the account should manage
     * @throws Exception If any unexpected transaction error occurs during the query/write process
     * @since 6.0.0
     */
    void delete(UUID uuid, Currency currency) throws Exception;

    /**
     * @param currency
     * @param limit
     * @return
     * @throws Exception
     * @since 6.0.0
     */
    Set<Account> performing(Currency currency, int limit) throws Exception;

    /**
     * @param transaction
     * @throws Exception
     * @since 6.0.0
     */
    void log(EconomyTransaction transaction) throws Exception;
}
