package com.nickimpact.impactor.spigot;

import co.aikar.commands.BaseCommand;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
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
import com.nickimpact.impactor.spigot.logging.SpigotLogger;
import com.nickimpact.impactor.spigot.services.SpigotMojangServerStatusService;
import com.nickimpact.impactor.spigot.ui.SpigotUI;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;

public class SpigotImpactorPlugin extends JavaPlugin implements ImpactorPlugin, Dependable {

	@Getter private static SpigotImpactorPlugin instance;

	private boolean connected = false;

	private SpigotImpactorInfo info = new SpigotImpactorInfo();

	private SpigotLogger logger;

	@Getter private SpigotMojangServerStatusService mojangServerStatusService;

	private PluginClassLoader loader;
	private DependencyManager manager;

	public SpigotImpactorPlugin() {
		this.connect();
	}

	@Override
	public void onEnable() {
		instance = this;
		this.logger = new SpigotLogger(this);
		new ImpactorService();
//		this.mojangServerStatusService = new SpigotMojangServerStatusService();
		Bukkit.getPluginManager().registerEvents(new SpigotUI.UIListener(), this);

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
