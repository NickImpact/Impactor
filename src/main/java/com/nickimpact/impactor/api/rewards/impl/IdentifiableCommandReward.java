package com.nickimpact.impactor.api.rewards.impl;

import com.nickimpact.impactor.api.rewards.IdentifiableReward;
import lombok.Getter;

@Getter
public class IdentifiableCommandReward extends CommandReward implements IdentifiableReward<String> {

	private String ID;
	private String name;

	public IdentifiableCommandReward(String ID, String name, String command) {
		super(command);
		this.ID = ID;
		this.name = name;
	}
}
