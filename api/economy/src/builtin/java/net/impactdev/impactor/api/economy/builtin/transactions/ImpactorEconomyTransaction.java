package net.impactdev.impactor.api.economy.builtin.transactions;

import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.economy.transactions.EconomyTransaction;
import net.impactdev.impactor.api.economy.transactions.details.TransactionResult;
import net.impactdev.impactor.api.economy.transactions.details.TransactionType;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

public class ImpactorEconomyTransaction implements EconomyTransaction {

    private final Currency currency;
    private final Account source;
    private final BigDecimal amount;
    private final TransactionType type;
    private final TransactionResult result;
    private final Instant timestamp;

    @Override
    public @NotNull Currency currency() {
        return this.currency;
    }

    @Override
    public @NotNull Account account() {
        return this.source;
    }

    @Override
    public @NotNull BigDecimal amount() {
        return this.amount;
    }

    @Override
    public @NotNull TransactionType type() {
        return this.type;
    }

    @Override
    public @NotNull TransactionResult result() {
        return this.result;
    }

    @Override
    public @NotNull Instant timestamp() {
        return this.timestamp;
    }
}
