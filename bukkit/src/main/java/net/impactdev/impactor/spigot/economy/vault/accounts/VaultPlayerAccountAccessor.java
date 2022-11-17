package net.impactdev.impactor.spigot.economy.vault.accounts;

import com.google.common.collect.Maps;
import net.impactdev.impactor.api.services.economy.accounts.Account;
import net.impactdev.impactor.api.services.economy.accounts.AccountAccessor;
import net.impactdev.impactor.api.services.economy.currency.Currency;
import net.impactdev.impactor.economy.accounts.ImpactorAccount;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;

public class VaultPlayerAccountAccessor implements AccountAccessor {

    private final Economy provider;
    private final Currency currency;
    private final String name;

    private final ReentrantLock lock = new ReentrantLock();
    private Account account;

    public VaultPlayerAccountAccessor(Economy provider, Currency currency, String name) {
        this.provider = provider;
        this.currency = currency;
        this.name = name;
    }

    @Override
    public CompletableFuture<Account> account(Currency currency) {
        return CompletableFuture.supplyAsync(() -> {
            this.lock.lock();
            if(this.account != null) {
                return this.account;
            }

            try {
                double balance = this.provider.getBalance(Bukkit.getOfflinePlayer(this.name));
                Account account = ImpactorAccount.create(currency, null);
                account.set(BigDecimal.valueOf(balance));

                return this.account = account;
            } finally {
                this.lock.unlock();
            }
        });
    }

    @Override
    public CompletableFuture<Map<Currency, Account>> accounts() {
        return CompletableFuture.supplyAsync(() -> {
            Map<Currency, Account> results = Maps.newHashMap();
            results.put(this.currency, this.account(currency).join());

            return results;
        });
    }
}
