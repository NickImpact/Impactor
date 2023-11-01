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

package net.impactdev.impactor.integrations.octo.fabric;

import com.epherical.octoecon.api.Economy;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.minecraft.api.text.AdventureTranslator;
import net.kyori.adventure.key.Key;
import net.minecraft.network.chat.Component;

import java.math.BigDecimal;

public final class CurrencyTranslator {

    public static Currency impactor(Economy economy, com.epherical.octoecon.api.Currency octo) {
        Currency.CurrencyBuilder builder = Currency.builder()
                .name(AdventureTranslator.fromNative(octo.getCurrencySingularName()))
                .plural(AdventureTranslator.fromNative(octo.getCurrencyPluralName()))
                .starting(BigDecimal.ZERO)
                .transferable(false)
                .symbol(AdventureTranslator.fromNative(octo.getCurrencySymbol()))
                .decimals(octo.decimalPlaces());

        if(economy.getDefaultCurrency().equals(octo)) {
            builder.primary();
        }

        return builder.key(keyForOcto(octo)).build();
    }

    public static com.epherical.octoecon.api.Currency octo(Currency impactor) {
        return new com.epherical.octoecon.api.Currency() {
            @Override
            public Component getCurrencySingularName() {
                return AdventureTranslator.toNative(impactor.singular());
            }

            @Override
            public Component getCurrencyPluralName() {
                return AdventureTranslator.toNative(impactor.plural());
            }

            @Override
            public Component getCurrencySymbol() {
                return AdventureTranslator.toNative(impactor.symbol());
            }

            @Override
            public Component format(double value) {
                return AdventureTranslator.toNative(impactor.format(BigDecimal.valueOf(value)));
            }

            @Override
            public Component format(double value, int decimalPlaces) {
                return AdventureTranslator.toNative(impactor.format(BigDecimal.valueOf(value)));
            }

            @Override
            public String getIdentity() {
                return impactor.key().asString();
            }
        };
    }

    @SuppressWarnings("PatternValidation")
    public static Key keyForOcto(com.epherical.octoecon.api.Currency currency) {
        String simple = currency.getIdentity();
        final int index = simple.indexOf(Key.DEFAULT_SEPARATOR);
        final String namespace = index >= 1 ? simple.substring(0, index) : "octo";
        final String value = index >= 0 ? simple.substring(index + 1) : simple;

        return Key.key(namespace, value);
    }

}
