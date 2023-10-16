package net.impactdev.impactor.core.mail.storage;

import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import net.impactdev.impactor.api.storage.StorageType;
import net.impactdev.impactor.api.storage.connection.configurate.loaders.HoconLoader;
import net.impactdev.impactor.api.storage.connection.configurate.loaders.JsonLoader;
import net.impactdev.impactor.api.storage.connection.configurate.loaders.YamlLoader;
import net.impactdev.impactor.core.mail.storage.implementations.MailConfigurateProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class MailStorageFactory {

    public static MailStorage instance(ImpactorPlugin plugin, @Nullable StorageType type, @NotNull StorageType fallback) {
        StorageType use = Optional.ofNullable(type).orElse(fallback);
        plugin.logger().info("Loading storage provider... [" + use.getName() + "]");
        return new MailStorage(createNewImplementation(use));
    }

    private static MailStorageImplementation createNewImplementation(StorageType type) {
        switch (type) {
            case JSON -> {
                return new MailConfigurateProvider(new JsonLoader());
            }
            case YAML -> {
                return new MailConfigurateProvider(new YamlLoader());
            }
            case HOCON -> {
                return new MailConfigurateProvider(new HoconLoader());
            }
        }

        throw new IllegalArgumentException("Unsupported storage type: " + type);
    }

}
