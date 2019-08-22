package com.nickimpact.impactor.sponge;

import co.aikar.commands.BaseCommand;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.nickimpact.impactor.api.ImpactorService;
import com.nickimpact.impactor.api.configuration.Config;
import com.nickimpact.impactor.api.logging.Logger;
import com.nickimpact.impactor.api.platform.Platform;
import com.nickimpact.impactor.api.plugin.ImpactorPlugin;
import com.nickimpact.impactor.api.plugin.PluginInfo;
import com.nickimpact.impactor.sponge.logging.SpongeLogger;
import com.nickimpact.impactor.sponge.services.SpongeMojangServerStatusService;
import lombok.Getter;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

import java.util.List;
import java.util.function.Consumer;

@Plugin(id = "impactor", name = "ImpactorAPI", version = "2.0.0", description = "A universal API for multiple tools for development")
public class SpongeImpactorPlugin extends AbstractSpongePlugin {

	@Getter private static SpongeImpactorPlugin instance;

	@Getter private SpongeMojangServerStatusService mojangServerStatusService;

	@Inject
	private org.slf4j.Logger fallback;

	private SpongeLogger logger;

	@Listener
	public void onInit(GameInitializationEvent e) {
		instance = this;
		new ImpactorService();
		this.logger = new SpongeLogger(this, this.fallback);
		mojangServerStatusService = new SpongeMojangServerStatusService();
	}

	@Override
	public Platform getPlatform() {
		return Platform.Sponge;
	}

	@Override
	public PluginInfo getPluginInfo() {
		return new SpongeImpactorInfo();
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
	public List<BaseCommand> getCommands() {
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
}
