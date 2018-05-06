package com.nickimpact.impactor;

import com.nickimpact.impactor.api.configuration.AbstractConfig;
import com.nickimpact.impactor.api.configuration.AbstractConfigAdapter;
import com.nickimpact.impactor.api.configuration.ConfigBase;
import com.nickimpact.impactor.api.logger.Logger;
import com.nickimpact.impactor.commands.PluginCmd;
import com.nickimpact.impactor.api.plugins.ConfigurableSpongePlugin;
import com.nickimpact.impactor.api.plugins.PluginInfo;
import com.nickimpact.impactor.configuration.ConfigKeys;
import com.nickimpact.impactor.logging.ConsoleLogger;
import com.nickimpact.impactor.logging.SpongeLogger;
import com.nickimpact.impactor.mojang.StatusCheck;
import lombok.Getter;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

import javax.inject.Inject;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

/**
 * (Some note will appear here)
 *
 * @author NickImpact (Nick DeGruccio)
 */
@Getter
@Plugin(id = CoreInfo.ID, name = CoreInfo.NAME, version = CoreInfo.VERSION, description = CoreInfo.DESCRIPTION)
public class ImpactorCore extends ConfigurableSpongePlugin {

	@Getter private static ImpactorCore instance;

	@Inject private PluginContainer container;

	@Inject @ConfigDir(sharedRoot = false) private Path configDir;

	private ConfigBase config;

	@Inject private org.slf4j.Logger fallback;

	private Logger logger;

	@Override
	public PluginInfo getPluginInfo() {
		return new CoreInfo();
	}

	@Listener
	public void onPreInit(GamePreInitializationEvent e) {
		instance = this;
		this.logger = new ConsoleLogger(this, new SpongeLogger(this, fallback));
		this.logger.info(Text.of("Impactor is now initializing..."));
		connect();
	}

	@Listener
	public void onDisconnect(GameStoppingServerEvent e) {
		disconnect();
	}

	@Override
	public void doConnect() {
		this.logger.info(Text.of("Loading configuration..."));
		this.config = new AbstractConfig(this, new AbstractConfigAdapter(this), new ConfigKeys(), "core.conf");
		this.config.init();

		this.logger.info(Text.of("Registering core commands..."));
		new PluginCmd(this).register(this);
		this.logger.info(Text.of("Initialization complete!"));

		final StatusCheck statusChecker = new StatusCheck();
		Sponge.getScheduler().createTaskBuilder().execute(() -> {
			if(!statusChecker.allGreen()) {
				try {
					statusChecker.fetch().report();
				} catch (Exception e) {
					this.getLogger().error("Unable to read Mojang Service Status...");
					e.printStackTrace();
				}
			}
		}).async().interval(60, TimeUnit.SECONDS).submit(this);
	}

	@Override
	public void doDisconnect() {

	}

	@Override
	public Logger getLogger() {
		return this.logger;
	}
}
