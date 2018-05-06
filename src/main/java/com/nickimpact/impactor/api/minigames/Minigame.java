package com.nickimpact.impactor.api.minigames;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public interface Minigame {

	String getName();

	void start();

	void end();

	default void shutdown() {
		Sponge.getServer().shutdown(Text.of(TextColors.RED, "Thanks for playing!"));
	}

	void win(Player player);

	void lose(Player player);

	Scoreboard getScoreboard();
}
