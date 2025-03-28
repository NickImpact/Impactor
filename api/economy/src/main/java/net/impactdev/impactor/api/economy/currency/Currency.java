/*
 * This file is part of Impactor, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018-2022 NickImpact
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package net.impactdev.impactor.api.economy.currency;

import com.google.common.base.Strings;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import net.impactdev.impactor.api.core.ServiceProvider;
import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.api.economy.accounts.banks.Bank;
import net.impactdev.impactor.api.economy.currency.aspects.Banking;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.function.Consumer;

import static net.kyori.adventure.text.Component.text;

/**
 * Represents an identifiable source of currency to an {@link Account}. A currency is bundled with information
 * pertaining to its particular identifier, as well as how to display and configure a newly created Account.
 *
 * @since 5.0.0
 */
public interface Currency {

    record Naming(Component singular, Component plural) {
        Component resolve(final @NotNull BigDecimal amount) {
            boolean singular = amount.compareTo(BigDecimal.ONE) == 0;
            return singular ? this.singular : this.plural;
        }
    }

    /**
     * A key used to represent the currency. This key should be unique to any other
     * registered currency, and will be used for details such as data management.
     *
     * @return A key representing the currency
     * @since 5.0.0
     */
    Key key();

    /**
     * Specifies naming rules for this currency. This namely pertains to the difference between
     * singular and plural values for an Account's balance.
     *
     * @return Naming rules for the currency
     * @since 6.0.0
     */
    Naming naming();

    /**
     * Specifies the symbol that should be used to represent the currency when formatting
     * in condensed mode.
     *
     * @return A symbol for this currency
     * @since 5.0.0
     */
    Component symbol();

    /**
     * The amount of money an account using this balance should start with, whether the
     * account has just been created or has processed a {@link Account#reset() reset} request.
     *
     * @return The standard starting balance for an account bound to this currency
     * @since 6.0.0
     */
    BigDecimal defaultAccountBalance();

    /**
     * Indicates how many decimal places should be used when formatting the number for this
     * currency. These decimal places will always be present, whether needed or unneeded.
     * For instance, for US currency, we would have a value of 2 to indicate a value
     * such as $0.00.
     *
     * @return The number of decimal places used to format a number
     * @since 5.0.0
     */
    int decimals();

    /**
     *
     * @return
     */
    BigDecimal minimum();

    /**
     * 
     * @return
     */
    BigDecimal maximum();

    /**
     * Creates a generic {@link TagResolver} for users of MiniMessage, which may wish to display a currency
     * using the given amount and locale.
     *
     * @param amount The amount which represents the accounts balance.
     * @param locale The locale used to display the given amount
     * @return A TagResolver bundling the relative components of the currency + formatting.
     * @since 6.0.0
     */
    default TagResolver createTagResolver(final @NotNull BigDecimal amount, final @NotNull Locale locale) {
        DecimalFormat formatter = new DecimalFormat("#,##0" + Strings.repeat("#", this.decimals()), new DecimalFormatSymbols(locale));

        return TagResolver
                .builder()
                .resolver(Placeholder.component("balance", text(formatter.format(amount))))
                .resolver(Placeholder.component("key", text(this.key().asString())))
                .tag("symbol", Tag.selfClosingInserting(this.symbol()))
                .tag("name", (args, context) -> Tag.selfClosingInserting(this.naming().resolve(amount)))
                .build();
    }

    /**
     * Given a {@link BigDecimal}, formats the value into a component using the condensed pattern.
     * This condensed pattern simply uses the {@link #symbol()} versus the contextual names
     * of the currency to write the component.
     *
     * <p>In terms of formatting this amount, the value of {@link Locale#ROOT} will be used
     * to determine how exactly to write the number.
     *
     * @param amount A {@link BigDecimal} representing some form of monetary value
     * @return A component representing the amount formatted into this currency's locale scheme
     * @since 5.0.0
     */
    default Component format(final @NotNull BigDecimal amount) {
        return this.format(amount, true);
    }

    /**
     * Given a {@link BigDecimal}, formats the value into a component using the condensed pattern.
     * This condensed pattern simply uses the {@link #symbol()} versus the contextual names
     * of the currency to write the component.
     *
     * <p>In terms of formatting this amount, the given locale will be used for translating
     * the given number into the locale's method of displaying currencies.
     *
     * @param amount A {@link BigDecimal} representing some form of monetary value
     * @return A component representing the amount formatted into this currency's locale scheme
     * @since 5.0.0
     */
    default Component format(final @NotNull BigDecimal amount, final @NotNull Locale locale) {
        return this.format(amount, true, locale);
    }

    /**
     * Given a {@link BigDecimal}, formats the value into a component, optionally condensed
     * or formatted using the {@link Naming#singular()} or {@link Naming#plural()} names, given the value
     * specified.
     *
     * <p>In terms of formatting this amount, the value of {@link Locale#ROOT} will be used
     * to determine how exactly to write the number.
     *
     * @param amount    A {@link BigDecimal} representing some form of monetary value
     * @param condensed Whether the formatter should use the symbol for the currency, or delegate
     *                  to the contextual names
     * @return A component representing the amount formatted into this currency's locale scheme
     * @since 5.0.0
     */
    default Component format(final @NotNull BigDecimal amount, final boolean condensed) {
        return this.format(amount, condensed, Locale.ROOT);
    }

    /**
     * Given a {@link BigDecimal}, formats the value into a component, optionally condensed
     * or formatted using the {@link Naming#singular()} or {@link Naming#plural()} names, given the value
     * specified.
     *
     * <p>When formatting the amount specified, this will use the given locale to ensure
     * the formatted value meets the locale's rules. For instance, this will replace dots
     * with commas where necessary.
     *
     * @param amount    A {@link BigDecimal} representing some form of monetary value
     * @param condensed Whether the formatter should use the symbol for the currency, or delegate
     *                  to the contextual names
     * @param locale    The locale that should be used to format the given amount
     * @return A component representing the amount formatted into this currency's locale scheme
     * @since 5.0.0
     */
    Component format(final @NotNull BigDecimal amount, final boolean condensed, final @NotNull Locale locale);

    static Builder builder() {
        return ServiceProvider.instance().provide(Builder.class);
    }

    interface Builder {

        /**
         * The unique key that will be used to describe this currency. A key is the primary means of uniquely
         * identifying a particular currency from another. This allows currencies to share the same name, yet
         * co-exist with one another so long as their key is different.
         *
         * @param key The key to identify this currency
         * @return This builder
         */
        @CanIgnoreReturnValue
        @Contract("_ -> this")
        Builder key(final @NotNull Key key);

        /**
         * The singular representation of the currency's name.
         *
         * @param singular The singular representation of the currency.
         * @param plural The plural representation of the currency
         * @return This builder
         */
        @CanIgnoreReturnValue
        @Contract("_,_ -> this")
        Builder displayName(final @NotNull Component singular, Component plural);

        /**
         * Specifies the symbol used by this currency. For American dollars, this would be the $ symbol.
         *
         * @param symbol The symbol to represent the currency
         * @return This builder
         */
        @CanIgnoreReturnValue
        @Contract("_ -> this")
        Builder symbol(final @NotNull Component symbol);

        /**
         * Specifies the starting balance of an account generated for this currency. Additionally, accounts that
         * are reset will be set back to this amount.
         *
         * @param amount The amount for accounts to start with
         * @return This builder
         */
        @CanIgnoreReturnValue
        @Contract("_ -> this")
        Builder starting(final @NotNull BigDecimal amount);

        /**
         * Specifies the number of decimals allowed when displaying this currency.
         *
         * @param decimals The number of decimal points to allow
         * @return This builder
         */
        @CanIgnoreReturnValue
        @Contract("_ -> this")
        Builder decimals(final int decimals);

        /**
         * Specifies the formatting technique to use for this Currency. This should be a MiniMessage compatible
         * string.
         *
         * <p>Internally, the parser for a currency will supply placeholders for the currency and the balance
         * that is being formatted. These placeholders are typically:
         * <ul>
         *     <li>symbol - The symbol used by the currency</li>
         *     <li>currency - The name of the currency, formatted by plural rules</li>
         *     <li>amount - The amount of currency being formatted</li>
         * </ul>
         * </p>
         *
         * @param format A MiniMessage compatible string to supply the format
         * @return This builder
         */
        @CanIgnoreReturnValue
        @Contract("_ -> this")
        Builder formatting(final @NotNull String format);

        /**
         * The minimum balance an account can hold. Balances are not able to go beyond this value. By default,
         * this value will typically be 0.
         *
         * @param minimum The minimum balance of an account bound to this currency
         * @return This builder
         */
        @CanIgnoreReturnValue
        @Contract("_ -> this")
        Builder minimum(final @NotNull BigDecimal minimum);

        /**
         * The maximum balance an account can ever reach. Balances are not able to go beyond this value. By default,
         * this will typically be 1 trillion.
         *
         * @param maximum The maximum balance of an account bound to this currency
         * @return This builder
         */
        @CanIgnoreReturnValue
        @Contract("_ -> this")
        Builder maximum(final @NotNull BigDecimal maximum);

        /**
         * Sets the minimum and maximum balance an account will be limited to.
         *
         * @param min The minimum balance of an account bound to this currency
         * @param max The maximum balance of an account bound to this currency
         * @return This builder
         */
        @CanIgnoreReturnValue
        @Contract("_,_ -> this")
        Builder boundaries(final @NotNull BigDecimal min, final @NotNull BigDecimal max);

        /**
         *
         * @param config
         * @return
         * @since 6.0.0
         */
        Builder banking(final @NotNull Consumer<Banking.Config> config);

        /**
         * Creates the currency using this builder's configuration. Unfilled fields will supply defaults,
         * with the only requirement of a currency being its key.
         *
         * @return The configured currency
         */
        Currency build();

    }

}
