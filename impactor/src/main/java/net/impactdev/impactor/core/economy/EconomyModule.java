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

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.economy.EconomyService;
import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.api.economy.transactions.composer.TransactionComposer;
import net.impactdev.impactor.api.economy.transactions.composer.TransferComposer;
import net.impactdev.impactor.api.events.ImpactorEvent;
import net.impactdev.impactor.api.logging.PluginLogger;
import net.impactdev.impactor.api.providers.BuilderProvider;
import net.impactdev.impactor.api.providers.FactoryProvider;
import net.impactdev.impactor.api.providers.ServiceProvider;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.core.commands.economy.EconomyCommands;
import net.impactdev.impactor.core.commands.events.RegisterCommandsEvent;
import net.impactdev.impactor.core.economy.accounts.ImpactorAccount;
import net.impactdev.impactor.core.economy.currency.ImpactorCurrency;
import net.impactdev.impactor.core.economy.registration.EconomyRegistrationProvider;
import net.impactdev.impactor.core.economy.registration.ImpactorSuggestEconomyServiceEvent;
import net.impactdev.impactor.core.economy.transactions.composers.BaseTransactionComposer;
import net.impactdev.impactor.core.economy.transactions.composers.TransferTransactionComposer;
import net.impactdev.impactor.core.modules.ImpactorModule;
import net.kyori.adventure.text.Component;
import net.kyori.event.EventBus;

public class EconomyModule implements ImpactorModule {

    @Override
    public void builders(BuilderProvider provider) {
        provider.register(Currency.CurrencyBuilder.class, ImpactorCurrency.ImpactorCurrencyBuilder::new);
        provider.register(Account.AccountBuilder.class, ImpactorAccount.ImpactorAccountBuilder::new);

        provider.register(TransactionComposer.class, BaseTransactionComposer::new);
        provider.register(TransferComposer.class, TransferTransactionComposer::new);
    }

    @Override
    public void init(Impactor api, PluginLogger logger) throws Exception {
        EconomyRegistrationProvider economy = new EconomyRegistrationProvider();
        api.events().post(new ImpactorSuggestEconomyServiceEvent(economy));

        String service = economy.suggestion().metadata().name().orElse(economy.suggestion().metadata().id());
        logger.info("Registering economy service (Provider: " + service + ")");
        api.services().register(EconomyService.class, economy.suggestion().supplier().get());
    }
}
