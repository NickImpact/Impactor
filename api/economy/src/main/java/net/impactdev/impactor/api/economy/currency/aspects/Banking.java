package net.impactdev.impactor.api.economy.currency.aspects;

import net.impactdev.impactor.api.economy.accounts.banks.Interest;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 *
 *
 * @since 6.0.0
 */
public interface Banking {

    /**
     *
     * @return
     * @since 6.0.0
     */
    Interest.Applier applier();

    /**
     *
     *
     * @since 6.0.0
     */
    interface Config {

        /**
         *
         * @param enabled
         * @return
         * @since 6.0.0
         */
        Config enabled(final boolean enabled);

        /**
         * Applies settings related to interest on a bank's stored currency.
         *
         * @param interest
         * @return
         * @since 6.0.0
         */
        Config interest(final Consumer<InterestConfig> interest);

    }

    /**
     *
     *
     * @since 6.0.0
     */
    interface InterestConfig {

        InterestConfig enabled(final boolean enabled);

        InterestConfig applier(final @NotNull Interest.Applier applier);

    }


}
