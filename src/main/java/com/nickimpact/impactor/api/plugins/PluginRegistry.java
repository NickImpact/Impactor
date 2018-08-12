package com.nickimpact.impactor.api.plugins;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PluginRegistry {

	private static List<SpongePlugin> plugins = Lists.newArrayList();

	public static void register(SpongePlugin plugin) {
		plugins.add(plugin);
	}

	public static void unregister(SpongePlugin plugin) {
		plugins.remove(plugin);
	}

	public static boolean isLoaded(SpongePlugin plugin) {
		return plugins.stream().anyMatch(pl -> pl.equals(plugin));
	}

	public static boolean isLoaded(String id) {
		return plugins.stream().anyMatch(pl -> pl.getPluginInfo().getID().equals(id));
	}

	public static Optional<SpongePlugin> getPlugin(SpongePlugin plugin) {
		return plugins.stream().filter(pl -> pl.equals(plugin)).findAny();
	}

	public static Optional<SpongePlugin> getPlugin(String id) {
		return plugins.stream().filter(pl -> pl.getPluginInfo().getID().equals(id)).findAny();
	}

	public static List<SpongePlugin> getPlugins() {
		return plugins;
	}

	public static List<SpongePlugin> getConnected() {
		return plugins.stream().filter(SpongePlugin::isConnected).collect(Collectors.toList());
	}

	public static List<SpongePlugin> getDisconnected() {
		return plugins.stream().filter(plugin -> !plugin.isConnected()).collect(Collectors.toList());
	}
}
