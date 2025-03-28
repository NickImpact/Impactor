package net.impactdev.impactor.api.economy.builtin.networking;

import net.impactdev.impactor.api.config.Config;
import net.impactdev.impactor.api.economy.builtin.EconomyConfigKeys;
import net.impactdev.impactor.api.networking.Messenger;
import net.impactdev.impactor.api.networking.messengers.NoOpMessenger;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public final class EconomyNetworkingFactory {

    private final Logger logger;

    public EconomyNetworkingFactory(Logger logger) {
        this.logger = logger;
    }

    public EconomyNetworkingService create(Config config) {
        final String method = config.get(EconomyConfigKeys.NETWORKING_METHOD);
        final Messenger.Provider provider = this.evaluate(method);

        this.logger.info("Loading networking service... [{}]", provider.name().toUpperCase(Locale.ROOT));
        return new EconomyNetworkingService(this.logger, provider);
    }

    @NotNull
    private Messenger.Provider evaluate(final String method) {
        if (method.equalsIgnoreCase("none")) {
            return new NoOpMessenger.Provider();
        }

        throw new IllegalArgumentException("Invalid networking method: " + method);
    }

}
