package com.nickimpact.impactor.spigot.animations;

import com.nickimpact.impactor.api.animations.AsyncAnimation;
import com.nickimpact.impactor.api.plugin.ImpactorPlugin;
import com.nickimpact.impactor.spigot.SpigotImpactorPlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public abstract class SpigotAsyncAnimation extends AsyncAnimation<BukkitTask> {

	public SpigotAsyncAnimation(ImpactorPlugin plugin) {
		super(plugin);
	}

	@Override
	public void fireSync(Runnable runnable) {
		Bukkit.getScheduler().runTask(SpigotImpactorPlugin.getInstance(), runnable);
	}
}
