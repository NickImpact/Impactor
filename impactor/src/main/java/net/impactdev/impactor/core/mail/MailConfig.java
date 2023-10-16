package net.impactdev.impactor.core.mail;

import net.impactdev.impactor.api.configuration.key.ConfigKey;
import net.impactdev.impactor.api.storage.StorageType;

import static net.impactdev.impactor.api.configuration.key.ConfigKeyFactory.key;

public final class MailConfig {

    public static final ConfigKey<StorageType> STORAGE_TYPE = key(adapter ->
            StorageType.parse(adapter.getString("storage-method", "json"))
    );

}
