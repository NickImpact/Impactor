package com.nickimpact.impactor.spigot.animations;

import com.nickimpact.impactor.api.animations.SyncAnimation;
import com.nickimpact.impactor.api.plugin.ImpactorPlugin;
import com.nickimpact.impactor.spigot.SpigotImpactorPlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public abstract class SpigotSyncAnimation extends SyncAnimation<BukkitTask> {

	public SpigotSyncAnimation(ImpactorPlugin plugin) {
		super(plugin);
	}

	@Override
	public void fireAsync(Runnable runnable) {
		Bukkit.getScheduler().runTaskAsynchronously(SpigotImpactorPlugin.getInstance(), runnable);
	}
}
