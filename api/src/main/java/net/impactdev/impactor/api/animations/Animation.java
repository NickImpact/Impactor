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

public interface Animation {

	/**
	 * Represents the frames/second an animation will run at.
	 *
	 * @return The number of frames the animation will play within a second
	 */
	int getFPS();

	/**
	 * Retrieves the number of frames the animation is to be composed of.
	 *
	 * @return The number of frames making up an animation
	 */
	int getNumFrames();

	/**
	 * Fetches the current frame the animation is on
	 *
	 * @return The frame currently being played
	 */
	int getCurrentFrame();

	/**
	 * Starts an animation, starting after the delay in milliseconds.
	 *
	 * @param loop Whether or not the animation will loop
	 */
	default void play(boolean loop) {
		this.play(0, loop);
	}

	/**
	 * Starts an animation, starting after the delay in milliseconds.
	 *
	 * @param delay The delay, in milliseconds, until the animation begins
	 * @param loop Whether or not the animation will loop
	 */
	void play(long delay, boolean loop);

	default void end() {
		Runnable completion = this.whenComplete();
		if(completion != null) {
			completion.run();
		}
		stop();
	}

	boolean isRunning();

	/** Cancels any running instance of this Animation */
	void stop();

	/** Resets an animation, altering its current frame back to 0 */
	void reset();

	/** Handles the cleaning actions of a animation when it completes or is stopped */
	void clean();

	/**
	 * Specifies whether or not the animation has completed
	 */
	boolean isComplete();

	/**
	 * Specifies the action to carry out when an Animation completes
	 *
	 * @return A runnable instance, or null for no completion action
	 */
	Runnable whenComplete();
}
