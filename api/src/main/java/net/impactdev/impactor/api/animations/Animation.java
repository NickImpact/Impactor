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
