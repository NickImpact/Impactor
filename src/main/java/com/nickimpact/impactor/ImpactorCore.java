package com.nickimpact.impactor;

import com.google.common.collect.Lists;
import com.nickimpact.impactor.api.commands.SpongeCommand;
import com.nickimpact.impactor.api.configuration.AbstractConfig;
import com.nickimpact.impactor.api.configuration.AbstractConfigAdapter;
import com.nickimpact.impactor.api.configuration.ConfigBase;
import com.nickimpact.impactor.api.logger.Logger;
import com.nickimpact.impactor.api.plugins.*;
import com.nickimpact.impactor.api.services.plan.PlanData;
import com.nickimpact.impactor.api.services.plan.PlanProvider;
import com.nickimpact.impactor.api.services.plan.implementations.ProvidedDataImplementations;
import com.nickimpact.impactor.commands.PluginCmd;
import com.nickimpact.impactor.configuration.ConfigKeys;
import com.nickimpact.impactor.configuration.MsgConfigKeys;
import com.nickimpact.impactor.logging.ConsoleLogger;
import com.nickimpact.impactor.logging.SpongeLogger;
import lombok.Getter;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.game.state.*;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

import javax.inject.Inject;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * (Some note will appear here)
 *
 * @author NickImpact (Nick DeGruccio)
 */
@Getter
@Plugin(id = CoreInfo.ID, name = CoreInfo.NAME, version = CoreInfo.VERSION, description = CoreInfo.DESCRIPTION)
public class ImpactorCore extends SpongePlugin {

	@Getter private static ImpactorCore instance;

	@Inject private PluginContainer container;

	@Inject @ConfigDir(sharedRoot = false) private Path configDir;

	private ConfigBase config;
	private ConfigBase msgConfig;

	@Inject private org.slf4j.Logger fallback;

	private Logger logger;

	private PlanProvider provider;

	private PluginCmd pluginCmd;

	@Override
	public PluginInfo getPluginInfo() {
		return new CoreInfo();
	}

	@Override
	public Logger getLogger() {
		return this.logger;
	}

	@Override
	public Optional<PlanData> getPlanData() {
		return Optional.empty();
	}

	@Override
	public List<ConfigBase> getConfigs() {
		return Lists.newArrayList(config, msgConfig);
	}

	@Override
	public List<SpongeCommand> getCommands() {
		return Lists.newArrayList(pluginCmd);
	}

	@Override
	public List<Object> getListeners() {
		return Collections.emptyList();
	}

	@Listener(order = Order.EARLY)
	public void preInit(GamePreInitializationEvent e) {
		instance = this;
		this.logger = new ConsoleLogger(this, new SpongeLogger(this, fallback));
		this.getLogger().info("Starting phase: " + e.getState().name() + "...");

		this.logger.info(Text.of("Loading configuration..."));
		this.config = new AbstractConfig(this, new AbstractConfigAdapter(this), new ConfigKeys(), "core.conf");
		this.msgConfig = new AbstractConfig(this, new AbstractConfigAdapter(this), new MsgConfigKeys(), "messages.conf");
		for(ConfigBase config : this.getConfigs()) {
			config.init();
		}

		this.getLogger().info("Phase complete!");
	}

	@Listener
	public void init(GameInitializationEvent e) {
		this.getLogger().info("Starting phase: " + e.getState().name() + "...");
		this.logger.info(Text.of("Registering core commands..."));
		(pluginCmd = new PluginCmd(this)).register(this);
		this.getLogger().info("Phase complete!");
	}

	@Listener
	public void startedServer(GameStartedServerEvent e) {
		this.getLogger().info("Starting phase: " + e.getState().name() + "...");
		this.getLogger().info("Connected plugins: ");
		PluginRegistry.getConnected().forEach(plugin -> this.getLogger().info("  - " + plugin.getPluginInfo().getName()));

//		Sponge.getScheduler().createTaskBuilder().execute(() -> {
//			try {
//				StatusCheck.fetch();
//				if (!StatusCheck.allGreen()) {
//					StatusCheck.report();
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//
//		}).async().interval(30, TimeUnit.SECONDS).submit(this);
	}

	@Listener(order = Order.LAST)
	public void registerPlanData(GameStartedServerEvent e) {
		if(Sponge.getPluginManager().isLoaded("plan")) {
			this.getLogger().info("Registering data provided by supporting Plan implementations...");

			this.provider = new PlanProvider();
			for(ProvidedDataImplementations pdi : ProvidedDataImplementations.values()) {
				this.provider.register(pdi.getImplementation());
				this.getLogger().info(String.format("Data for %s loaded!", pdi.getImplementation().getSourcePlugin()));
			}
			for (SpongePlugin plugin : PluginRegistry.getConnected()) {
				plugin.getPlanData().ifPresent(data -> {
					this.provider.register(data.getData());
					this.getLogger().info(String.format("Data for %s loaded!", plugin.getPluginInfo().getName()));
				});
			}
		}

		this.getLogger().info("Phase complete!");
	}

	@Listener
	public void stoppedServer(GameStoppingServerEvent e) {
		this.disconnect();
	}

	@Override
	public void onDisconnect() {}

	@Override
	public void onReload() {}
}
