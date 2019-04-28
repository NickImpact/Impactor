package com.nickimpact.impactor.api.storage.connections.sql.file;

import java.io.File;

public class SQLiteConnectionFactory extends FlatfileConnectionFactory {

	public SQLiteConnectionFactory(File file) {
		super("SQLite", file);
	}

	@Override
	protected String getDriverClass() {
		return "org.sqlite.JDBC";
	}

	@Override
	protected String getDriverID() {
		return "jdbc:sqlite";
	}
}
