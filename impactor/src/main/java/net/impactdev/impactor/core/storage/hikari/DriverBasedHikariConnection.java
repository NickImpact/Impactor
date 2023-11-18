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
