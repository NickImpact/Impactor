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
import net.impactdev.impactor.api.events.ImpactorEvent;
import net.impactdev.impactor.api.logging.PluginLogger;
import net.impactdev.impactor.api.providers.BuilderProvider;
import net.impactdev.impactor.api.providers.FactoryProvider;
import net.impactdev.impactor.api.providers.ServiceProvider;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.core.economy.currency.ImpactorCurrency;
import net.impactdev.impactor.core.economy.registration.EconomyRegistrationProvider;
import net.impactdev.impactor.core.economy.registration.ImpactorSuggestEconomyServiceEvent;
import net.impactdev.impactor.core.modules.ImpactorModule;
import net.kyori.event.EventBus;

public class EconomyModule implements ImpactorModule {
    @Override
    public void factories(FactoryProvider provider) {}

    @Override
    public void builders(BuilderProvider provider) {
        provider.register(Currency.CurrencyBuilder.class, ImpactorCurrency.ImpactorCurrencyBuilder::new);
    }

    @Override
    public void services(ServiceProvider provider) {}

    @Override
    public void subscribe(EventBus<ImpactorEvent> bus) {

    }

    @Override
    public void init(Impactor service, PluginLogger logger) throws Exception {
        EconomyRegistrationProvider economy = new EconomyRegistrationProvider();
        service.events().post(new ImpactorSuggestEconomyServiceEvent(economy));

        logger.info("Registering economy service (Provider: "
                + economy.suggestion().metadata().name().orElse(economy.suggestion().metadata().id())
                + ")"
        );
        service.services().register(EconomyService.class, economy.suggestion().supplier().get());
    }
}
