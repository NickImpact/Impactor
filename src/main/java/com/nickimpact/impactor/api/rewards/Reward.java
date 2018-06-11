package com.nickimpact.impactor.api.rewards;

import org.spongepowered.api.entity.living.player.Player;

public interface Reward {

	/**
	 * Retrieves the ID for this reward. This will be the primary key for referencing
	 * reward types.
	 *
	 * @return The ID of the reward.
	 */
	String getID();

	/**
	 * Retrieves the name of the reward. This represents the actual display name for the reward.
	 *
	 * @return The name of the reward.
	 */
	String getName();

	/**
	 * Gives the current instance of a Reward to a player.
	 *
	 * @param player The player we want to give the award to
	 * @throws Exception In the event the reward is unable to be given out to the intended player
	 */
	void give(Player player) throws Exception;
}
