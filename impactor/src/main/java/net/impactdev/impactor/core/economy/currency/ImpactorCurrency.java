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

package net.impactdev.impactor.core.economy.currency;

import net.impactdev.impactor.api.economy.currency.Currency;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import net.luckperms.api.util.Tristate;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Locale;

import static net.kyori.adventure.text.Component.text;

public class ImpactorCurrency implements Currency {

    private final Key key;

    private final Component name;
    private final Component plural;
    private final Component symbol;
    private final BigDecimal starting;
    private final int decimals;
    private final SymbolFormatting formatting;
    private final boolean primary;

    private final String pattern;

    private ImpactorCurrency(final ImpactorCurrencyBuilder builder) {
        this.key = builder.key;
        this.name = builder.name;
        this.plural = builder.plural;
        this.symbol = builder.symbol;
        this.starting = builder.starting;
        this.decimals = builder.decimals;
        this.formatting = builder.formatting;
        this.primary = builder.primary;

        this.pattern = "%." + this.decimals + "f";
    }

    @Override
    public Key key() {
        return this.key;
    }

    @Override
    public Component singular() {
        return this.name;
    }

    @Override
    public Component plural() {
        return this.plural;
    }

    @Override
    public Component symbol() {
        return this.symbol;
    }

    @Override
    public SymbolFormatting formatting() {
        return this.formatting;
    }

    @Override
    public Component format(@NotNull BigDecimal amount, boolean condensed, @NotNull Locale locale) {
        Component value = text(String.format(locale, this.pattern, amount.doubleValue()));
        if(condensed) {
            return this.formatting.modify(this, value);
        }

        if(amount.doubleValue() == 1) {
            return value.append(Component.space()).append(this.singular());
        } else {
            return value.append(Component.space()).append(this.plural());
        }
    }

    @Override
    public BigDecimal defaultAccountBalance() {
        return this.starting;
    }

    @Override
    public int decimals() {
        return this.decimals;
    }

    @Override
    public boolean primary() {
        return this.primary;
    }

    @Override
    public TriState transferable() {
        return null;
    }

    public static class ImpactorCurrencyBuilder implements CurrencyBuilder {

        private Key key;

        private Component name;
        private Component plural;
        private Component symbol;
        private SymbolFormatting formatting;
        private BigDecimal starting;
        private int decimals;
        private boolean primary;
        private TriState transferable = TriState.NOT_SET;

        @Override
        public CurrencyBuilder key(@NotNull Key key) {
            this.key = key;
            return this;
        }

        @Override
        public CurrencyBuilder name(@NotNull Component name) {
            this.name = name;
            return this;
        }

        @Override
        public CurrencyBuilder plural(@NotNull Component plural) {
            this.plural = plural;
            return this;
        }

        @Override
        public CurrencyBuilder symbol(@NotNull Component symbol) {
            this.symbol = symbol;
            return this;
        }

        @Override
        public CurrencyBuilder formatting(@NotNull SymbolFormatting format) {
            this.formatting = format;
            return this;
        }

        @Override
        public CurrencyBuilder starting(@NotNull BigDecimal amount) {
            this.starting = amount;
            return this;
        }

        @Override
        public CurrencyBuilder decimals(int decimals) {
            this.decimals = decimals;
            return this;
        }

        @Override
        public CurrencyBuilder primary() {
            this.primary = true;
            return this;
        }

        public CurrencyBuilder transferable(final boolean state) {
            this.transferable = TriState.byBoolean(state);
            return this;
        }

        @Override
        public Currency build() {
            return new ImpactorCurrency(this);
        }
    }
}
