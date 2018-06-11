package com.nickimpact.impactor.api.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StorageCredentials {

	private final String address;

	private final String database;

	private final String username;

	private final String password;
}
