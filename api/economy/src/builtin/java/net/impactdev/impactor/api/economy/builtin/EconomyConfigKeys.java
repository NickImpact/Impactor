package net.impactdev.impactor.api.economy.builtin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.impactdev.impactor.api.config.key.ConfigKey;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.networking.messengers.redis.RedisConfig;
import net.impactdev.impactor.api.storage.StorageType;
import net.impactdev.impactor.api.storage.connections.StorageCredentials;
import net.kyori.adventure.key.Key;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Function;

import static net.impactdev.impactor.api.config.key.ConfigKeyFactory.booleanKey;
import static net.impactdev.impactor.api.config.key.ConfigKeyFactory.key;
import static net.impactdev.impactor.api.config.key.ConfigKeyFactory.notReloadable;
import static net.impactdev.impactor.api.config.key.ConfigKeyFactory.stringKey;
import static net.kyori.adventure.text.Component.text;

public final class EconomyConfigKeys {

    /**
     * The ultimate determiner of which service will be the executing service by the server. This allows
     * an individual server with multiple options to choose their intended service, rather than allow plugins/mods
     * to determine the final result.
     *
     * @since 6.0.0
     */
    public static final ConfigKey<String> SERVICE_REQUEST = stringKey("service", "impactor");

    public static final ConfigKey<String> SERVER_IDENTIFIER = stringKey("server", "global");

    // -----------------------------------------------------------------------------------------------------------------
    //
    // Currency Configuration
    //
    // -----------------------------------------------------------------------------------------------------------------

    @SuppressWarnings("PatternValidation")
    public static final ConfigKey<Key> PRIMARY_CURRENCY = key(adapter -> Key.key(adapter.getString("currencies.primary", "impactor:dollars")));

    @SuppressWarnings("PatternValidation")
    public static final ConfigKey<List<Currency>> CURRENCIES = key(adapter -> {
        List<Currency> currencies = new ArrayList<>();
        List<String> definitions = adapter.getStringList("currencies", Collections.emptyList());
        for (String definition : definitions) {
            StringJoiner joiner = new StringJoiner(".").add("currencies").add(definition);

            Function<String, String> modifier = key -> joiner + "." + key;
            currencies.add(Currency
                    .builder()
                    .key(Key.key(definition))
                    .name(text(adapter.getString(modifier.apply("singular"), "Dollar")))
                    .plural(text(adapter.getString(modifier.apply("plural"), "Dollars")))
                    .symbol(text(adapter.getString(modifier.apply("symbol"), "$")))
                    .decimals(adapter.getInteger(modifier.apply("decimals"), 2))
                    .starting(BigDecimal.valueOf(adapter.getDouble(modifier.apply("default-balance"), 500)))
                    .boundaries(BigDecimal.valueOf(adapter.getDouble(modifier.apply("boundaries.minimum-balance"), 0)), BigDecimal.valueOf(adapter.getDouble(modifier.apply("boundaries.maximum-balance"), 1_000_000_000_000.0)))
                    .build());
        }

        return currencies;
    });

    // -----------------------------------------------------------------------------------------------------------------
    //
    // Storage Configuration
    //
    // -----------------------------------------------------------------------------------------------------------------

    public static final ConfigKey<StorageType> STORAGE_METHOD = notReloadable(key(adapter -> {
        throw new UnsupportedOperationException("Awaiting storage implementation");
    }));

    public static final ConfigKey<StorageCredentials> STORAGE_CREDENTIALS = notReloadable(key(adapter -> {
        String address = adapter.getString("storage.data.address", "localhost");
        String database = adapter.getString("storage.data.database", "minecraft");
        String username = adapter.getString("storage.data.username", "root");
        String password = adapter.getString("storage.data.password", "");

        int maxPoolSize = adapter.getInteger("storage.data.pool-settings.maximum-pool-size", 10);
        int minIdle = adapter.getInteger("storage.data.pool-settings.minimum-idle", maxPoolSize);
        int maxLifetime = adapter.getInteger("storage.data.pool-settings.maximum-lifetime", 1800000);
        int connectionTimeout = adapter.getInteger("storage.data.pool-settings.connection-timeout", 5000);
        int keepAliveTime = adapter.getInteger("storage.data.pool-settings.keep-alive", 0);
        Map<String, String> props = ImmutableMap.copyOf(adapter.getStringMap("storage.data.pool-settings.properties", ImmutableMap.of()));
        return new StorageCredentials(address, database, username, password, maxPoolSize, minIdle, maxLifetime, keepAliveTime, connectionTimeout, props);
    }));

    // -----------------------------------------------------------------------------------------------------------------
    //
    // Networking Configuration
    //
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Specifies the communication layer to be used by the networking service.
     *
     * @since 6.0.0
     */
    public static final ConfigKey<String> NETWORKING_METHOD = stringKey("networking.method", "none");

    /**
     * Specifies if the instance handling the built-in economy should act as the networking processor for incoming
     * requests. The processor is responsible for evaluating requests and determining the ultimate response per
     * request. It must also ensure that a request can be performed, and must do so in a way that ensures two
     * requests that might come across at the same time apply in the order they came in.
     *
     * @since 6.0.0
     */
    public static final ConfigKey<Boolean> NETWORKING_PROCESSOR = booleanKey("networking.processor", false);

    /**
     * Specifies connection settings to the redis networking service. These will only be used if the chosen
     * {@link #NETWORKING_METHOD} is set to use redis.
     *
     * @since 6.0.0
     */
    public static final ConfigKey<RedisConfig> REDIS = notReloadable(key(adapter -> new RedisConfig(adapter.getStringList("messaging.redis.addresses", ImmutableList.of()), adapter.getString("messaging.redis.username", "default"), adapter.getString("messaging.redis.password", ""), adapter.getBoolean("messaging.redis.ssl", false))));

}
