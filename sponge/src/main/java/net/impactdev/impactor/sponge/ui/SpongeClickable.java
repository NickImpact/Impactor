package net.impactdev.impactor.sponge.ui;

import net.impactdev.impactor.api.gui.Clickable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;

@Getter
@AllArgsConstructor
public class SpongeClickable implements Clickable<Player, ClickInventoryEvent> {

	private Player player;
	private ClickInventoryEvent event;

}
