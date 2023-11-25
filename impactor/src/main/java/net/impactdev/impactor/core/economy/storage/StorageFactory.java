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

package net.impactdev.impactor.core.economy.storage;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.configuration.Config;
import net.impactdev.impactor.api.economy.EconomyService;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import net.impactdev.impactor.api.storage.StorageType;
import net.impactdev.impactor.api.storage.connection.configurate.loaders.HoconLoader;
import net.impactdev.impactor.api.storage.connection.configurate.loaders.JsonLoader;
import net.impactdev.impactor.api.storage.connection.configurate.loaders.YamlLoader;
import net.impactdev.impactor.core.economy.EconomyConfig;
import net.impactdev.impactor.core.economy.storage.implementations.ConfigurateProvider;
import net.impactdev.impactor.core.economy.storage.implementations.SQLProvider;
import net.impactdev.impactor.core.storage.sql.MariaDbConnectionImpl;
import net.impactdev.impactor.core.storage.sql.MySQLConnectionImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class StorageFactory {

    public static EconomyStorage instance(ImpactorPlugin plugin, @NotNull Config config, @NotNull StorageType fallback) {
        StorageType use = Optional.ofNullable(config.get(EconomyConfig.STORAGE_TYPE)).orElse(fallback);
        plugin.logger().info("Loading storage provider... [" + use.getName() + "]");
        return new EconomyStorage(createNewImplementation(use, config));
    }

    private static EconomyStorageImplementation createNewImplementation(StorageType type, Config config) {
        switch (type) {
            case JSON:
                return new ConfigurateProvider(new JsonLoader());
            case YAML:
                return new ConfigurateProvider(new YamlLoader());
            case HOCON:
                return new ConfigurateProvider(new HoconLoader());
            case MYSQL:
                return new SQLProvider(
                        new MySQLConnectionImpl(config.get(EconomyConfig.STORAGE_CREDENTIALS)),
                        config.get(EconomyConfig.SQL_TABLE_PREFIX)
                );
            case MARIADB:
                return new SQLProvider(
                        new MariaDbConnectionImpl(config.get(EconomyConfig.STORAGE_CREDENTIALS)),
                        config.get(EconomyConfig.SQL_TABLE_PREFIX)
                );
        }

        throw new IllegalArgumentException("Unsupported storage type: " + type);
    }
}
