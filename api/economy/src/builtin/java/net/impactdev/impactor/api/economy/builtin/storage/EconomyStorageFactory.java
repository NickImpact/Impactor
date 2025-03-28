package net.impactdev.impactor.api.economy.builtin.storage;

import net.impactdev.impactor.api.config.Config;
import net.impactdev.impactor.api.economy.builtin.EconomyConfigKeys;
import net.impactdev.impactor.api.economy.builtin.storage.implementations.ConfigurateProvider;
import net.impactdev.impactor.api.storage.StorageType;
import net.impactdev.impactor.api.storage.StorageTypes;
import net.impactdev.impactor.api.storage.connections.flatfile.HoconLoader;
import net.impactdev.impactor.api.storage.connections.flatfile.JsonLoader;
import net.impactdev.impactor.api.storage.connections.flatfile.YamlLoader;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public final class EconomyStorageFactory {

    private static final Map<StorageType, Function<Config, EconomyStorageImplementation>> implementations = new HashMap<>();

    static {
        implementations.put(StorageTypes.JSON, config -> new ConfigurateProvider(new JsonLoader()));
        implementations.put(StorageTypes.HOCON, config -> new ConfigurateProvider(new HoconLoader()));
        implementations.put(StorageTypes.YAML, config -> new ConfigurateProvider(new YamlLoader()));
    }

    /**
     * Creates the storage facet responsible for processing IO. The backing implementation is defined by the user,
     * and is fetched via the general configuration.
     *
     * @param logger The logger used by the service
     * @param config The config used by the service
     * @param fallback A fallback type, such that if the requested config option isn't available, the fallback will
     *                 be used instead
     * @return A new storage facet, backed with the user defined storage type, or the fallback type
     * @since 6.0.0
     */
    public static EconomyStorage create(final Logger logger, final Config config, final StorageType fallback) {
        StorageType provider = Optional.ofNullable(config.get(EconomyConfigKeys.STORAGE_METHOD)).orElse(fallback);
        logger.info("Loading storage provider... [{}]", provider.name());

        return new EconomyStorage(createImplementation(provider, config));
    }

    private static EconomyStorageImplementation createImplementation(final StorageType target, final Config config) {
        return Optional.ofNullable(implementations.get(target))
                .map(provider -> provider.apply(config))
                .orElseThrow(() -> new IllegalArgumentException("No implementation found for " + target.name()));
    }

}
