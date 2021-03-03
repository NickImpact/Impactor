package net.impactdev.impactor.spigot.animations;

import net.impactdev.impactor.api.animations.SyncAnimation;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import net.impactdev.impactor.spigot.SpigotImpactorPlugin;
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
