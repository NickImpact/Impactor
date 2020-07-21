package com.nickimpact.impactor.spigot.services;

import com.nickimpact.impactor.api.services.mojang.MojangServerStatusService;
import com.nickimpact.impactor.spigot.SpigotImpactorPlugin;
import org.bukkit.Bukkit;

public class SpigotMojangServerStatusService extends MojangServerStatusService {
	@Override
	public void run() {
		Bukkit.getScheduler().runTaskTimerAsynchronously(SpigotImpactorPlugin.getInstance(), () -> {
			try {
				this.checker.fetch();
			} catch (Exception e) {
				SpigotImpactorPlugin.getInstance().getPluginLogger().warn("Failed to read Mojang Server Status with reason (" + e.getMessage() + ")");
			}
		}, 0, 30 * 20);
	}
}
