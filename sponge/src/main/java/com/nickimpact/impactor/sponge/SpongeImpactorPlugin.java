package com.nickimpact.impactor.sponge;

import co.aikar.commands.BaseCommand;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.nickimpact.impactor.api.Impactor;
import com.nickimpact.impactor.api.configuration.Config;
import com.nickimpact.impactor.api.plugin.ImpactorPlugin;
import com.nickimpact.impactor.api.plugin.PluginMetadata;
import com.nickimpact.impactor.api.plugin.components.Depending;
import com.nickimpact.impactor.api.plugin.registry.PluginRegistry;
import com.nickimpact.impactor.api.services.text.MessageService;
import com.nickimpact.impactor.api.storage.StorageType;
import com.nickimpact.impactor.api.storage.dependencies.Dependency;
import com.nickimpact.impactor.api.storage.dependencies.DependencyManager;
import com.nickimpact.impactor.api.storage.dependencies.classloader.PluginClassLoader;
import com.nickimpact.impactor.api.storage.dependencies.classloader.ReflectionClassLoader;
import com.nickimpact.impactor.sponge.api.SpongeImpactorAPIProvider;
import com.nickimpact.impactor.sponge.configuration.ConfigKeys;
import com.nickimpact.impactor.sponge.configuration.SpongeConfig;
import com.nickimpact.impactor.sponge.configuration.SpongeConfigAdapter;
import com.nickimpact.impactor.sponge.plugin.AbstractSpongePlugin;
import com.nickimpact.impactor.sponge.scheduler.SpongeSchedulerAdapter;
import com.nickimpact.impactor.sponge.services.SpongeMojangServerStatusService;
import com.nickimpact.impactor.sponge.text.SpongeMessageService;
import lombok.Getter;
import com.nickimpact.impactor.common.api.ApiRegistrationUtil;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.AsynchronousExecutor;
import org.spongepowered.api.scheduler.SpongeExecutorService;
import org.spongepowered.api.scheduler.SynchronousExecutor;

import java.io.File;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.List;

@Plugin(id = "impactor", name = "ImpactorAPI", version = "@version@", description = "A universal API for multiple tools for development")
public class SpongeImpactorPlugin extends AbstractSpongePlugin implements Depending {

	@Getter private static SpongeImpactorPlugin instance;

	@Getter private SpongeMojangServerStatusService mojangServerStatusService;

	@Inject
	@ConfigDir(sharedRoot = false)
	private Path configDir;
	private Config config;

	@Inject
	public SpongeImpactorPlugin(Logger fallback, @SynchronousExecutor SpongeExecutorService sync, @AsynchronousExecutor SpongeExecutorService async) {
		super(PluginMetadata.builder().id("impactor").name("Impactor").version("@version@").build(), fallback);
		ApiRegistrationUtil.register(new SpongeImpactorAPIProvider(SpongeSchedulerAdapter.builder()
				.bootstrap(this)
				.sync(sync)
				.async(async)
				.scheduler(Sponge.getScheduler())
				.build()
		));
	}

	@Listener
	public void onPreInit(GamePreInitializationEvent e) {
		Impactor.getInstance().getRegistry().register(MessageService.class, new SpongeMessageService());
	}

	@Listener
	public void onInit(GameInitializationEvent e) {
		instance = this;
		this.config = new SpongeConfig(new SpongeConfigAdapter(this, new File(configDir.toFile(), "settings.conf")), new ConfigKeys());

		if(this.config.get(ConfigKeys.USE_MOJANG_STATUS_FETCHER)) {
			this.getPluginLogger().info("Enabling Mojang Status Watcher...");
			mojangServerStatusService = new SpongeMojangServerStatusService();
		}

		this.getPluginLogger().info("Pooling plugin dependencies...");
		List<StorageType> toLaunch = Lists.newArrayList();
		for(ImpactorPlugin plugin : PluginRegistry.getAll()) {
			if(plugin instanceof Depending) {
				Depending dependable = (Depending) plugin;

				for(StorageType st : dependable.getStorageRequirements()) {
					if(toLaunch.contains(st)) {
						continue;
					}

					toLaunch.add(st);
				}
			}
		}

		this.getPluginLogger().info("Dependencies found, setting these up now...");
		Impactor.getInstance().getRegistry().register(PluginClassLoader.class, new ReflectionClassLoader(this));
		Impactor.getInstance().getRegistry().register(DependencyManager.class, new DependencyManager(this));
		this.getPluginLogger().info("Initializing default dependencies...");
		this.getDependencyManager().loadDependencies(EnumSet.of(Dependency.CONFIGURATE_CORE, Dependency.CONFIGURATE_HOCON, Dependency.HOCON_CONFIG, Dependency.CONFIGURATE_GSON, Dependency.CONFIGURATE_YAML));
		this.getPluginLogger().info("Initializing plugin dependencies...");
		for(StorageType st : toLaunch) {
			this.getPluginLogger().info("Loading storage type module: " + st.getName());
			this.getDependencyManager().loadStorageDependencies(ImmutableSet.of(st));
		}
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

	public Config getConfig() {
		return this.config;
	}

	public PluginClassLoader getPluginClassLoader() {
		return Impactor.getInstance().getRegistry().get(PluginClassLoader.class);
	}

	public DependencyManager getDependencyManager() {
		return Impactor.getInstance().getRegistry().get(DependencyManager.class);
	}

	@Override
	public List<StorageType> getStorageRequirements() {
		return Lists.newArrayList();
	}
}
