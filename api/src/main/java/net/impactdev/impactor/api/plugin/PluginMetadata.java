package net.impactdev.impactor.api.plugin;

import lombok.Builder;

@Builder
public final class PluginMetadata {

	private final String id;
	private final String name;
	private final String version;
	private final String description;

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

}
