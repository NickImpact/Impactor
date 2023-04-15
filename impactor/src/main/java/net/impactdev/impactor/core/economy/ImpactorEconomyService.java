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

import com.google.common.collect.Multimap;
import net.impactdev.impactor.api.configuration.Config;
import net.impactdev.impactor.api.economy.EconomyService;
import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.economy.currency.CurrencyProvider;
import net.impactdev.impactor.api.storage.StorageType;
import net.impactdev.impactor.api.utility.ExceptionPrinter;
import net.impactdev.impactor.core.economy.currency.ImpactorCurrencyProvider;
import net.impactdev.impactor.core.economy.storage.EconomyStorage;
import net.impactdev.impactor.core.economy.storage.StorageFactory;
import net.impactdev.impactor.core.plugin.BaseImpactorPlugin;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class ImpactorEconomyService implements EconomyService {

    private final CurrencyProvider provider;
    private final EconomyStorage storage;
    private final Config config;

    public ImpactorEconomyService() {
        this.config = Config.builder()
                .path(BaseImpactorPlugin.instance().configurationDirectory().resolve("economy.conf"))
                .provider(EconomyConfig.class)
                .provideIfMissing(() -> BaseImpactorPlugin.instance().resource(root -> root.resolve("configs").resolve("economy.conf")))
                .build();

        List<Currency> currencies = this.config.get(EconomyConfig.CURRENCIES);
        if(currencies.isEmpty()) {
            throw new IllegalStateException("No currencies defined");
        }

        this.provider = new ImpactorCurrencyProvider(currencies);
        this.storage = StorageFactory.instance(BaseImpactorPlugin.instance(), this.config.get(EconomyConfig.STORAGE_TYPE), StorageType.JSON);

        try {
            this.storage.init();
        } catch (Exception e) {
            ExceptionPrinter.print(BaseImpactorPlugin.instance().logger(), e);
        }
    }

    @Override
    public String name() {
        return "Impactor Economy Service";
    }

    @Override
    public CurrencyProvider currencies() {
        return this.provider;
    }

    public EconomyStorage storage() {
        return this.storage;
    }

    public Config config() {
        return this.config;
    }

    @Override
    public CompletableFuture<Boolean> hasAccount(Currency currency, UUID uuid) {
        return this.storage.hasAccount(currency, uuid);
    }

    @Override
    public CompletableFuture<Account> account(Currency currency, UUID uuid) {
        return this.storage.account(currency, uuid, builder -> builder);
    }

    @Override
    public CompletableFuture<Account> account(Currency currency, UUID uuid, Account.AccountModifier modifier) {
        return this.storage.account(currency, uuid, modifier);
    }

    @Override
    public CompletableFuture<Multimap<Currency, Account>> accounts() {
        return this.storage.accounts();
    }

    @Override
    public CompletableFuture<Void> deleteAccount(Currency currency, UUID uuid) {
        return this.storage.delete(currency, uuid);
    }
}
