package net.impactdev.impactor.sponge.services.economy.transactions;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionType;
import org.spongepowered.api.service.economy.transaction.TransactionTypes;
import org.spongepowered.api.service.economy.transaction.TransferResult;

import java.math.BigDecimal;
import java.util.Set;

public class SpongeTransferResult implements TransferResult {

    private final Account to;
    private final Account from;

    private final Currency currency;
    private final BigDecimal amount;
    private final ResultType result;

    public SpongeTransferResult(Account to, Account from, Currency currency, BigDecimal amount, ResultType result) {
        this.to = to;
        this.from = from;
        this.currency = currency;
        this.amount = amount;
        this.result = result;
    }

    @Override
    public Account accountTo() {
        return this.to;
    }

    @Override
    public Account account() {
        return this.from;
    }

    @Override
    public Currency currency() {
        return this.currency;
    }

    @Override
    public BigDecimal amount() {
        return this.amount;
    }

    @Override
    public Set<Context> contexts() {
        return SpongeTransactionResult.EMPTY_CONTEXT;
    }

    @Override
    public ResultType result() {
        return this.result;
    }

    @Override
    public TransactionType type() {
        return TransactionTypes.TRANSFER.get();
    }
}
