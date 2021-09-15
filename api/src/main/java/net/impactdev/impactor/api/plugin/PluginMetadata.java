package net.impactdev.impactor.api.plugin;

import net.impactdev.impactor.api.utilities.Builder;

public final class PluginMetadata {

	private final String id;
	private final String name;
	private final String version;
	private final String description;

	PluginMetadata(String id, String name, String version, String description) {
		this.id = id;
		this.name = name;
		this.version = version;
		this.description = description;
	}

	public String getID() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public String getDescription() {
		return description;
	}

	public static PluginMetadataBuilder builder() {
		return new PluginMetadataBuilder();
	}

	public static class PluginMetadataBuilder implements Builder<PluginMetadata, PluginMetadataBuilder> {
		private String id;
		private String name;
		private String version;
		private String description;

		public PluginMetadataBuilder id(String id) {
			this.id = id;
			return this;
		}

		public PluginMetadataBuilder name(String name) {
			this.name = name;
			return this;
		}

		public PluginMetadataBuilder version(String version) {
			this.version = version;
			return this;
		}

		public PluginMetadataBuilder description(String description) {
			this.description = description;
			return this;
		}

		@Override
		public PluginMetadataBuilder from(PluginMetadata input) {
			this.id = input.id;
			this.name = input.name;
			this.version = input.version;
			this.description = input.description;

			return this;
		}

		public PluginMetadata build() {
			return new PluginMetadata(id, name, version, description);
		}

	}
}
