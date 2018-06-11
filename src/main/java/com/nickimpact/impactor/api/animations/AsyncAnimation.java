package com.nickimpact.impactor.api.animations;

import com.nickimpact.impactor.api.plugins.SpongePlugin;
import lombok.RequiredArgsConstructor;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public abstract class AsyncAnimation implements Animation {

	private final SpongePlugin plugin;

	private int frame = 0;

	private Task runner;

	@Override
	public void play(long delay, boolean loop) {
		this.runner = Task.builder().execute(() -> this.run(loop))
				.interval((long)Math.round(1000 / getFPS()), TimeUnit.MILLISECONDS)
				.async()
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
	public boolean isRunning() {
		return this.frame < getNumFrames() && this.frame != 0;
	}

	@Override
	public int getCurrentFrame() {
		return this.frame;
	}

	public void fireSync(Runnable runnable) {
		Sponge.getScheduler().createTaskBuilder().execute(runnable).submit(plugin);
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
