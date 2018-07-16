package com.nickimpact.impactor.api.rewards;

public interface IdentifiableReward<T> extends Reward<T> {

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
}
