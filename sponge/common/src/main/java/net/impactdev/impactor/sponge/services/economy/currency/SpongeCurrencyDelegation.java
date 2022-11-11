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

package net.impactdev.impactor.sponge.services.economy.currency;

import net.impactdev.impactor.economy.currency.ImpactorCurrency;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.service.economy.Currency;

import java.math.BigDecimal;

public class SpongeCurrencyDelegation implements Currency {

    private final ImpactorCurrency delegate;

    public SpongeCurrencyDelegation(ImpactorCurrency delegate) {
        this.delegate = delegate;
    }

    @Override
    public Component displayName() {
        return this.delegate.name();
    }

    @Override
    public Component pluralDisplayName() {
        return this.delegate.plural();
    }

    @Override
    public Component symbol() {
        return this.delegate.symbol();
    }

    @Override
    public Component format(BigDecimal amount, int numFractionDigits) {
        return this.delegate.format(amount);
    }

    @Override
    public int defaultFractionDigits() {
        return this.delegate.decimals();
    }

    @Override
    public boolean isDefault() {
        return this.delegate.primary();
    }
}
