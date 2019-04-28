package com.nickimpact.impactor.api.plugin;

import com.nickimpact.impactor.api.commands.Command;
import com.nickimpact.impactor.api.configuration.Config;
import com.nickimpact.impactor.api.logging.Logger;
import com.nickimpact.impactor.api.platform.Platform;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public interface ImpactorPlugin {

	Platform getPlatform();

	PluginInfo getPluginInfo();

	Logger getPluginLogger();

	List<Config> getConfigs();

	List<Command> getCommands();

	List<Object> getListeners();

	Consumer<ImpactorPlugin> onReload();

	boolean isConnected();

	void setConnected();

	void handleDisconnect();

	/**
	 * Attempts to connect the plugin to the core itself. This method is typically
	 * called via any starting phase in Sponge.
	 */
	default void connect() {
		if (!PluginRegistry.isLoaded(this.getPluginInfo().getID())) {
			PluginRegistry.register(this);
			this.setConnected();
		} else {
			if (!isConnected()) {
				this.setConnected();
			}
		}
	}

	default void disconnect() {
		if(PluginRegistry.isLoaded(this.getPluginInfo().getID())) {
			PluginRegistry.unregister(this);
			this.handleDisconnect();
		}
	}
}
