package net.impactdev.impactor.core.storage.sql;

import net.impactdev.impactor.api.storage.StorageCredentials;
import net.impactdev.impactor.api.storage.connection.sql.hikari.MariaDBConnection;
import net.impactdev.impactor.core.storage.hikari.DriverBasedHikariConnection;

import java.util.function.Function;

public class MariaDbConnectionImpl extends DriverBasedHikariConnection implements MariaDBConnection {
    public MariaDbConnectionImpl(StorageCredentials credentials) {
        super(credentials);
    }

    @Override
    public String name() {
        return "MariaDB";
    }

    @Override
    public Function<String, String> statementProcessor() {
        return s -> s.replace('\'', '`'); // use backticks for quotes
    }

    @Override
    protected String driverClassName() {
        return "org.mariadb.jdbc.Driver";
    }

    @Override
    protected String driverJdbcIdentifier() {
        return "mariadb";
    }

    @Override
    protected String defaultPort() {
        return "3306";
    }
}
