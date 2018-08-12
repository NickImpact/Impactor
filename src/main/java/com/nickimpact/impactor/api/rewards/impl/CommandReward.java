package com.nickimpact.impactor.api.rewards.impl;

import com.nickimpact.impactor.api.rewards.Reward;
import com.nickimpact.impactor.json.Typing;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

@Typing("Command")
public class CommandReward implements Reward<String> {

	private final String type = "Command";

	private String command;

	public CommandReward(String command) {
		this.command = command;
	}

	@Override
	public String getReward() {
		return this.command;
	}

	@Override
	public void give(Player player) throws Exception {
		Sponge.getCommandManager().process(Sponge.getServer().getConsole(), command);
	}
}
