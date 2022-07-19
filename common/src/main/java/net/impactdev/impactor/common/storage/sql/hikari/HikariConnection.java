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

package net.impactdev.impactor.common.storage.sql.hikari;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.impactdev.impactor.api.storage.StorageCredentials;
import net.impactdev.impactor.api.storage.connection.sql.SqlConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class HikariConnection implements SqlConnection {

    private final StorageCredentials configuration;
    private HikariDataSource hikari;

    public HikariConnection(StorageCredentials configuration) {
        this.configuration = configuration;
    }

    /**
     * Gets the default port used by the database
     *
     * @return the default port
     */
    protected abstract String defaultPort();

    /**
     * Configures the {@link HikariConfig} with the relevant database properties.
     *
     * <p>Each driver does this slightly differently...</p>
     *
     * @param config the hikari config
     * @param address the database address
     * @param port the database port
     * @param databaseName the database name
     * @param username the database username
     * @param password the database password
     */
    protected abstract void configureDatabase(HikariConfig config, String address, String port, String databaseName, String username, String password);

    /**
     * Allows the connection factory instance to override certain properties before they are set.
     *
     * @param properties the current properties
     */
    protected void overrideProperties(Map<String, String> properties) {
        // https://github.com/brettwooldridge/HikariCP/wiki/Rapid-Recovery
        properties.putIfAbsent("socketTimeout", String.valueOf(TimeUnit.SECONDS.toMillis(30)));
    }

    /**
     * Sets the given connection properties onto the config.
     *
     * @param config the hikari config
     * @param properties the properties
     */
    protected void setProperties(HikariConfig config, Map<String, String> properties) {
        for (Map.Entry<String, String> property : properties.entrySet()) {
            config.addDataSourceProperty(property.getKey(), property.getValue());
        }
    }

    /**
     * Called after the Hikari pool has been initialised
     */
    protected void postInitialize() {

    }

    @Override
    public void init() {
        HikariConfig config;
        try {
            config = new HikariConfig();
        } catch (LinkageError e) {
            //handleClassloadingError(e);
            throw e;
        }

        // set pool name so the logging output can be linked back to us
        config.setPoolName("impactor-hikari");

        // get the database info/credentials from the config file
        String[] addressSplit = this.configuration.getAddress().split(":");
        String address = addressSplit[0];
        String port = addressSplit.length > 1 ? addressSplit[1] : defaultPort();

        // allow the implementation to configure the HikariConfig appropriately with these values
        try {
            configureDatabase(config, address, port, this.configuration.getDatabase(), this.configuration.getUsername(), this.configuration.getPassword());
        } catch (NoSuchMethodError e) {
            e.printStackTrace();
            //handleClassloadingError(e);
        }

        // get the extra connection properties from the config
        Map<String, String> properties = new HashMap<>(this.configuration.getProperties());

        // allow the implementation to override/make changes to these properties
        overrideProperties(properties);

        // set the properties
        setProperties(config, properties);

        // configure the connection pool
        config.setMaximumPoolSize(this.configuration.getMaxPoolSize());
        config.setMinimumIdle(this.configuration.getMinIdleConnections());
        config.setMaxLifetime(this.configuration.getMaxLifetime());
        config.setKeepaliveTime(this.configuration.getKeepAliveTime());
        config.setConnectionTimeout(this.configuration.getConnectionTimeout());

        // don't perform any initial connection validation - we subsequently call #getConnection
        // to setup the schema anyways
        config.setInitializationFailTimeout(-1);

        this.hikari = new HikariDataSource(config);

        postInitialize();
    }

    @Override
    public void shutdown() {
        if (this.hikari != null) {
            this.hikari.close();
        }
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

    @Override
    public Map<String, String> getMeta() {
        Map<String, String> ret = new LinkedHashMap<>();
        boolean success = true;

        long start = System.currentTimeMillis();
        try (Connection c = connection()) {
            try (Statement s = c.createStatement()) {
                s.execute("/* ping */ SELECT 1");
            }
        } catch (SQLException e) {
            success = false;
        }
        long duration = System.currentTimeMillis() - start;

        if (success) {
            ret.put("Ping", duration + " ms");
            ret.put("Connected", "true");
        } else {
            ret.put("Connected", "false");
        }

        return ret;
    }

//    // dumb plugins seem to keep doing stupid stuff with shading of SLF4J and Log4J.
//    // detect this and print a more useful error message.
//    private static void handleClassloadingError(Throwable throwable) {
//        List<String> noteworthyClasses = ImmutableList.of(
//                "org.slf4j.LoggerFactory",
//                "org.slf4j.ILoggerFactory",
//                "org.apache.logging.slf4j.Log4jLoggerFactory",
//                "org.apache.logging.log4j.spi.LoggerContext",
//                "org.apache.logging.log4j.spi.AbstractLoggerAdapter",
//                "org.slf4j.impl.StaticLoggerBinder",
//                "org.slf4j.helpers.MessageFormatter"
//        );
//
//        ImpactorPlugin plugin = Impactor.getInstance().getRegistry().get("impactor", ImpactorPlugin.class);
//        PluginLogger logger = plugin.logger();
//        logger.warn("A " + throwable.getClass().getSimpleName() + " has occurred whilst initialising Hikari. This is likely due to classloading conflicts between other plugins.");
//        logger.warn("Please check for other plugins below (and try loading LuckPerms without them installed) before reporting the issue.");
//
//        for (String className : noteworthyClasses) {
//            Class<?> clazz;
//            try {
//                clazz = Class.forName(className);
//            } catch (Exception e) {
//                continue;
//            }
//
//            ClassLoader loader = clazz.getClassLoader();
//            String loaderName;
//            try {
//                loaderName = .identifyClassLoader(loader) + " (" + loader.toString() + ")";
//            } catch (Throwable e) {
//                loaderName = loader.toString();
//            }
//
//            logger.warn("Class " + className + " has been loaded by: " + loaderName);
//        }
//    }
}
