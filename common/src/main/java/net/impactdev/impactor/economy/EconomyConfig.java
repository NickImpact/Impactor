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

package net.impactdev.impactor.economy;

import com.google.common.collect.Lists;
import net.impactdev.impactor.api.configuration.ConfigKey;
import net.impactdev.impactor.api.configuration.ConfigPath;
import net.impactdev.impactor.api.configuration.loader.KeyProvider;
import net.impactdev.impactor.api.services.economy.currency.Currency;
import net.impactdev.impactor.api.storage.StorageType;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.intellij.lang.annotations.Subst;

import java.math.BigDecimal;
import java.util.List;

import static net.impactdev.impactor.api.configuration.ConfigKeyTypes.customKey;
import static net.impactdev.impactor.api.configuration.ConfigPath.path;
import static net.kyori.adventure.text.Component.text;

@KeyProvider
public final class EconomyConfig {

    public static final ConfigKey<StorageType> STORAGE_TYPE = customKey(adapter ->
            StorageType.parse(adapter.getString(path("storage-method"), "json"))
    );
    public static final ConfigKey<List<Currency>> CURRENCIES = customKey(adapter -> {
        List<Currency> results = Lists.newArrayList();
        ConfigPath path = path("currencies");
        for(@Subst("dollars") String option : adapter.getKeys(path, Lists.newArrayList("dollars"))) {
            Key key = Key.key("impactor", option);
            ConfigPath config = path.resolve(option);

            results.add(Currency.builder()
                    .key(key)
                    .primary(adapter.getBoolean(config.resolve("primary"), true))
                    .name(text(adapter.getString(config.resolve("name"), "Dollar")))
                    .plural(text(adapter.getString(config.resolve("plural"), "Dollars")))
                    .symbol(text(adapter.getString(config.resolve("symbol"), "$")))
                    .decimals(adapter.getInteger(config.resolve("decimals"), 2))
                    .starting(BigDecimal.valueOf(adapter.getDouble(config.resolve("starting"), 500)))
                    .build()
            );
        }

        return results;
    });

}
