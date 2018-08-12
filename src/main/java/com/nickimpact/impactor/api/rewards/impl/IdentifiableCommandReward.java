package com.nickimpact.impactor.api.rewards.impl;

import com.nickimpact.impactor.api.rewards.IdentifiableReward;
import com.nickimpact.impactor.json.Typing;
import lombok.Getter;

@Getter
@Typing("ICommand")
public class IdentifiableCommandReward extends CommandReward implements IdentifiableReward<String> {

	private final String type = "ICommand";

	private String ID;
	private String name;

	public IdentifiableCommandReward(String ID, String name, String command) {
		super(command);
		this.ID = ID;
		this.name = name;
	}
}
