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

package net.impactdev.impactor.storage.provider.sql.hikari;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.impactdev.impactor.api.plugin.PluginMetadata;
import net.impactdev.impactor.api.storage.StorageCredentials;
import net.impactdev.impactor.api.storage.connection.sql.SQLConnection;
import net.impactdev.impactor.api.utilities.printing.PrettyPrinter;
import net.kyori.adventure.text.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class HikariConnectionFactory implements SQLConnection {

    protected final PluginMetadata metadata;
    protected final StorageCredentials configuration;
    private HikariDataSource hikari;

    public HikariConnectionFactory(PluginMetadata metadata, StorageCredentials configuration) {
        this.metadata = metadata;
        this.configuration = configuration;
    }

    protected abstract String defaultPort();

    protected abstract void configure(HikariConfig config, String address, String port, String database, String user, String password);

    protected void override(Map<String, String> properties) {
        properties.putIfAbsent("socketTimeout", String.valueOf(TimeUnit.SECONDS.toMillis(30)));
    }

    protected void apply(HikariConfig config, Map<String, String> properties) {
        for (Map.Entry<String, String> property : properties.entrySet()) {
            config.addDataSourceProperty(property.getKey(), property.getValue());
        }
    }

    @Override
    public void init() {
        HikariConfig config = new HikariConfig();
        config.setPoolName(this.metadata.name().orElse(this.metadata.id()) + "-hikari");

        String[] addressFull = this.configuration.getAddress().split(":");
        String address = addressFull[0];
        String port = addressFull.length > 1 ? addressFull[1] : this.defaultPort();

        this.configure(config, address, port, this.configuration.getDatabase(), this.configuration.getUsername(), this.configuration.getPassword());
        Map<String, String> properties = new HashMap<>(this.configuration.getProperties());
        this.override(properties);
        this.apply(config, properties);

        config.setMaximumPoolSize(this.configuration.getMaxPoolSize());
        config.setMinimumIdle(this.configuration.getMinIdleConnections());
        config.setMaxLifetime(this.configuration.getMaxLifetime());
        config.setKeepaliveTime(this.configuration.getKeepAliveTime());
        config.setConnectionTimeout(this.configuration.getConnectionTimeout());
        config.setInitializationFailTimeout(-1);

        this.hikari = new HikariDataSource(config);
    }

    @Override
    public void shutdown() throws Exception {
        if(this.hikari != null) {
            this.hikari.close();
        }
    }

//    @Override
//    public Map<Component, Component> meta() {
//        Map<Component, Component> meta = new LinkedHashMap<>();
//        boolean success = true;
//
//        long start = System.currentTimeMillis();
//        try (Connection c = connection()) {
//            try (Statement s = c.createStatement()) {
//                s.execute("/* ping */ SELECT 1");
//            }
//        } catch (SQLException e) {
//            success = false;
//        }
//
//        if (success) {
//            long duration = System.currentTimeMillis() - start;
////            meta.put(
////                    Component.translatable("luckperms.command.info.storage.meta.ping-key"),
////                    Component.text(duration + "ms", NamedTextColor.GREEN)
////            );
//        }
////        meta.put(
////                Component.translatable("luckperms.command.info.storage.meta.connected-key"),
////                Message.formatBoolean(success)
////        );
//
//        return meta;
//    }

    @Override
    public PrettyPrinter.IPrettyPrintable meta() {
        return null;
    }

    @Override
    public Connection connection() throws SQLException {
        if (this.hikari == null) {
            throw new SQLException("Unable to get a connection from the pool. (hikari is null)");
        }

        Connection connection = this.hikari.getConnection();
        if (connection == null) {
            throw new SQLException("Unable to get a connection from the pool. (getConnection returned null)");
        }

        return connection;
    }
}
