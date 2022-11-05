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

package net.impactdev.impactor.economy.currency;

import joptsimple.internal.Strings;
import net.impactdev.impactor.api.services.economy.currency.Currency;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.StringJoiner;

public class ImpactorCurrency implements Currency {

    private final Key key;
    private final boolean primary;

    private final Component name;
    private final Component plural;
    private final Component symbol;
    private final BigDecimal starting;
    private final int decimals;

    private final DecimalFormat formatter;

    private ImpactorCurrency(final ImpactorCurrencyBuilder builder) {
        this.key = builder.key;
        this.primary = builder.primary;
        this.name = builder.name;
        this.plural = builder.plural;
        this.symbol = builder.symbol;
        this.starting = builder.starting;
        this.decimals = builder.decimals;

        this.formatter = this.createFormatter();
    }

    @Override
    public Key key() {
        return this.key;
    }

    @Override
    public boolean primary() {
        return this.primary;
    }

    @Override
    public Component name() {
        return this.name;
    }

    @Override
    public Component plural() {
        return null;
    }

    @Override
    public Component symbol() {
        return this.symbol;
    }

    @Override
    public Component format(BigDecimal amount) {
        return Component.text(this.formatter.format(amount));
    }

    @Override
    public BigDecimal starting() {
        return this.starting;
    }

    @Override
    public int decimals() {
        return this.decimals;
    }

    private DecimalFormat createFormatter() {
        StringJoiner joiner = new StringJoiner(".");
        joiner.add("0");
        joiner.add(Strings.repeat('#', this.decimals()));

        return new DecimalFormat(joiner.toString());
    }

    public static class ImpactorCurrencyBuilder implements CurrencyBuilder {

        private Key key;
        private boolean primary;

        private Component name;
        private Component plural;
        private Component symbol;
        private BigDecimal starting;
        private int decimals;

        @Override
        public CurrencyBuilder key(Key key) {
            this.key = key;
            return this;
        }

        @Override
        public CurrencyBuilder primary(boolean primary) {
            this.primary = primary;
            return this;
        }

        @Override
        public CurrencyBuilder name(Component name) {
            this.name = name;
            return this;
        }

        @Override
        public CurrencyBuilder plural(Component plural) {
            this.plural = plural;
            return this;
        }

        @Override
        public CurrencyBuilder symbol(Component symbol) {
            this.symbol = symbol;
            return this;
        }

        @Override
        public CurrencyBuilder starting(BigDecimal amount) {
            this.starting = amount;
            return this;
        }

        @Override
        public CurrencyBuilder decimals(int decimals) {
            this.decimals = decimals;
            return this;
        }

        @Override
        public Currency build() {
            return new ImpactorCurrency(this);
        }
    }
}
