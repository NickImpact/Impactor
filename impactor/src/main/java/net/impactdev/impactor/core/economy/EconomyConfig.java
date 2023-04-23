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

package net.impactdev.impactor.core.economy;

import com.google.common.collect.Lists;
import net.impactdev.impactor.api.configuration.key.ConfigKey;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.storage.StorageType;
import net.kyori.adventure.key.Key;

import java.math.BigDecimal;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;

import static net.impactdev.impactor.api.configuration.key.ConfigKeyFactory.booleanKey;
import static net.impactdev.impactor.api.configuration.key.ConfigKeyFactory.doubleKey;
import static net.impactdev.impactor.api.configuration.key.ConfigKeyFactory.key;
import static net.kyori.adventure.text.Component.text;

public final class EconomyConfig {

    public static final ConfigKey<StorageType> STORAGE_TYPE = key(adapter ->
            StorageType.parse(adapter.getString("storage-method", "json"))
    );

    public static final ConfigKey<Boolean> APPLY_RESTRICTIONS = booleanKey("restrictions.enabled", true);
    public static final ConfigKey<BigDecimal> MIN_BALANCE = key(adapter -> {
        double value = adapter.getDouble("restrictions.minimum-balance", 0.0);
        return BigDecimal.valueOf(value);
    });
    public static final ConfigKey<BigDecimal> MAX_BALANCE = key(adapter -> {
        double value = adapter.getDouble("restrictions.maximum-balance", 100000000000.0);
        return BigDecimal.valueOf(value);
    });
    public static final ConfigKey<Boolean> ALLOW_TRANSFER_CROSS_CURRENCY = booleanKey("restrictions.allow-cross-currency-transfers", false);

    @SuppressWarnings("PatternValidation")
    public static final ConfigKey<List<Currency>> CURRENCIES = key(adapter -> {
        List<Currency> results = Lists.newArrayList();
        for(String option : adapter.getKeys("currencies", Lists.newArrayList("dollars"))) {
            StringJoiner joiner = new StringJoiner(".")
                    .add("currencies")
                    .add(option);

            Function<String, String> modifier = key -> joiner + "." + key;

            Currency.CurrencyBuilder builder = Currency.builder()
                    .key(Key.key(option))
                    .name(text(adapter.getString(modifier.apply("singular"), "Dollar")))
                    .plural(text(adapter.getString(modifier.apply("plural"), "Dollars")))
                    .symbol(text(adapter.getString(modifier.apply("symbol.character"), "$")))
                    .formatting(Currency.SymbolFormatting.fromIdentifier(adapter.getString(
                            modifier.apply("symbol.placement"),
                            Currency.SymbolFormatting.BEFORE.name().toLowerCase()
                    )))
                    .decimals(adapter.getInteger(modifier.apply("decimals"), 2))
                    .starting(BigDecimal.valueOf(adapter.getDouble(modifier.apply("default-balance"), 500)));

            if(adapter.getBoolean(modifier.apply("primary"), false)) {
                builder.primary();
            }

            results.add(builder.build());
        }

        return results;
    });

}
