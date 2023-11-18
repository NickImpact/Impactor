package net.impactdev.impactor.core.storage.hikari;

import net.impactdev.impactor.api.storage.StorageCredentials;
import net.impactdev.impactor.api.storage.connection.sql.SQLConnection;
import net.impactdev.impactor.api.storage.connection.sql.hikari.HikariConnectionBuilder;

public abstract class HikariConnectionBuilderImpl<T extends SQLConnection, B extends HikariConnectionBuilder<T, B>>
        implements HikariConnectionBuilder<T, B>
{

    protected StorageCredentials credentials;

    @SuppressWarnings("unchecked")
    @Override
    public B credentials(StorageCredentials credentials) {
        this.credentials = credentials;
        return (B) this;
    }
}
