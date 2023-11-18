package net.impactdev.impactor.core.storage.hikari;

import com.google.common.base.Preconditions;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.impactdev.impactor.api.storage.StorageCredentials;
import net.impactdev.impactor.api.storage.connection.sql.SQLConnection;
import net.impactdev.impactor.api.utility.printing.PrettyPrinter;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class HikariConnection implements SQLConnection {

    private static int index = 1;
    protected final StorageCredentials credentials;

    @MonotonicNonNull
    private HikariDataSource hikari;

    public HikariConnection(StorageCredentials credentials) {
        this.credentials = credentials;
    }

    protected abstract String defaultPort();

    protected abstract void configure(HikariConfig config, StorageCredentials credentials, StorageConfiguration configuration);

    protected void overrideProperties(Map<String, Object> properties) {
        properties.putIfAbsent("socketTimeout", String.valueOf(TimeUnit.SECONDS.toMillis(30)));
    }

    protected void setProperties(HikariConfig config, Map<String, Object> properties) {
        for (Map.Entry<String, Object> property : properties.entrySet()) {
            config.addDataSourceProperty(property.getKey(), property.getValue());
        }
    }

    protected void postInitialize() {}

    @Override
    public void init() throws Exception {
        HikariConfig config = new HikariConfig();
        config.setPoolName("Impactor (" + (index++) + ")");

        String[] addressSplit = this.credentials.getAddress().split(":");
        String address = addressSplit[0];
        String port = addressSplit.length > 1 ? addressSplit[1] : this.defaultPort();

        this.configure(config, credentials, new StorageConfiguration(address, port));
        Map<String, Object> properties = new HashMap<>(this.credentials.getProperties());
        this.overrideProperties(properties);
        this.setProperties(config, properties);

        config.setMaximumPoolSize(this.credentials.getMaxPoolSize());
        config.setMinimumIdle(this.credentials.getMinIdleConnections());
        config.setMaxLifetime(this.credentials.getMaxLifetime());
        config.setMaxLifetime(this.credentials.getKeepAliveTime());
        config.setConnectionTimeout(this.credentials.getConnectionTimeout());

        config.setInitializationFailTimeout(-1);
        this.hikari = new HikariDataSource(config);

        this.postInitialize();
    }

    @Override
    public void shutdown() throws Exception {
        if(this.hikari != null) {
            this.hikari.close();
        }
    }

    @Override
    public Connection connection() throws SQLException {
        Preconditions.checkNotNull(this.hikari);

        Connection connection = this.hikari.getConnection();
        Preconditions.checkNotNull(connection);

        return connection;
    }

    @Override
    public void meta(PrettyPrinter printer) throws Exception {
        long start = System.currentTimeMillis();
        try(Connection connection = this.connection()) {
            try(Statement s = connection.createStatement()) {
                s.execute("/* ping /* SELECT 1");
            }

            int duration = (int) (System.currentTimeMillis() - start);
            printer.add("Ping: %dms", (Number) duration);
        } catch (Exception e) {
            printer.add("Connection failed...");
        }
    }

    protected record StorageConfiguration(String address, String port) {}
}
