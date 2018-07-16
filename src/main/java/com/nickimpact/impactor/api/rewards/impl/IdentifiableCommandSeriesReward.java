package com.nickimpact.impactor.api.rewards.impl;

import com.nickimpact.impactor.api.rewards.IdentifiableReward;
import lombok.Getter;

import java.util.List;

@Getter
public class IdentifiableCommandSeriesReward extends CommandSeriesReward implements IdentifiableReward<List<String>> {

	private String ID;
	private String name;

	public IdentifiableCommandSeriesReward(String ID, String name, List<String> commands) {
		super(commands);
		this.ID = ID;
		this.name = name;
	}

	public IdentifiableCommandSeriesReward(String ID, String name, String... commands) {
		super(commands);
		this.ID = ID;
		this.name = name;
	}
}
