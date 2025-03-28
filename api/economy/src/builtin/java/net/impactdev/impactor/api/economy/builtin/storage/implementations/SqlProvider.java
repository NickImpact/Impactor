package net.impactdev.impactor.api.economy.builtin.storage.implementations;

import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.api.economy.builtin.storage.EconomyStorageImplementation;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.economy.transactions.EconomyTransaction;
import net.impactdev.impactor.api.storage.StorageMetadata;

import java.util.Set;
import java.util.UUID;

public class SqlProvider implements EconomyStorageImplementation {

    @Override
    public String name() {
        return "";
    }

    @Override
    public void init() throws Exception {

    }

    @Override
    public void shutdown() throws Exception {

    }

    @Override
    public StorageMetadata metadata() throws Exception {
        return null;
    }

    @Override
    public boolean exists(UUID uuid, Currency currency) throws Exception {
        return false;
    }

    @Override
    public Account account(UUID uuid, Currency currency) throws Exception {
        return null;
    }

    @Override
    public void save(Account account) throws Exception {

    }

    @Override
    public void delete(UUID uuid, Currency currency) throws Exception {

    }

    @Override
    public Set<Account> performing(Currency currency, int limit) throws Exception {
        return Set.of();
    }

    @Override
    public void log(EconomyTransaction transaction) throws Exception {

    }
}
