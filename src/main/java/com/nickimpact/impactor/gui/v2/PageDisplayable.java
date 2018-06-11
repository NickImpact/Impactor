package com.nickimpact.impactor.gui.v2;

import org.spongepowered.api.entity.living.player.Player;

public interface PageDisplayable {

	Page getDisplay();

	default void open(Player player, int page) {
		this.getDisplay().open(player, page);
	}

	default void close(Player player) {
		this.getDisplay().close(player);
	}
}
