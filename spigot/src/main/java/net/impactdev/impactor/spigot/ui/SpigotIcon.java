package net.impactdev.impactor.spigot.ui;

import net.impactdev.impactor.api.gui.Clickable;
import net.impactdev.impactor.api.gui.Icon;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class SpigotIcon implements Icon<ItemStack, Player, InventoryClickEvent> {

	private ItemStack display;
	private Set<Consumer<Clickable<Player, InventoryClickEvent>>> listeners;

	public static final SpigotIcon BORDER;

	static {
		BORDER = new SpigotIcon(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15));
		ItemMeta meta = BORDER.getDisplay().getItemMeta();
		meta.setDisplayName(" ");

		BORDER.getDisplay().setItemMeta(meta);
	}

	public SpigotIcon(ItemStack display) {
		this.display = display;
		this.listeners = new HashSet<>();
	}

	@Override
	public ItemStack getDisplay() {
		return this.display;
	}

	@Override
	public void addListener(Consumer<Clickable<Player, InventoryClickEvent>> listener) {
		this.listeners.add(listener);
	}

	@Override
	public void process(Clickable<Player, InventoryClickEvent> clickable) {
		for(Consumer<Clickable<Player, InventoryClickEvent>> listener : this.listeners) {
			listener.accept(clickable);
		}
	}
}
