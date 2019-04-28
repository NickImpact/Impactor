package com.nickimpact.impactor.spigot.ui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.nickimpact.impactor.api.gui.InventoryDimensions;
import com.nickimpact.impactor.api.gui.Layout;
import com.nickimpact.impactor.api.gui.UI;
import com.nickimpact.impactor.api.plugin.ImpactorPlugin;
import com.nickimpact.impactor.spigot.SpigotImpactorPlugin;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

public class SpigotUI implements UI<Player, InventoryClickEvent, SpigotIcon> {

	private final ImpactorPlugin plugin;

	private Inventory inventory;
	private InventoryDimensions dimensions;

	private Map<Integer, SpigotIcon> slots;
	private List<BiConsumer<Player, InventoryClickEvent>> additionals = Lists.newArrayList();

	private UIListener listener;

	private SpigotUI(ImpactorPlugin plugin, SpigotUIBuilder builder) {
		this.plugin = plugin;
		this.slots = Maps.newHashMap();
		this.inventory = Bukkit.createInventory(null, builder.size, builder.title);
		this.dimensions = new InventoryDimensions(9, 6);
	}

	@Override
	public ImpactorPlugin getPlugin() {
		return this.plugin;
	}

	@Override
	public SpigotUI define(Layout<SpigotIcon> layout) {
		this.slots.clear();
		for(int i = 0; i < inventory.getSize(); i++) {
			final int slot = i;
			layout.getIcon(i).ifPresent(icon -> this.setSlot(slot, icon));
		}
		return this;
	}

	@Override
	public Optional<SpigotIcon> getIcon(int slot) {
		return Optional.ofNullable(this.slots.get(slot));
	}

	@Override
	public void setSlot(int slot, SpigotIcon icon) {
		this.inventory.setItem(slot, icon.getDisplay());
		this.slots.put(slot, icon);
	}

	@Override
	public void open(Player player) {
		player.openInventory(this.inventory);
		Bukkit.getServer().getPluginManager().registerEvents(this.listener = new UIListener(player, this), SpigotImpactorPlugin.getInstance());
	}

	@Override
	public void close(Player player) {
		player.closeInventory();
		HandlerList.unregisterAll(this.listener);
	}

	@Override
	public void clear() {
		this.inventory.clear();
		this.slots.clear();
	}

	@Override
	public void clear(int... slots) {
		for(int slot : slots) {
			this.inventory.clear(slot);
			this.slots.remove(slot);
		}
	}

	@Override
	public UI attachListener(BiConsumer<Player, InventoryClickEvent> listener) {
		this.additionals.add(listener);
		return this;
	}

	@Override
	public InventoryDimensions getDimension() {
		return this.dimensions;
	}

	public static SpigotUIBuilder builder() {
		return new SpigotUIBuilder();
	}

	public static class SpigotUIBuilder {

		private String title;
		private int size;

		public SpigotUIBuilder title(String title) {
			this.title = title;
			return this;
		}

		public SpigotUIBuilder size(int size) {
			this.size = size;
			return this;
		}

		public SpigotUI build() {
			return new SpigotUI(SpigotImpactorPlugin.getInstance(), this);
		}
	}

	@AllArgsConstructor
	public static class UIListener implements Listener {

		private Player player;
		private SpigotUI ui;

		@EventHandler
		public void onInventoryClick(InventoryClickEvent e) {
			if(e.getWhoClicked().getUniqueId().equals(this.player.getUniqueId())) {
				if(e.getClickedInventory() == null) {
					return;
				}

				if(e.getClickedInventory().getTitle().equals(ui.inventory.getTitle())) {
					e.setCancelled(true);
					ui.getIcon(e.getRawSlot()).ifPresent(icon -> icon.process(new SpigotClickable(this.player, e)));
					for (BiConsumer<Player, InventoryClickEvent> extra : ui.additionals) {
						extra.accept(this.player, e);
					}
				}
			}
		}
	}
}
