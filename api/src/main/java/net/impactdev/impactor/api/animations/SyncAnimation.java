/*
 * This file is part of Impactor, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018-2021 NickImpact
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package net.impactdev.impactor.api.animations;

import net.impactdev.impactor.api.plugin.ImpactorPlugin;

public abstract class SyncAnimation<T> implements Animation {

	protected final ImpactorPlugin plugin;

	private int frame = 0;
	protected T runner;

	public SyncAnimation(ImpactorPlugin plugin) {
		this.plugin = plugin;
	}

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

	@Override
	public boolean isComplete() {
		return this.frame == this.getNumFrames();
	}

	public abstract void fireAsync(Runnable runnable);
}
