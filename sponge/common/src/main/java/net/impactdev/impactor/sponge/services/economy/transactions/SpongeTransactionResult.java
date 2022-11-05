package net.impactdev.impactor.sponge.services.economy.transactions;

import com.google.common.collect.Sets;
import net.impactdev.impactor.api.services.economy.transactions.EconomyTransaction;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransactionType;
import org.spongepowered.api.service.economy.transaction.TransactionTypes;

import java.math.BigDecimal;
import java.util.Set;

public class SpongeTransactionResult implements TransactionResult {

    public static final Set<Context> EMPTY_CONTEXT = Sets.newHashSet();

    private final Account account;
    private final Currency currency;
    private final EconomyTransaction delegate;

    public SpongeTransactionResult(Account account, Currency currency, EconomyTransaction delegate) {
        this.account = account;
        this.currency = currency;
        this.delegate = delegate;
    }

    @Override
    public Account account() {
        return this.account;
    }

    @Override
    public Currency currency() {
        return this.currency;
    }

    @Override
    public BigDecimal amount() {
        return this.delegate.amount();
    }

    @Override
    public Set<Context> contexts() {
        return EMPTY_CONTEXT;
    }

    @Override
    public ResultType result() {
        switch (this.delegate.result()) {
            case SUCCESS:
                return ResultType.SUCCESS;
            case FAILED:
                return ResultType.FAILED;
            case NO_REMAINING_SPACE:
                return ResultType.ACCOUNT_NO_SPACE;
            case NOT_ENOUGH_FUNDS:
                return ResultType.ACCOUNT_NO_FUNDS;
            default:
                throw new IllegalStateException("Unknown result type mapping: " + this.delegate.result());
        }
    }

    @Override
    public TransactionType type() {
        switch (this.delegate.type()) {
            case TRANSFER:
                return TransactionTypes.TRANSFER.get();
            case WITHDRAW:
                return TransactionTypes.WITHDRAW.get();
            case DEPOSIT:
                return TransactionTypes.DEPOSIT.get();
            default:
                throw new IllegalStateException("Unknown transaction type mapping: " + this.delegate.type());
        }
    }
}
