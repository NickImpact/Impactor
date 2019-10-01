package com.nickimpact.impactor.api.storage.sql.executors;

import com.nickimpact.impactor.api.storage.sql.ConnectionFactory;
import com.nickimpact.impactor.api.storage.sql.executors.options.SQLPrepared;
import com.nickimpact.impactor.api.storage.sql.executors.options.SQLResults;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BaseOperations {

	public static <T> T query(ConnectionFactory factory, String statement, SQLPrepared<T> action) throws Exception {
		try(Connection connection = factory.getConnection()) {
			try(PreparedStatement ps = connection.prepareStatement(statement)) {
				return action.prepare(connection, ps);
			}
		}
	}

	public static <T> T results(PreparedStatement ps, SQLResults<T> action) throws Exception {
		try(ResultSet rs = ps.executeQuery()) {
			return action.results(rs);
		}
	}
}
