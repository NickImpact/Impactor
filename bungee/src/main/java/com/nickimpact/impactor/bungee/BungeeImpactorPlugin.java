package com.nickimpact.impactor.bungee;

import co.aikar.commands.BaseCommand;
import com.nickimpact.impactor.api.configuration.Config;
import com.nickimpact.impactor.api.logging.Logger;
import com.nickimpact.impactor.api.platform.Platform;
import com.nickimpact.impactor.api.plugin.ImpactorPlugin;
import com.nickimpact.impactor.api.plugin.PluginInfo;
import com.nickimpact.impactor.bungee.logging.BungeeLogger;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.List;
import java.util.function.Consumer;

public class BungeeImpactorPlugin extends Plugin implements ImpactorPlugin {

	private Logger logger;

	@Override
	public void onLoad() {
		this.logger = new BungeeLogger(this.getLogger());
	}

	@Override
	public Platform getPlatform() {
		return null;
	}

	@Override
	public PluginInfo getPluginInfo() {
		return null;
	}

	@Override
	public Logger getPluginLogger() {
		return this.logger;
	}

	@Override
	public List<Config> getConfigs() {
		return null;
	}

	@Override
	public List<BaseCommand> getCommands() {
		return null;
	}

	@Override
	public List<Object> getListeners() {
		return null;
	}

	@Override
	public Consumer<ImpactorPlugin> onReload() {
		return null;
	}

	@Override
	public boolean isConnected() {
		return false;
	}

	@Override
	public void setConnected() {

	}

	@Override
	public void handleDisconnect() {

	}
}
