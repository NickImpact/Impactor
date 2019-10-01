package com.nickimpact.impactor.api.storage.sql.executors.options;

import java.sql.ResultSet;

@FunctionalInterface
public interface SQLResults<T> {
	T results(ResultSet rs) throws Exception;
}
