package com.nickimpact.impactor.api.storage.connections.sql.file;

import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;

import java.sql.Connection;
import java.sql.SQLException;

@AllArgsConstructor
public final class NonClosableConnection implements Connection {

	@Delegate(excludes = Exclude.class)
	private Connection delegate;

	@Override
	public void close() throws SQLException {}

	private interface Exclude {
		void close() throws SQLException;
	}
}