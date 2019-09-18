package com.nickimpact.impactor.sponge;

import co.aikar.commands.BaseCommand;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.nickimpact.impactor.api.ImpactorService;
import com.nickimpact.impactor.api.configuration.Config;
import com.nickimpact.impactor.api.logging.Logger;
import com.nickimpact.impactor.api.platform.Platform;
import com.nickimpact.impactor.api.plugin.Dependable;
import com.nickimpact.impactor.api.plugin.ImpactorPlugin;
import com.nickimpact.impactor.api.plugin.PluginInfo;
import com.nickimpact.impactor.api.plugin.PluginRegistry;
import com.nickimpact.impactor.api.storage.StorageType;
import com.nickimpact.impactor.api.storage.dependencies.Dependency;
import com.nickimpact.impactor.api.storage.dependencies.DependencyManager;
import com.nickimpact.impactor.api.storage.dependencies.classloader.PluginClassLoader;
import com.nickimpact.impactor.api.storage.dependencies.classloader.ReflectionClassLoader;
import com.nickimpact.impactor.sponge.configuration.ConfigKeys;
import com.nickimpact.impactor.sponge.configuration.SpongeConfig;
import com.nickimpact.impactor.sponge.configuration.SpongeConfigAdapter;
import com.nickimpact.impactor.sponge.logging.SpongeLogger;
import com.nickimpact.impactor.sponge.services.SpongeMojangServerStatusService;
import lombok.Getter;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

import java.io.File;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;

@Plugin(id = "impactor", name = "ImpactorAPI", version = "2.0.0", description = "A universal API for multiple tools for development")
public class SpongeImpactorPlugin extends AbstractSpongePlugin implements Dependable {

	@Getter private static SpongeImpactorPlugin instance;

	@Getter private SpongeMojangServerStatusService mojangServerStatusService;

	@Inject
	private org.slf4j.Logger fallback;
	private SpongeLogger logger;

	@Inject
	@ConfigDir(sharedRoot = false)
	private Path configDir;
	private Config config;

	private PluginClassLoader loader;
	private DependencyManager manager;

	public SpongeImpactorPlugin() {
		this.connect();
	}

	@Listener
	public void onInit(GameInitializationEvent e) {
		instance = this;
		new ImpactorService();
		this.logger = new SpongeLogger(this, this.fallback);
		this.config = new SpongeConfig(new SpongeConfigAdapter(this, new File(configDir.toFile(), "settings.conf")), new ConfigKeys());

		if(this.config.get(ConfigKeys.USE_MOJANG_STATUS_FETCHER)) {
			this.logger.info("Enabling Mojang Status Watcher...");
			mojangServerStatusService = new SpongeMojangServerStatusService();
		}

		this.logger.info("Pooling plugin dependencies...");
		List<StorageType> toLaunch = Lists.newArrayList();
		for(ImpactorPlugin plugin : PluginRegistry.getConnected()) {
			if(plugin instanceof Dependable) {
				Dependable dependable = (Dependable) plugin;

				for(StorageType st : dependable.getStorageTypes()) {
					if(toLaunch.contains(st)) {
						continue;
					}

					toLaunch.add(st);
				}
			}
		}

		this.logger.info("Dependencies found, setting these up now...");
		this.loader = new ReflectionClassLoader(this);
		this.manager = new DependencyManager(this);
		this.logger.info("Initializing default dependencies...");
		this.manager.loadDependencies(EnumSet.of(Dependency.CONFIGURATE_CORE, Dependency.CONFIGURATE_HOCON, Dependency.HOCON_CONFIG, Dependency.CONFIGURATE_GSON, Dependency.CONFIGURATE_YAML));
		this.logger.info("Initializing plugin dependencies...");
		for(StorageType st : toLaunch) {
			this.logger.debug("Loading storage type: " + st.getName());
			this.manager.loadStorageDependencies(ImmutableSet.of(st));
		}
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

	public Config getConfig() {
		return this.config;
	}

	@Override
	public PluginClassLoader getPluginClassLoader() {
		return this.loader;
	}

	@Override
	public DependencyManager getDependencyManager() {
		return this.manager;
	}

	@Override
	public List<StorageType> getStorageTypes() {
		return Lists.newArrayList();
	}
}
