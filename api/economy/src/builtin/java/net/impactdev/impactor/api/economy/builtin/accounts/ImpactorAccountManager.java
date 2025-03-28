package net.impactdev.impactor.api.economy.builtin.accounts;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.api.economy.accounts.AccountManager;
import net.impactdev.impactor.api.economy.builtin.networking.EconomyNetworkingService;
import net.impactdev.impactor.api.economy.builtin.networking.messages.TransactionResponseMessage;
import net.impactdev.impactor.api.economy.builtin.storage.EconomyStorage;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.economy.transactions.EconomyTransaction;
import net.impactdev.impactor.api.events.EventSubscription;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public final class ImpactorAccountManager implements AccountManager {

    private final Logger logger = LogManager.getLogger("Impactor Account Manager");
    private final EconomyStorage storage;
    private final EconomyNetworkingService networking;

    private final AsyncLoadingCache<AccountKey, Account> accounts;

    public ImpactorAccountManager(final EconomyStorage storage, final EconomyNetworkingService networking) {
        this.storage = storage;
        this.networking = networking;
        this.accounts = Caffeine.newBuilder()
                .expireAfterAccess(5, TimeUnit.MINUTES)
                .buildAsync((key, executor) -> this.storage.getOrCreate(key.uuid, key.currency));

        EventSubscription<TransactionResponseMessage> subscription = this.networking.subscribe(message -> {
            EconomyTransaction transaction = message.transaction();
            if (transaction.successful()) {
                
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> exists(UUID uuid, Currency currency) {
        return this.storage.exists(uuid, currency).orTimeout(5, TimeUnit.SECONDS);
    }

    @Override
    public CompletableFuture<Account> getOrCreate(UUID uuid, Currency currency) {
        return this.accounts.get(new AccountKey(currency, uuid)).orTimeout(5, TimeUnit.SECONDS);
    }

    @Override
    public CompletableFuture<Void> delete(UUID uuid, Currency currency) {
        return this.storage.delete(uuid, currency).thenAccept(ignore -> {
            final AccountKey key = new AccountKey(currency, uuid);
            this.accounts.synchronous().invalidate(key);
        }).orTimeout(5, TimeUnit.SECONDS);
    }

    @Override
    public CompletableFuture<Set<Account>> performing(Currency currency, int limit) {
        return this.storage.performing(currency, limit);
    }

    private record AccountKey(Currency currency, UUID uuid) { }
}
