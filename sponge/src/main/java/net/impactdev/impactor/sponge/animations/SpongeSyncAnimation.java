package net.impactdev.impactor.sponge.animations;

import net.impactdev.impactor.api.animations.SyncAnimation;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;

import java.util.concurrent.TimeUnit;

public abstract class SpongeSyncAnimation extends SyncAnimation<Task> {

	public SpongeSyncAnimation(ImpactorPlugin plugin) {
		super(plugin);
	}

	@Override
	public void fireAsync(Runnable runnable) {
		Sponge.getScheduler().createTaskBuilder().execute(runnable).async().submit(this.plugin);
	}

	@Override
	public void play(long delay, boolean loop) {
		this.runner = Task.builder().execute(() -> this.run(loop))
				.interval(Math.round(1000.0 / getFPS()), TimeUnit.MILLISECONDS)
				.delay(delay, TimeUnit.MILLISECONDS)
				.submit(this.plugin);
	}

	@Override
	public void stop() {
		if(runner != null) {
			runner.cancel();
		}
	}
}
