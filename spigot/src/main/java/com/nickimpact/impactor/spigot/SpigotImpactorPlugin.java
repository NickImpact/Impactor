package com.nickimpact.impactor.spigot;

import com.google.common.collect.Lists;
import com.nickimpact.impactor.api.ImpactorService;
import com.nickimpact.impactor.api.commands.Command;
import com.nickimpact.impactor.api.configuration.Config;
import com.nickimpact.impactor.api.logging.Logger;
import com.nickimpact.impactor.api.platform.Platform;
import com.nickimpact.impactor.api.plugin.ImpactorPlugin;
import com.nickimpact.impactor.api.plugin.PluginInfo;
import com.nickimpact.impactor.spigot.logging.SpigotLogger;
import com.nickimpact.impactor.spigot.services.SpigotMojangServerStatusService;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.function.Consumer;

public class SpigotImpactorPlugin extends JavaPlugin implements ImpactorPlugin {

	@Getter private static SpigotImpactorPlugin instance;

	private boolean connected = false;

	private SpigotImpactorInfo info = new SpigotImpactorInfo();

	private SpigotLogger logger;

	@Getter private SpigotMojangServerStatusService mojangServerStatusService;

	public SpigotImpactorPlugin() {
		this.connect();
	}

	@Override
	public void onEnable() {
		instance = this;
		this.logger = new SpigotLogger(this);
		new ImpactorService();
		this.mojangServerStatusService = new SpigotMojangServerStatusService();
	}

	@Override
	public void onDisable() {
		this.disconnect();

	}

	@Override
	public Platform getPlatform() {
		return Platform.Spigot;
	}

	@Override
	public PluginInfo getPluginInfo() {
		return info;
	}

	@Override
	public Logger getPluginLogger() {
		return this.logger;
	}

	@Override
	public List<Config> getConfigs() {
		return Lists.newArrayList();
	}

	@Override
	public List<Command> getCommands() {
		return Lists.newArrayList();
	}

	@Override
	public List<Object> getListeners() {
		return Lists.newArrayList();
	}

	@Override
	public Consumer<ImpactorPlugin> onReload() {
		return plugin -> {};
	}

	@Override
	public boolean isConnected() {
		return this.connected;
	}

	@Override
	public void setConnected() {
		this.connected = true;
	}

	@Override
	public void handleDisconnect() {
		this.connected = false;
	}

	@Override
	public void connect() {

	}

	@Override
	public void disconnect() {

	}
}
