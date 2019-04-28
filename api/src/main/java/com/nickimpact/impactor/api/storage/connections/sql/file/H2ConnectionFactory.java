package com.nickimpact.impactor.api.storage.connections.sql.file;

import java.io.File;

public class H2ConnectionFactory extends FlatfileConnectionFactory {

	public H2ConnectionFactory(File file) {
		super("H2", file);
	}

	@Override
	protected String getDriverClass() {
		return "org.h2.Driver";
	}

	@Override
	protected String getDriverID() {
		return "jdbc:h2";
	}
}
