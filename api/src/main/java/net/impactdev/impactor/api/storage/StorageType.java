package net.impactdev.impactor.api.storage;

import com.google.common.collect.ImmutableList;

import java.util.List;

public enum StorageType {

	JSON("JSON", "json", "flatfile"),
	YAML("YAML", "yaml", "yml"),
	HOCON("HOCON", "hocon"),
	MONGODB("MongoDB", "mongodb"),
	MARIADB("MariaDB", "mariadb"),
	MYSQL("MySQL", "mysql"),
	POSTGRESQL("PostgreSQL", "postgresql"),
	SQLITE("SQLite", "sqlite"),
	H2("H2", "h2");

	private final String name;

	private final List<String> identifiers;

	StorageType(String name, String... identifiers) {
		this.name = name;
		this.identifiers = ImmutableList.copyOf(identifiers);
	}

	public static StorageType parse(String name) {
		for (StorageType t : values()) {
			for (String id : t.getIdentifiers()) {
				if (id.equalsIgnoreCase(name)) {
					return t;
				}
			}
		}
		return null;
	}

	public String getName() {
		return this.name;
	}

	public List<String> getIdentifiers() {
		return this.identifiers;
	}
}
