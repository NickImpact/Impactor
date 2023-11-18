package net.impactdev.impactor.core.storage.sql;

import net.impactdev.impactor.api.storage.StorageCredentials;
import net.impactdev.impactor.api.storage.connection.sql.hikari.MySQLConnection;
import net.impactdev.impactor.core.storage.hikari.DriverBasedHikariConnection;

import java.util.Map;
import java.util.function.Function;

public final class MySQLConnectionImpl extends DriverBasedHikariConnection implements MySQLConnection {

    public MySQLConnectionImpl(StorageCredentials credentials) {
        super(credentials);
    }

    @Override
    public String name() {
        return "MySQL";
    }

    @Override
    protected String defaultPort() {
        return "3306";
    }

    @Override
    protected String driverClassName() {
        return "com.mysql.cj.jdbc.Driver";
    }

    @Override
    protected String driverJdbcIdentifier() {
        return "mysql";
    }

    @Override
    protected void overrideProperties(Map<String, Object> properties) {
        // https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
        properties.putIfAbsent("cachePrepStmts", "true");
        properties.putIfAbsent("prepStmtCacheSize", "250");
        properties.putIfAbsent("prepStmtCacheSqlLimit", "2048");
        properties.putIfAbsent("useServerPrepStmts", "true");
        properties.putIfAbsent("useLocalSessionState", "true");
        properties.putIfAbsent("rewriteBatchedStatements", "true");
        properties.putIfAbsent("cacheResultSetMetadata", "true");
        properties.putIfAbsent("cacheServerConfiguration", "true");
        properties.putIfAbsent("elideSetAutoCommits", "true");
        properties.putIfAbsent("maintainTimeStats", "false");
        properties.putIfAbsent("alwaysSendSetIsolation", "false");
        properties.putIfAbsent("cacheCallableStmts", "true");

        // https://stackoverflow.com/a/54256150
        properties.putIfAbsent("serverTimezone", "UTC");

        super.overrideProperties(properties);
    }

    @Override
    public Function<String, String> statementProcessor() {
        return s -> s.replace('\'', '`'); // use backticks for quotes
    }
}
