package com.nickimpact.impactor.api.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class StorageCredentials {

	private final String address;

	private final String database;

	private final String username;

	private final String password;

	private final int maxPoolSize;

	private final int minIdleConnections;

	private final int maxLifetime;

	private final int connectionTimeout;

	private final Map<String, String> properties;
}
