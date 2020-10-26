package net.impactdev.impactor.spigot.animations;

import net.impactdev.impactor.api.animations.AsyncAnimation;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import net.impactdev.impactor.spigot.SpigotImpactorPlugin;
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
