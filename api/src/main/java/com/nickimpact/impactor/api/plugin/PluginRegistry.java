package com.nickimpact.impactor.api.plugin;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PluginRegistry {

	private static List<ImpactorPlugin> plugins = Lists.newArrayList();

	public static void register(ImpactorPlugin plugin) {
		plugins.add(plugin);
	}

	public static void unregister(ImpactorPlugin plugin) {
		plugins.remove(plugin);
	}

	public static boolean isLoaded(ImpactorPlugin plugin) {
		return plugins.stream().anyMatch(pl -> pl.equals(plugin));
	}

	public static boolean isLoaded(String id) {
		return plugins.stream().anyMatch(pl -> pl.getPluginInfo().getID().equals(id));
	}

	public static Optional<ImpactorPlugin> getPlugin(ImpactorPlugin plugin) {
		return plugins.stream().filter(pl -> pl.equals(plugin)).findAny();
	}

	public static Optional<ImpactorPlugin> getPlugin(String id) {
		return plugins.stream().filter(pl -> pl.getPluginInfo().getID().equals(id)).findAny();
	}

	public static List<ImpactorPlugin> getPlugins() {
		return plugins;
	}

	public static List<ImpactorPlugin> getConnected() {
		return plugins.stream().filter(ImpactorPlugin::isConnected).collect(Collectors.toList());
	}

	public static List<ImpactorPlugin> getDisconnected() {
		return plugins.stream().filter(plugin -> !plugin.isConnected()).collect(Collectors.toList());
	}

}
