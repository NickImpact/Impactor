package net.impactdev.impactor.api.economy.builtin.accounts;

import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.api.economy.builtin.transactions.TransactionRequest;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.economy.transactions.EconomyTransaction;
import net.impactdev.impactor.api.economy.transactions.details.TransactionType;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class ImpactorAccount implements Account {

    private final UUID owner;
    private final Currency currency;

    private BigDecimal balance;

    public ImpactorAccount(final UUID owner, final Currency currency) {
        this.owner = owner;
        this.currency = currency;
    }

    public static ImpactorAccount load(final UUID owner, final Currency currency, final BigDecimal balance) {
        ImpactorAccount account = new ImpactorAccount(owner, currency);
        account.balance = balance;

        return account;
    }

    public static ImpactorAccount create(final UUID owner, final Currency currency) {
        ImpactorAccount account = new ImpactorAccount(owner, currency);
        account.balance = currency.defaultAccountBalance();

        return account;
    }

    @Override
    public @NotNull UUID owner() {
        return this.owner;
    }

    @Override
    public @NotNull Currency currency() {
        return this.currency;
    }

    @Override
    public @NotNull BigDecimal balance() {
        return this.balance;
    }

    @Override
    public @NotNull CompletableFuture<EconomyTransaction> set(@NotNull BigDecimal amount) {
        final TransactionRequest request = new TransactionRequest(
                this.currency,
                this,
                amount,
                TransactionType.SET,
                Instant.now()
        );

        return null;
    }

    @Override
    public @NotNull CompletableFuture<EconomyTransaction> deposit(@NotNull BigDecimal amount) {
        return null;
    }

    @Override
    public @NotNull CompletableFuture<EconomyTransaction> withdraw(@NotNull BigDecimal amount) {
        return null;
    }

    @Override
    public @NotNull CompletableFuture<EconomyTransaction.Transfer> transfer(@NotNull BigDecimal amount, @NotNull Account to) {
        return null;
    }

    @Override
    public @NotNull CompletableFuture<EconomyTransaction> reset() {
        return null;
    }
}
