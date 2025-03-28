package net.impactdev.impactor.api.economy.accounts.banks;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

/**
 * Interest represents a means of manipulating some form of cash value based on a time metric. Over time,
 * a balance might be adjusted to reflect a gain or loss of cash, based on the type of line being applied.
 *
 * @since 6.0.0
 */
public interface Interest {

    /**
     * Applies the interest to the given balance. How interest is applied is up to the implementation, such as
     * multiplicative or additive to the overall balance.
     *
     * @param balance The value to represent the current balance
     * @return The updated balance post interest application
     * @since 6.0.0
     */
    @NotNull
    BigDecimal apply(@NotNull BigDecimal balance);

    /**
     * Specifies the amount of time required before interest can be applied. This works in tandem with
     * {@link #lastApplied()} to ensure interest is applied accordingly.
     *
     * @return The amount of time required to apply interest
     * @since 6.0.0
     */
    Duration applyAfter();

    /**
     * Specifies the exact instant interest was last applied. For some implementations, this could be useful in
     * determining just how much interest needs to be applied based on the amount of interest periods missed.
     *
     * @return The instant of the last interest adjustment
     * @since 6.0.0
     */
    Instant lastApplied();

    /**
     *
     *
     * @since 6.0.0
     */
    @FunctionalInterface
    interface Applier {

        /**
         * Applies the
         *
         * @param balance
         * @return
         * @since 6.0.0
         */
        BigDecimal apply(@NotNull BigDecimal balance);

    }

}
