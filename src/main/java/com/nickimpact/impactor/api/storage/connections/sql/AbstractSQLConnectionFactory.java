package com.nickimpact.impactor.api.storage.connections.sql;

import com.nickimpact.impactor.api.storage.connections.AbstractConnectionFactory;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class AbstractSQLConnectionFactory extends AbstractConnectionFactory {

	public AbstractSQLConnectionFactory(String name) {
		super(name);
	}

	public abstract Connection getConnection() throws SQLException;

}
