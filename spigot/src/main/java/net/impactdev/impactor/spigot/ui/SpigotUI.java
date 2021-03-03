package net.impactdev.impactor.spigot.ui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.impactdev.impactor.api.gui.InventoryDimensions;
import net.impactdev.impactor.api.gui.Layout;
import net.impactdev.impactor.api.gui.UI;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import net.impactdev.impactor.spigot.SpigotImpactorPlugin;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SpigotUI implements UI<Player, InventoryClickEvent, InventoryCloseEvent, SpigotIcon> {

	private final ImpactorPlugin plugin;

	private Inventory inventory;
	private InventoryDimensions dimensions;

	private SpigotLayout layout;

	private Map<Integer, SpigotIcon> slots;
	private List<BiConsumer<Player, InventoryClickEvent>> additionals = Lists.newArrayList();
	private List<Consumer<InventoryCloseEvent>> closeAdditionals = Lists.newArrayList();

	private SpigotUI(ImpactorPlugin plugin, SpigotUIBuilder builder) {
		this.plugin = plugin;
		this.slots = Maps.newHashMap();
		this.inventory = Bukkit.createInventory(null, builder.size, builder.title);
		this.dimensions = new InventoryDimensions(9, builder.size / 9);
	}

	@Override
	public ImpactorPlugin getPlugin() {
		return this.plugin;
	}

	@Override
	public SpigotLayout getLayout() {
		return this.layout;
	}

	@Override
	public SpigotUI define(Layout<SpigotIcon> layout) {
		this.layout = (SpigotLayout) layout;
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
		UIListener.register(player.getUniqueId(), this);
	}

	@Override
	public void close(Player player) {
		player.closeInventory();
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
	public UI attachCloseListener(Consumer<InventoryCloseEvent> listener) {
		this.closeAdditionals.add(listener);
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
			this.title = ChatColor.translateAlternateColorCodes('&', title);
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

		private static Map<UUID, SpigotUI> viewers = Maps.newHashMap();

		public static void register(UUID uuid, SpigotUI ui) {
			viewers.put(uuid, ui);
		}

		public static SpigotUI unregister(UUID uuid) {
			return viewers.remove(uuid);
		}

		@EventHandler
		public void onInventoryClick(InventoryClickEvent e) {
			if(viewers.containsKey(e.getWhoClicked().getUniqueId())) {
				if(e.getClickedInventory() == null) {
					return;
				}

				Player player = Bukkit.getPlayer(e.getWhoClicked().getUniqueId());
				SpigotUI ui = viewers.get(player.getUniqueId());

				if(e.getClickedInventory().getTitle().equals(ui.inventory.getTitle()) || player.getOpenInventory().getTitle().equals(ui.inventory.getTitle())) {
					e.setCancelled(true);
					ui.getIcon(e.getRawSlot()).ifPresent(icon -> icon.process(new SpigotClickable(player, e)));
					for (BiConsumer<Player, InventoryClickEvent> extra : ui.additionals) {
						extra.accept(player, e);
					}
				}
			}
		}

		@EventHandler
		public void onInventoryClose(InventoryCloseEvent e) {
			if(viewers.containsKey(e.getPlayer().getUniqueId())) {
				SpigotUI ui = viewers.get(e.getPlayer().getUniqueId());
				if (e.getInventory().getTitle().equals(ui.inventory.getTitle())) {
					unregister(e.getPlayer().getUniqueId());
					ui.closeAdditionals.forEach(c -> c.accept(e));
				}
			}
		}
	}
}
