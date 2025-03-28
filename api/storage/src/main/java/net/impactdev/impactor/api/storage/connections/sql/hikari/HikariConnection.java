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

package net.impactdev.impactor.api.storage.connections.sql.hikari;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.impactdev.impactor.api.storage.connections.StorageCredentials;
import net.impactdev.impactor.api.storage.connections.sql.SqlConnection;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class HikariConnection implements SqlConnection {

    private final StorageCredentials credentials;

    @MonotonicNonNull
    private HikariDataSource hikari;

    protected HikariConnection(final StorageCredentials credentials) {
        this.credentials = credentials;
    }

    /**
     * Gets the default port used by the database
     *
     * @return the default port
     */
    protected abstract String port();

    /**
     * Configures the {@link HikariConfig} with the relevant database properties.
     *
     * <p>Each driver does this slightly differently...</p>
     *
     * @param config the hikari config
     * @param address the database address
     * @param port the database port
     * @param database the database name
     * @param username the database username
     * @param password the database password
     */
    protected abstract void configure(HikariConfig config, String address, String port, String database, String username, String password);

    /**
     * Allows the connection factory instance to override certain properties before they are set.
     *
     * @param properties the current properties
     */
    protected void overrideProperties(Map<String, Object> properties) {
        properties.putIfAbsent("socketTimeout", String.valueOf(TimeUnit.SECONDS.toMillis(30)));
    }

    /**
     * Sets the given connection properties onto the config.
     *
     * @param config the hikari config
     * @param properties the properties
     */
    protected void setProperties(HikariConfig config, Map<String, Object> properties) {
        for (Map.Entry<String, Object> property : properties.entrySet()) {
            config.addDataSourceProperty(property.getKey(), property.getValue());
        }
    }

    /**
     * Called after the Hikari pool has been initialised
     */
    protected void postInitialize() { }

    @Override
    public void init() throws Exception {
        HikariConfig config = new HikariConfig();

        // set pool name so the logging output can be linked back to us
        config.setPoolName("Impactor (Hikari)");

        // get the database info/credentials from the config file
        String[] addressSplit = this.credentials.address().split(":");
        String address = addressSplit[0];
        String port = addressSplit.length > 1 ? addressSplit[1] : port();

        // allow the implementation to configure the HikariConfig appropriately with these values
        this.configure(config, address, port, this.credentials.database(), this.credentials.username(), this.credentials.password());

        // get the extra connection properties from the config
        Map<String, Object> properties = new HashMap<>(this.credentials.properties());

        // allow the implementation to override/make changes to these properties
        overrideProperties(properties);

        // set the properties
        setProperties(config, properties);

        // configure the connection pool
        config.setMaximumPoolSize(this.credentials.maxPoolSize());
        config.setMinimumIdle(this.credentials.minIdleConnections());
        config.setMaxLifetime(this.credentials.maxLifetime());
        config.setKeepaliveTime(this.credentials.keepAliveTime());
        config.setConnectionTimeout(this.credentials.connectionTimeout());

        // don't perform any initial connection validation - we subsequently call #getConnection
        // to setup the schema anyways
        config.setInitializationFailTimeout(-1);

        this.hikari = new HikariDataSource(config);
        postInitialize();
    }

    @Override
    public void shutdown() throws Exception {
        if(this.hikari != null) {
            this.hikari.close();
        }
    }

    @Override
    public Connection connection() throws SQLException {
        if(this.hikari == null) {
            throw new SQLException("Unable to get a connection from the pool (hikari is null)");
        }

        Connection connection = this.hikari.getConnection();
        if(connection == null) {
            throw new SQLException("Unable to get a connection from the pool (connection is null)");
        }

        return connection;
    }
}
