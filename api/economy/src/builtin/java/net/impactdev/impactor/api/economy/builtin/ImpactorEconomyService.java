package net.impactdev.impactor.api.economy.builtin;

import net.impactdev.impactor.api.config.Config;
import net.impactdev.impactor.api.economy.EconomyService;
import net.impactdev.impactor.api.economy.accounts.AccountManager;
import net.impactdev.impactor.api.economy.builtin.accounts.ImpactorAccountManager;
import net.impactdev.impactor.api.economy.builtin.currency.ImpactorCurrencyProvider;
import net.impactdev.impactor.api.economy.builtin.networking.EconomyNetworkingFactory;
import net.impactdev.impactor.api.economy.builtin.networking.EconomyNetworkingService;
import net.impactdev.impactor.api.economy.builtin.storage.EconomyStorage;
import net.impactdev.impactor.api.economy.builtin.storage.EconomyStorageFactory;
import net.impactdev.impactor.api.economy.currency.CurrencyProvider;
import net.impactdev.impactor.api.platform.events.LifecycleEvents;
import net.impactdev.impactor.api.storage.StorageTypes;
import net.impactdev.impactor.loader.Version;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public final class ImpactorEconomyService implements EconomyService {

    public static final EconomyService.ServiceDetails DETAILS = new EconomyService.ServiceDetails() {
        @Override
        public String identifier() {
            return "impactor";
        }

        @Override
        public Version version() {
            return new Version(6, 0, 0);
        }
    };

    private final Logger logger = LogManager.getLogger("Impactor Economy Service");

    private final CurrencyProvider currencies;
    private final AccountManager accounts;
    private final EconomyStorage storage;
    private final EconomyNetworkingService networking;

    public ImpactorEconomyService(final @NotNull Config config) {
        this.logger.info("Starting economy service...");

        this.storage = EconomyStorageFactory.create(this.logger, config, StorageTypes.JSON);
        this.networking = new EconomyNetworkingFactory(this.logger).create(config);

        this.currencies = new ImpactorCurrencyProvider(config);
        this.accounts = new ImpactorAccountManager(this.storage, this.networking);

        LifecycleEvents.SERVER_STOPPING.subscribe(event -> {
            this.logger.info("Shutting down economy service...");

            this.storage.shutdown();
        });
    }

    @Override
    public ServiceDetails details() {
        return DETAILS;
    }

    @Override
    public CurrencyProvider currencies() {
        return this.currencies;
    }

    @Override
    public AccountManager accounts() {
        return this.accounts;
    }

    public EconomyNetworkingService networking() {
        return this.networking;
    }

}
