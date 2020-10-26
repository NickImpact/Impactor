package net.impactdev.impactor.api.animations;

import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AsyncAnimation<T> implements Animation {

	protected final ImpactorPlugin plugin;

	private int frame = 0;

	protected T runner;

	protected void run(boolean loop) {
		this.playFrame(frame++);
		if(loop) {
			this.frame %= this.getNumFrames();
		}

		if(this.frame >= this.getNumFrames()) {
			this.end();
		}
	}

	protected abstract void playFrame(int frame);

	@Override
	public boolean isRunning() {
		return false;
	}

	@Override
	public int getCurrentFrame() {
		return 0;
	}

	@Override
	public void reset() {
		this.frame = 0;
	}

	public abstract void fireSync(Runnable runnable);
}
