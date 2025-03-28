package net.impactdev.impactor.api.economy.builtin.currency;

import com.google.inject.Inject;
import net.impactdev.impactor.api.config.Config;
import net.impactdev.impactor.api.economy.builtin.EconomyConfigKeys;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.economy.currency.CurrencyProvider;
import net.kyori.adventure.key.Key;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class ImpactorCurrencyProvider implements CurrencyProvider {

    private final Logger logger = LogManager.getLogger("Impactor Currency Service");
    private final Map<Key, Currency> currencies = new ConcurrentHashMap<>();
    private final Currency primary;

    @Inject
    public ImpactorCurrencyProvider(final Config config) {
        this.logger.debug("Setting up currencies...");
        config.get(EconomyConfigKeys.CURRENCIES).forEach(currency -> this.currencies.put(currency.key(), currency));

        this.primary = this.currencies.computeIfAbsent(
                config.get(EconomyConfigKeys.PRIMARY_CURRENCY), key -> {
                    throw new IllegalArgumentException("Invalid primary currency definition: " + key);
                }
        );
    }

    @Override
    public @NotNull Currency primary() {
        return this.primary;
    }

    @Override
    public Optional<Currency> currency(Key key) {
        return Optional.ofNullable(this.currencies.get(key));
    }

}
