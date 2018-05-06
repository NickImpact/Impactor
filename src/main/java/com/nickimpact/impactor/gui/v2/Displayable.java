package com.nickimpact.impactor.gui.v2;

import org.spongepowered.api.entity.living.player.Player;

/**
 * (Some note will appear here)
 *
 * @author NickImpact (Nick DeGruccio)
 */
public interface Displayable {

	UI getDisplay();

	default void open(Player player) {
		this.getDisplay().open(player);
	}

	default void close(Player player) {
		this.getDisplay().close(player);
	}
}
