package net.impactdev.impactor.spigot.ui;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

@Getter
@AllArgsConstructor
public class SpigotClickable implements Clickable<Player, InventoryClickEvent> {
	private Player player;
	private InventoryClickEvent event;
}
