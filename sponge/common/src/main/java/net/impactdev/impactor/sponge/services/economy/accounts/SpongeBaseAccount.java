package net.impactdev.impactor.sponge.services.economy.accounts;

import com.google.common.collect.Maps;
import net.impactdev.impactor.api.services.economy.accounts.AccountAccessor;
import net.impactdev.impactor.economy.accounts.ImpactorAccount;
import net.impactdev.impactor.economy.currency.ImpactorCurrency;
import net.impactdev.impactor.sponge.services.economy.currency.SpongeCurrencyDelegator;
import net.impactdev.impactor.sponge.services.economy.transactions.SpongeTransactionResult;
import net.impactdev.impactor.sponge.services.economy.transactions.SpongeTransferResult;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransferResult;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("DuplicatedCode")
public class SpongeBaseAccount implements Account {

    private final AccountAccessor holder;

    public SpongeBaseAccount(AccountAccessor holder) {
        this.holder = holder;
    }

    @Override
    public String identifier() {
        return "Impactor Mirror Account";
    }

    @Override
    public Component displayName() {
        return Component.text("Impactor Mirror Account");
    }

    @Override
    public BigDecimal defaultBalance(Currency currency) {
        return SpongeCurrencyDelegator.impactor(currency).starting();
    }

    @Override
    public boolean hasBalance(Currency currency, Set<Context> contexts) {
        return true;
    }

    @Override
    public boolean hasBalance(Currency currency, Cause cause) {
        return true;
    }

    @Override
    public BigDecimal balance(Currency currency, Set<Context> contexts) {
        return this.holder.account(SpongeCurrencyDelegator.impactor(currency)).join().balance();
    }

    @Override
    public BigDecimal balance(Currency currency, Cause cause) {
        return this.holder.account(SpongeCurrencyDelegator.impactor(currency)).join().balance();
    }

    @Override
    public Map<Currency, BigDecimal> balances(Set<Context> contexts) {
        return this.holder.accounts().thenApply(result -> {
            Map<Currency, BigDecimal> balances = Maps.newHashMap();
            result.forEach((key, value) -> balances.put(
                    SpongeCurrencyDelegator.sponge((ImpactorCurrency) key),
                    value.balance()
            ));

            return balances;
        }).join();
    }

    @Override
    public Map<Currency, BigDecimal> balances(Cause cause) {
        return this.holder.accounts().thenApply(result -> {
            Map<Currency, BigDecimal> balances = Maps.newHashMap();
            result.forEach((key, value) -> balances.put(
                    SpongeCurrencyDelegator.sponge((ImpactorCurrency) key),
                    value.balance()
            ));

            return balances;
        }).join();
    }

    @Override
    public TransactionResult setBalance(Currency currency, BigDecimal amount, Set<Context> contexts) {
        ImpactorAccount account = this.holder.account(SpongeCurrencyDelegator.impactor(currency))
                .thenApply(a -> (ImpactorAccount) a)
                .join();

        return new SpongeTransactionResult(this, currency, account.set(amount));
    }

    @Override
    public TransactionResult setBalance(Currency currency, BigDecimal amount, Cause cause) {
        ImpactorAccount account = this.holder.account(SpongeCurrencyDelegator.impactor(currency))
                .thenApply(a -> (ImpactorAccount) a)
                .join();

        return new SpongeTransactionResult(this, currency, account.set(amount));
    }

    @Override
    public Map<Currency, TransactionResult> resetBalances(Set<Context> contexts) {
        return this.holder.accounts()
                .thenApply(accounts -> {
                    Map<Currency, TransactionResult> results = Maps.newHashMap();
                    accounts.forEach(((currency, account) -> {
                        Currency translated = SpongeCurrencyDelegator.sponge((ImpactorCurrency) currency);
                        results.put(translated, new SpongeTransactionResult(this, translated, account.reset()));
                    }));

                    return results;
                })
                .join();
    }

    @Override
    public Map<Currency, TransactionResult> resetBalances(Cause cause) {
        return this.holder.accounts()
                .thenApply(accounts -> {
                    Map<Currency, TransactionResult> results = Maps.newHashMap();
                    accounts.forEach(((currency, account) -> {
                        Currency translated = SpongeCurrencyDelegator.sponge((ImpactorCurrency) currency);
                        results.put(translated, new SpongeTransactionResult(this, translated, account.reset()));
                    }));

                    return results;
                })
                .join();
    }

    @Override
    public TransactionResult resetBalance(Currency currency, Set<Context> contexts) {
        ImpactorAccount account = this.holder.account(SpongeCurrencyDelegator.impactor(currency))
                .thenApply(a -> (ImpactorAccount) a)
                .join();

        return new SpongeTransactionResult(this, currency, account.reset());
    }

    @Override
    public TransactionResult resetBalance(Currency currency, Cause cause) {
        ImpactorAccount account = this.holder.account(SpongeCurrencyDelegator.impactor(currency))
                .thenApply(a -> (ImpactorAccount) a)
                .join();

        return new SpongeTransactionResult(this, currency, account.reset());
    }

    @Override
    public TransactionResult deposit(Currency currency, BigDecimal amount, Set<Context> contexts) {
        ImpactorAccount account = this.holder.account(SpongeCurrencyDelegator.impactor(currency))
                .thenApply(a -> (ImpactorAccount) a)
                .join();

        return new SpongeTransactionResult(this, currency, account.deposit(amount));
    }

    @Override
    public TransactionResult deposit(Currency currency, BigDecimal amount, Cause cause) {
        ImpactorAccount account = this.holder.account(SpongeCurrencyDelegator.impactor(currency))
                .thenApply(a -> (ImpactorAccount) a)
                .join();

        return new SpongeTransactionResult(this, currency, account.deposit(amount));
    }

    @Override
    public TransactionResult withdraw(Currency currency, BigDecimal amount, Set<Context> contexts) {
        ImpactorAccount account = this.holder.account(SpongeCurrencyDelegator.impactor(currency))
                .thenApply(a -> (ImpactorAccount) a)
                .join();

        return new SpongeTransactionResult(this, currency, account.withdraw(amount));
    }

    @Override
    public TransactionResult withdraw(Currency currency, BigDecimal amount, Cause cause) {
        ImpactorAccount account = this.holder.account(SpongeCurrencyDelegator.impactor(currency))
                .thenApply(a -> (ImpactorAccount) a)
                .join();

        return new SpongeTransactionResult(this, currency, account.withdraw(amount));
    }

    @Override
    public TransferResult transfer(Account to, Currency currency, BigDecimal amount, Set<Context> contexts) {
        ImpactorAccount account = this.holder.account(SpongeCurrencyDelegator.impactor(currency))
                .thenApply(a -> (ImpactorAccount) a)
                .join();

        TransactionResult withdraw = new SpongeTransactionResult(this, currency, account.withdraw(amount));
        if(withdraw.result().equals(ResultType.SUCCESS)) {
            TransactionResult deposit = to.deposit(currency, amount);
            if(!deposit.result().equals(ResultType.SUCCESS)) {
                account.deposit(amount);
            }
            return new SpongeTransferResult(to, this, currency, amount, deposit.result());
        } else {
            return new SpongeTransferResult(to, this, currency, amount, withdraw.result());
        }
    }

    @Override
    public TransferResult transfer(Account to, Currency currency, BigDecimal amount, Cause cause) {
        ImpactorAccount account = this.holder.account(SpongeCurrencyDelegator.impactor(currency))
                .thenApply(a -> (ImpactorAccount) a)
                .join();

        TransactionResult withdraw = new SpongeTransactionResult(this, currency, account.withdraw(amount));
        if(withdraw.result().equals(ResultType.SUCCESS)) {
            TransactionResult deposit = to.deposit(currency, amount);
            if(!deposit.result().equals(ResultType.SUCCESS)) {
                account.deposit(amount);
            }
            return new SpongeTransferResult(to, this, currency, amount, deposit.result());
        } else {
            return new SpongeTransferResult(to, this, currency, amount, withdraw.result());
        }
    }
}
