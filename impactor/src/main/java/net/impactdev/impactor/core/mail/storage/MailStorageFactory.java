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
