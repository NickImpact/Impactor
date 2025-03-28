package net.impactdev.impactor.api.economy.accounts;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.economy.transactions.EconomyTransaction;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

/**
 * Specifies something that performs like an individual account, which can receive queries or requests
 * to evaluate an economic value or manipulate it by some means.
 *
 * @since 6.0.0
 */
public interface AccountLike {

    /**
     * The currency this account represents. Each account is linked to a particular currency,
     * versus an account holding onto a set of currencies.
     *
     * @return The currency this account reflects
     * @since 6.0.0
     */
    @NotNull Currency currency();

    /**
     * Represents the current balance of the account.
     *
     * @return The balance of the account
     * @since 6.0.0
     */
    @NotNull BigDecimal balance();

    /**
     * Attempts to set the balance of this account to the given amount.
     *
     * @param amount The amount of money to set the account to
     * @return A transaction built with details on the request
     * @since 6.0.0
     */
    @NotNull
    @CanIgnoreReturnValue
    CompletableFuture<EconomyTransaction> set(final @NotNull BigDecimal amount);

    /**
     * Attempts to deposit the given amount into the account.
     *
     * @param amount The amount of money to deposit
     * @return A transaction built with details on the request
     * @since 6.0.0
     */
    @NotNull
    @CanIgnoreReturnValue
    CompletableFuture<EconomyTransaction> deposit(final @NotNull BigDecimal amount);

    /**
     * Attempts to withdraw the given amount from the account.
     *
     * @param amount The amount to withdraw
     * @return A transaction built with details on the request
     * @since 6.0.0
     */
    @NotNull
    @CanIgnoreReturnValue
    CompletableFuture<EconomyTransaction> withdraw(final @NotNull BigDecimal amount);

    /**
     * Attempts to transfer funds from the account to the target account.
     *
     * @param amount The amount to transfer from the account to the target
     * @param to     The Account receiving the funds
     * @return A transaction built with details on the request
     * @since 6.0.0
     */
    @NotNull
    @CanIgnoreReturnValue
    CompletableFuture<EconomyTransaction.Transfer> transfer(final @NotNull BigDecimal amount, final @NotNull Account to);

    /**
     * Attempts to reset the balance of the account to the default balance set by the {@link Currency}.
     *
     * @return A transaction built with details on the request
     * @since 6.0.0
     */
    @NotNull
    @CanIgnoreReturnValue
    CompletableFuture<EconomyTransaction> reset();

}
