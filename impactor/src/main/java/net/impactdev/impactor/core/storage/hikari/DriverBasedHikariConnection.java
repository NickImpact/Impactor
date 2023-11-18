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

package net.impactdev.impactor.core.storage.hikari;

import com.zaxxer.hikari.HikariConfig;
import net.impactdev.impactor.api.storage.StorageCredentials;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

/**
 * Based on code from LuckPerms, licensed under MIT. For more info,
 * <a href="https://github.com/LuckPerms/LuckPerms/blob/master/common/src/main/java/me/lucko/luckperms/common/storage/implementation/sql/connection/hikari/DriverBasedHikariConnectionFactory.java">
 * click here
 * </a>!
 */
public abstract class DriverBasedHikariConnection extends HikariConnection {

    public DriverBasedHikariConnection(StorageCredentials credentials) {
        super(credentials);
    }

    protected abstract String driverClassName();

    protected abstract String driverJdbcIdentifier();

    @Override
    protected void configure(HikariConfig config, StorageCredentials credentials, StorageConfiguration configuration) {
        config.setDriverClassName(this.driverClassName());
        config.setJdbcUrl(String.format("jdbc:%s://%s:%s/%s", this.driverJdbcIdentifier(), configuration.address(), configuration.port(), credentials.getDatabase()));
        config.setUsername(credentials.getUsername());
        config.setPassword(credentials.getPassword());
    }

    @Override
    protected void postInitialize() {
        super.postInitialize();

        // Calling Class.forName("<driver class name>") is enough to call the static initializer
        // which makes our driver available in DriverManager. We don't want that, so unregister it after
        // the pool has been setup.
        deregisterDriver(this.driverClassName());
    }

    private static void deregisterDriver(String driverClassName) {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            if (driver.getClass().getName().equals(driverClassName)) {
                try {
                    DriverManager.deregisterDriver(driver);
                } catch (SQLException e) {
                    // ignore
                }
            }
        }
    }
}
