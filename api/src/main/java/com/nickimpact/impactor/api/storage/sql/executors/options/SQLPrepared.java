package com.nickimpact.impactor.api.storage.sql.executors.options;

import java.sql.Connection;
import java.sql.PreparedStatement;

@FunctionalInterface
public interface SQLPrepared<T> {
	T prepare(Connection connection, PreparedStatement ps) throws Exception;
}