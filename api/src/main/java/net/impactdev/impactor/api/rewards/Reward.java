package net.impactdev.impactor.api.rewards;

public interface Reward<T, U> {

	/**
	 * Returns an instance of the reward based on the specs of its design.
	 *
	 * @return An instance of the defined reward
	 */
	T getReward();

	/**
	 * Gives the current instance of a Reward to a player.
	 *
	 * @param player The player we want to give the award to
	 * @throws Exception In the event the reward is unable to be given out to the intended player
	 */
	void give(U player) throws Exception;

}
