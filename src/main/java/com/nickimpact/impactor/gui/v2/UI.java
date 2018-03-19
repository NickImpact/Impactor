package com.nickimpact.impactor.gui.v2;

import com.google.common.collect.Maps;
import com.nickimpact.impactor.CoreInfo;
import com.nickimpact.impactor.ImpactorCore;
import com.nickimpact.impactor.configuration.ConfigKeys;
import com.nickimpact.impactor.gui.Clickable;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import lombok.Getter;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.text.Text;

import java.util.Map;
import java.util.function.BiConsumer;

/**
 * (Some note will appear here)
 *
 * @author NickImpact (Nick DeGruccio)
 */
public class UI {

	@Getter private Player player;

	private final SpongePlugin plugin;

	private Inventory inventory;

	private Map<Integer, Icon> slots;
	private BiConsumer<InteractInventoryEvent.Close, Player> closeAction;

	private UI(Player player, SpongePlugin plugin, Builder builder) {
		this.player = player;
		this.slots = Maps.newHashMap();
		this.inventory = Inventory.builder()
				.of(InventoryArchetypes.MENU_GRID)
				.property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(builder.title))
				.property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(builder.rows))
				.listener(ClickInventoryEvent.class, this::processClick)
				.listener(InteractInventoryEvent.Close.class, this::processClose)
				.build(plugin);
		this.plugin = plugin;

		if(builder.layout != null) {
			this.applyLayout(builder.layout);
		}

		this.closeAction = builder.closeAction;
	}

	public UI applyLayout(Layout layout) {
		slots.clear();
		for(int i = 0; i < inventory.capacity(); i++) {
			setSlot(i, layout.getIcon(i));
		}

		return this;
	}

	public void setSlot(int index, Icon icon) {
		GridInventory inv = this.inventory.query(GridInventory.class);
		Slot slot = inv.getSlot(SlotIndex.of(index)).orElseThrow(() -> new IllegalArgumentException("Invalid index: " + index));
		slot.set(icon.getDisplay());
		slots.put(index, icon);
	}

	public void open() {
		player.openInventory(this.inventory);
		if(this.debugEnabled()) {
			plugin.getConsole().ifPresent(console -> {
				console.sendMessages(
						Text.of(CoreInfo.DEBUG, "Opening Inventory for ", player.getName(), "..."),
						Text.of(CoreInfo.DEBUG, "  Title: ", this.inventory.getProperty(InventoryTitle.class, InventoryTitle.PROPERTY_NAME).get().getValue()),
						Text.of(CoreInfo.DEBUG, "  Provider: ", this.plugin.getPluginInfo().getName(), "-", this.plugin.getPluginInfo().getVersion())
				);
			});
		}
	}

	public void close() {
		player.closeInventory();
		if(this.debugEnabled()) {
			plugin.getConsole().ifPresent(console -> {
				console.sendMessages(
						Text.of(CoreInfo.DEBUG, "Closing Inventory for ", player.getName(), "..."),
						Text.of(CoreInfo.DEBUG, "  Title: ", this.inventory.getProperty(InventoryTitle.class, InventoryTitle.PROPERTY_NAME).get().getValue()),
						Text.of(CoreInfo.DEBUG, "  Provider: ", this.plugin.getPluginInfo().getName(), "-", this.plugin.getPluginInfo().getVersion())
				);
			});
		}
	}

	public void clear() {
		this.slots.clear();
		this.slots.forEach((slot, icon) -> {
			this.inventory.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(slot))).first().clear();
		});
	}

	public void clear(int... slots) {
		for(int slot : slots) {
			this.slots.remove(slot);
		}
	}

	private void processClick(ClickInventoryEvent event) {
		if(this.debugEnabled()) {
			plugin.getConsole().ifPresent(console -> {
				console.sendMessages(
						Text.of(CoreInfo.DEBUG, "Processing inventory click event for ", player.getName(), "..."),
						Text.of(CoreInfo.DEBUG, "  Title: ", this.inventory.getProperty(InventoryTitle.class, InventoryTitle.PROPERTY_NAME).get().getValue()),
						Text.of(CoreInfo.DEBUG, "  Provider: ", this.plugin.getPluginInfo().getName(), "-", this.plugin.getPluginInfo().getVersion())
				);
			});
		}
		event.setCancelled(true);
		event.getCause().first(Player.class).ifPresent(player -> {
			event.getTransactions().forEach(transaction -> {
				transaction.getSlot().getProperty(SlotIndex.class, "slotindex").ifPresent(slot -> {
					Icon icon = slots.get(slot.getValue());
					if(icon != null) {
						Sponge.getScheduler().createTaskBuilder()
								.execute(() -> icon.process(new Clickable(player, event)))
								.delayTicks(1)
								.submit(this.plugin);
					}
				});
			});
		});
	}

	private void processClose(InteractInventoryEvent.Close event) {
		if(closeAction != null) {
			if(this.debugEnabled()) {
				plugin.getConsole().ifPresent(console -> {
					console.sendMessages(
							Text.of(CoreInfo.DEBUG, "Processing inventory close event for ", player.getName(), "..."),
							Text.of(CoreInfo.DEBUG, "  Title: ", this.inventory.getProperty(InventoryTitle.class, InventoryTitle.PROPERTY_NAME).get().getValue()),
							Text.of(CoreInfo.DEBUG, "  Provider: ", this.plugin.getPluginInfo().getName(), "-", this.plugin.getPluginInfo().getVersion())
					);
				});
			}
			closeAction.accept(event, event.getCause().first(Player.class).orElse(null));
		}
	}

	private boolean debugEnabled() {
		return ImpactorCore.getInstance().getConfig().get(ConfigKeys.DEBUG_ENABLED) &&
				ImpactorCore.getInstance().getConfig().get(ConfigKeys.DEBUG_INVENTORY);
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private Text title = Text.EMPTY;
		private int rows = 6;
		private Layout layout;
		private BiConsumer<InteractInventoryEvent.Close, Player> closeAction;

		public Builder title(Text title) {
			this.title = title;
			return this;
		}

		public Builder layout(Layout layout) {
			this.layout = layout;
			return this;
		}

		public Builder closeAction(BiConsumer<InteractInventoryEvent.Close, Player> task) {
			this.closeAction = task;
			return this;
		}

		public UI build(Player player, SpongePlugin plugin) {
			return new UI(player, plugin, this);
		}
	}
}
