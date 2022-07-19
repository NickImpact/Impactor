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

package net.impactdev.impactor.common.storage;

import net.impactdev.impactor.api.module.Module;
import net.impactdev.impactor.api.registry.Registry;
import net.impactdev.impactor.api.storage.connection.configurate.HoconLoader;
import net.impactdev.impactor.api.storage.connection.configurate.JsonLoader;
import net.impactdev.impactor.api.storage.connection.configurate.YamlLoader;
import net.impactdev.impactor.api.storage.connection.sql.file.H2Connection;
import net.impactdev.impactor.api.storage.connection.sql.hikari.MariaDBConnection;
import net.impactdev.impactor.api.storage.connection.sql.hikari.MySQLConnection;
import net.impactdev.impactor.common.api.ModuleImplementation;
import net.impactdev.impactor.common.storage.configurate.HoconLoaderImpl;
import net.impactdev.impactor.common.storage.configurate.JsonLoaderImpl;
import net.impactdev.impactor.common.storage.configurate.YamlLoaderImpl;
import net.impactdev.impactor.common.storage.sql.file.H2ConnectionImpl;
import net.impactdev.impactor.common.storage.sql.hikari.MariaDBConnectionImpl;
import net.impactdev.impactor.common.storage.sql.hikari.MySQLConnectionImpl;

@ModuleImplementation
public class StorageModule implements Module {

    @Override
    public String name() {
        return "Storage";
    }

    @Override
    public void register(Registry registry) {}

    @Override
    public void builders(Registry registry) {
        registry.registerBuilderSupplier(H2Connection.H2ConnectionBuilder.class, H2ConnectionImpl.H2ConnectionBuilderImpl::new);
        registry.registerBuilderSupplier(MariaDBConnection.MariaDBConnectionBuilder.class, MariaDBConnectionImpl.MariaDBConnectionBuilderImpl::new);
        registry.registerBuilderSupplier(MySQLConnection.MySQLConnectionBuilder.class, MySQLConnectionImpl.MySQLConnectionBuilderImpl::new);
        registry.registerBuilderSupplier(YamlLoader.YamlLoaderBuilder.class, YamlLoaderImpl.YamlLoaderBuilderImpl::new);
        registry.registerBuilderSupplier(JsonLoader.JsonLoaderBuilder.class, JsonLoaderImpl.JsonLoaderBuilderImpl::new);
        registry.registerBuilderSupplier(HoconLoader.HoconLoaderBuilder.class, HoconLoaderImpl.HoconLoaderBuilderImpl::new);

    }
}
