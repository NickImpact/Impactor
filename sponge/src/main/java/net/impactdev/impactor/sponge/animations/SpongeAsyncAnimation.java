package net.impactdev.impactor.sponge.animations;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.animations.AsyncAnimation;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import net.impactdev.impactor.api.scheduler.SchedulerTask;

import java.util.concurrent.TimeUnit;

public abstract class SpongeAsyncAnimation extends AsyncAnimation<SchedulerTask> {

	public SpongeAsyncAnimation(ImpactorPlugin plugin) {
		super(plugin);
	}

	@Override
	public void play(long delay, boolean loop) {
		this.runner = Impactor.getInstance().getScheduler().asyncDelayedAndRepeating(
				() -> this.run(loop),
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
	public void fireSync(Runnable runnable) {
		Impactor.getInstance().getScheduler().executeSync(runnable);
	}
}
