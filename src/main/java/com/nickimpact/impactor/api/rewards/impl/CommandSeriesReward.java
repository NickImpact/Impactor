package com.nickimpact.impactor.api.rewards.impl;

import com.nickimpact.impactor.api.rewards.Reward;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import scala.actors.threadpool.Arrays;

import java.util.List;

public class CommandSeriesReward implements Reward<List<String>> {

	private List<String> commands;

	public CommandSeriesReward(List<String> commands) {
		this.commands = commands;
	}

	public CommandSeriesReward(String... commands) {
		this.commands = Arrays.asList(commands);
	}

	@Override
	public List<String> getReward() {
		return this.commands;
	}

	@Override
	public void give(Player player) throws Exception {
		for(String cmd : this.commands) {
			Sponge.getCommandManager().process(Sponge.getServer().getConsole(), cmd);
		}
	}
}
