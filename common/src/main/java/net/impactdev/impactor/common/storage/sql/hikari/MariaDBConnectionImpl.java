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
import net.impactdev.impactor.api.storage.StorageCredentials;
import net.impactdev.impactor.api.storage.connection.sql.hikari.MariaDBConnection;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MariaDBConnectionImpl extends HikariConnection implements MariaDBConnection {

    private MariaDBConnectionImpl(MariaDBConnectionBuilderImpl builder) {
        super(builder.credentials);
    }

    @Override
    public String name() {
        return "MariaDB";
    }

    @Override
    protected String defaultPort() {
        return "3306";
    }

    @Override
    protected void configureDatabase(HikariConfig config, String address, String port, String databaseName, String username, String password) {
        config.setDataSourceClassName("org.mariadb.jdbc.MariaDbDataSource");
        config.addDataSourceProperty("serverName", address);
        config.addDataSourceProperty("port", port);
        config.addDataSourceProperty("databaseName", databaseName);
        config.setUsername(username);
        config.setPassword(password);
    }

    @Override
    protected void setProperties(HikariConfig config, Map<String, String> properties) {
        String propertiesString = properties.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining(";"));

        // kinda hacky. this will call #setProperties on the datasource, which will append these options
        // onto the connections.
        config.addDataSourceProperty("properties", propertiesString);
    }

    @Override
    public Function<String, String> getStatementProcessor() {
        return s -> s.replace('\'', '`'); // use backticks for quotes
    }

    public static class MariaDBConnectionBuilderImpl implements MariaDBConnectionBuilder {

        private StorageCredentials credentials;

        @Override
        public MariaDBConnectionBuilder credentials(StorageCredentials credentials) {
            this.credentials = credentials;
            return this;
        }

        @Override
        public MariaDBConnection build() {
            return new MariaDBConnectionImpl(this);
        }

    }
}
