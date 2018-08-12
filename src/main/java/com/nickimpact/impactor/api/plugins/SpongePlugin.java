package com.nickimpact.impactor.api.plugins;

import com.google.common.collect.Maps;
import com.nickimpact.impactor.api.configuration.ConfigBase;
import lombok.Getter;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;

import java.util.Map;

@Getter
public abstract class SpongePlugin implements IPlugin {

	private boolean connected;

	private Map<String, Boolean> dependencies = Maps.newHashMap();

	public SpongePlugin() {
		this.connect();
	}

	/**
	 * Attempts to connect the plugin to the core itself. This method is typically
	 * called via any starting phase in Sponge.
	 */
	public void connect() {
		if (!PluginRegistry.isLoaded(this.getClass().getAnnotation(Plugin.class).id())) {
			connected = true;
			PluginRegistry.register(this);
		} else {
			if (!isConnected()) {
				connected = true;
			}
		}
	}

	/**
	 * Disables all plugin tasks, listeners, and commands currently registered to the inheriting plugin.
	 */
	public void disconnect() {
		Sponge.getCommandManager().getOwnedBy(this).forEach(Sponge.getCommandManager()::removeMapping);
		Sponge.getEventManager().unregisterPluginListeners(this);
		Sponge.getScheduler().getScheduledTasks(this).forEach(Task::cancel);
		this.connected = false;
	}

	@Override
	public void reload() {
		this.disconnect();
		this.connect();
		this.getConfigs().forEach(ConfigBase::reload);
		this.getCommands().forEach(cmd -> cmd.register(this));
		this.getListeners().forEach(listener -> Sponge.getEventManager().registerListeners(this, listener));
	}
}
