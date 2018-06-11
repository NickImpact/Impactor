package com.nickimpact.impactor.api.animations;

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
	 * @return The number of frames making up an animatiion
	 */
	int getNumFrames();

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
		Runnable completion = onComplete();
		if(completion != null) {
			completion.run();
		}
		stop();
	}

	boolean isRunning();

	/**
	 * Fetches the current frame the animation is on
	 *
	 * @return The frame currently being played
	 */
	int getCurrentFrame();

	/** Cancels any running instance of this Animation */
	void stop();

	/** Resets an animation, altering its current frame back to 0 */
	void reset();

	/**
	 * Specifies the action to carry out when an Animation completes
	 *
	 * @return A runnable instance, or null for no completion action
	 */
	Runnable onComplete();
}
