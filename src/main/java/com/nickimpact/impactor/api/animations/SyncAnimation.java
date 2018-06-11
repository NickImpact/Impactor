package com.nickimpact.impactor.api.animations;

import com.nickimpact.impactor.api.plugins.SpongePlugin;
import lombok.RequiredArgsConstructor;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public abstract class SyncAnimation implements Animation {

	private final SpongePlugin plugin;

	private int frame = 0;

	private Task runner;

	@Override
	public void play(long delay, boolean loop) {
		this.runner = Task.builder().execute(() -> this.run(loop))
				.interval((long)Math.round(1000 / getFPS()), TimeUnit.MILLISECONDS)
				.delay(delay, TimeUnit.MILLISECONDS)
				.submit(plugin);
	}

	private void run(boolean loop) {
		this.playFrame(frame++);
		if(loop) {
			frame %= getNumFrames();
		}

		if(frame >= getNumFrames()) {
			this.end();
		}
	}

	protected abstract void playFrame(int frame);

	@Override
	public int getCurrentFrame() {
		return this.frame;
	}

	public void fireAsync(Runnable runnable) {
		Sponge.getScheduler().createTaskBuilder().execute(runnable).async().submit(plugin);
	}

	@Override
	public void stop() {
		if(runner != null) {
			runner.cancel();
		}
	}

	@Override
	public void reset() {
		this.frame = 0;
	}
}
