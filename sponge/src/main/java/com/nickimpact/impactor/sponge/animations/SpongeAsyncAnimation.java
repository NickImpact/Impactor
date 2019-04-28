package com.nickimpact.impactor.sponge.animations;

import com.nickimpact.impactor.api.animations.AsyncAnimation;
import com.nickimpact.impactor.api.plugin.ImpactorPlugin;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;

import java.util.concurrent.TimeUnit;

public abstract class SpongeAsyncAnimation extends AsyncAnimation<Task> {

	public SpongeAsyncAnimation(ImpactorPlugin plugin) {
		super(plugin);
	}

	@Override
	public void play(long delay, boolean loop) {
		this.runner = Task.builder().execute(() -> this.run(loop))
				.interval(Math.round(1000.0 / getFPS()), TimeUnit.MILLISECONDS)
				.delay(delay, TimeUnit.MILLISECONDS)
				.async()
				.submit(this.plugin);
	}

	@Override
	public void stop() {
		if(runner != null) {
			runner.cancel();
		}
	}

	@Override
	public void fireSync(Runnable runnable) {
		Sponge.getScheduler().createTaskBuilder().execute(runnable).submit(this.plugin);
	}
}
