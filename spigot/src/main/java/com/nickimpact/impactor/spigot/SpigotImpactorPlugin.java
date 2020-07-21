package com.nickimpact.impactor.spigot;

import co.aikar.commands.BaseCommand;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.nickimpact.impactor.api.Impactor;
import com.nickimpact.impactor.api.configuration.Config;
import com.nickimpact.impactor.api.plugin.ImpactorPlugin;
import com.nickimpact.impactor.api.plugin.PluginMetadata;
import com.nickimpact.impactor.api.plugin.components.Depending;
import com.nickimpact.impactor.api.plugin.registry.PluginRegistry;
import com.nickimpact.impactor.api.storage.StorageType;
import com.nickimpact.impactor.api.storage.dependencies.Dependency;
import com.nickimpact.impactor.api.storage.dependencies.DependencyManager;
import com.nickimpact.impactor.api.storage.dependencies.classloader.PluginClassLoader;
import com.nickimpact.impactor.api.storage.dependencies.classloader.ReflectionClassLoader;
import com.nickimpact.impactor.spigot.logging.SpigotLogger;
import com.nickimpact.impactor.spigot.plugin.AbstractSpigotPlugin;
import com.nickimpact.impactor.spigot.ui.SpigotUI;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.EnumSet;
import java.util.List;

public class SpigotImpactorPlugin extends AbstractSpigotPlugin implements Depending {

	@Getter private static SpigotImpactorPlugin instance;

	public SpigotImpactorPlugin() {
		super(PluginMetadata.builder().id("impactor").name("Impactor").version("@version@").build());

		// TODO - API Registration
	}

	@Override
	public void onEnable() {
		instance = this;
		Bukkit.getPluginManager().registerEvents(new SpigotUI.UIListener(), this);

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
			this.getPluginLogger().debug("Loading storage type: " + st.getName());
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
