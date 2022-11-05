package net.impactdev.impactor.sponge.services.economy.currency;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.impactdev.impactor.economy.currency.ImpactorCurrency;
import org.spongepowered.api.service.economy.Currency;

public final class SpongeCurrencyDelegator {

    private static final BiMap<ImpactorCurrency, Currency> delegators = HashBiMap.create();

    public static void register(ImpactorCurrency currency) {
        delegators.put(currency, new SpongeCurrencyDelegation(currency));
    }

    public static Currency sponge(ImpactorCurrency currency) {
        return delegators.get(currency);
    }

    public static ImpactorCurrency impactor(Currency currency) {
        return delegators.inverse().get(currency);
    }

}
