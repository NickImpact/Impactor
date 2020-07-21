package com.nickimpact.impactor.spigot.logging;

import com.nickimpact.impactor.api.logging.Logger;
import com.nickimpact.impactor.api.plugin.ImpactorPlugin;
import org.bukkit.ChatColor;

import java.util.List;

public class SpigotLogger implements Logger {

	private ImpactorPlugin plugin;

	public SpigotLogger(ImpactorPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void noTag(String message) {
		System.out.println(ChatColor.translateAlternateColorCodes('&', message));
	}

	@Override
	public void noTag(List<String> message) {
		for(String str : message) {
			this.noTag(str);
		}
	}

	@Override
	public void info(String message) {
		System.out.println(ChatColor.translateAlternateColorCodes('&', "&e" + plugin.getPluginInfo().getName() + " &7\u00bb " + message));
	}

	@Override
	public void info(List<String> message) {
		for(String str : message) {
			this.info(str);
		}
	}

	@Override
	public void warn(String message) {
		System.out.println(ChatColor.translateAlternateColorCodes('&', "&e" + plugin.getPluginInfo().getName() + " &7(&6Warning&7) " + message));
	}

	@Override
	public void warn(List<String> message) {
		for(String str : message) {
			this.warn(str);
		}
	}

	@Override
	public void error(String message) {
		System.out.println(ChatColor.translateAlternateColorCodes('&', "&e" + plugin.getPluginInfo().getName() + " &7(&cError&7) " + message));
	}

	@Override
	public void error(List<String> message) {
		for(String str : message) {
			this.error(str);
		}
	}

	@Override
	public void debug(String message) {
		System.out.println(ChatColor.translateAlternateColorCodes('&', "&e" + plugin.getPluginInfo().getName() + " &7(&bDebug&7) " + message));
	}

	@Override
	public void debug(List<String> message) {
		for(String str : message) {
			this.debug(str);
		}
	}
}
