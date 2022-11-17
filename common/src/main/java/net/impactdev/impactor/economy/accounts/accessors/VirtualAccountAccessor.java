package net.impactdev.impactor.economy.accounts.accessors;

import net.impactdev.impactor.api.services.economy.accounts.Account;
import net.impactdev.impactor.api.services.economy.currency.Currency;

import java.util.concurrent.CompletableFuture;

public class VirtualAccountAccessor extends BaseAccountAccessor {

    private final String identifier;

    public VirtualAccountAccessor(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public CompletableFuture<Account> account(Currency currency) {
        return this.storage().account(this.identifier, currency);
    }

}
