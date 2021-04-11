package net.impactdev.impactor.sponge.animations;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.animations.SyncAnimation;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import net.impactdev.impactor.api.scheduler.SchedulerTask;
import net.impactdev.impactor.sponge.scheduler.SpongeSchedulerAdapter;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;

import java.util.concurrent.TimeUnit;

public abstract class SpongeSyncAnimation extends SyncAnimation<SchedulerTask> {

	public SpongeSyncAnimation(ImpactorPlugin plugin) {
		super(plugin);
	}

	@Override
	public void play(long delay, boolean loop) {
		this.runner = Impactor.getInstance().getScheduler().asyncDelayedAndRepeating(
				() -> Impactor.getInstance().getScheduler().executeSync(() -> this.run(loop)),
				delay, TimeUnit.MILLISECONDS,
				Math.round(1000.0 / getFPS()), TimeUnit.MILLISECONDS
		);
	}

	@Override
	public void stop() {
		if(runner != null) {
			runner.cancel();
		}
	}

	@Override
	public void fireAsync(Runnable runnable) {
		Impactor.getInstance().getScheduler().executeAsync(runnable);
	}
}
