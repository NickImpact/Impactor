package com.nickimpact.impactor.bungee;

import co.aikar.commands.BaseCommand;
import com.google.common.collect.Lists;
import com.nickimpact.impactor.api.Impactor;
import com.nickimpact.impactor.api.configuration.Config;
import com.nickimpact.impactor.api.plugin.PluginMetadata;
import com.nickimpact.impactor.api.plugin.components.Depending;
import com.nickimpact.impactor.api.storage.StorageType;
import com.nickimpact.impactor.api.storage.dependencies.DependencyManager;
import com.nickimpact.impactor.api.storage.dependencies.classloader.PluginClassLoader;
import com.nickimpact.impactor.api.storage.dependencies.classloader.ReflectionClassLoader;
import com.nickimpact.impactor.bungee.api.BungeeImpactorAPIProvider;
import com.nickimpact.impactor.bungee.event.BungeeEventBus;
import com.nickimpact.impactor.bungee.plugin.AbstractBungeePlugin;
import com.nickimpact.impactor.bungee.scheduler.BungeeSchedulerAdapter;
import com.nickimpact.impactor.common.api.ApiRegistrationUtil;

import java.util.List;

public class BungeeImpactorPlugin extends AbstractBungeePlugin implements Depending {

	private final BungeeImpactorBootstrap bootstrap;

	public BungeeImpactorPlugin(BungeeImpactorBootstrap bootstrap, java.util.logging.Logger logger) {
		super(PluginMetadata.builder().id("impactor").name("Impactor").version("@version@").build(), logger);
		this.bootstrap = bootstrap;
	}

	public void onLoad() {
		ApiRegistrationUtil.register(new BungeeImpactorAPIProvider(
				new BungeeEventBus(this.bootstrap),
				new BungeeSchedulerAdapter(this.bootstrap)
		));

		Impactor.getInstance().getRegistry().register(PluginClassLoader.class, new ReflectionClassLoader(this));
		Impactor.getInstance().getRegistry().register(DependencyManager.class, new DependencyManager(this));

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
