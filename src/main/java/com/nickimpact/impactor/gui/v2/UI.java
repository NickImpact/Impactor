package com.nickimpact.impactor.gui.v2;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.nickimpact.impactor.ImpactorCore;
import com.nickimpact.impactor.api.logger.Logger;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import com.nickimpact.impactor.configuration.ConfigKeys;
import com.nickimpact.impactor.gui.Clickable;
import lombok.Getter;
import lombok.Setter;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.*;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * (Some note will appear here)
 *
 * @author NickImpact (Nick DeGruccio)
 */
public class UI {

	private final SpongePlugin plugin;

	private Inventory inventory;
	@Getter private InventoryArchetype archetype;
	@Getter private InventoryDimension dimension;

	private Map<Integer, Icon> slots;
	@Setter private BiConsumer<InteractInventoryEvent.Open, Player> openAction;
	@Setter private BiConsumer<InteractInventoryEvent.Close, Player> closeAction;
	private List<BiConsumer<ClickInventoryEvent, Player>> additionals = Lists.newArrayList();

	private UI(SpongePlugin plugin, Builder builder) {
		this.slots = Maps.newHashMap();
		this.archetype = builder.archetype;
		this.dimension = builder.dimension;
		this.inventory = builder.builder
				.listener(ClickInventoryEvent.class, this::processClick)
				.listener(InteractInventoryEvent.Open.class, this::processOpen)
				.listener(InteractInventoryEvent.Close.class, this::processClose)
				.build(plugin);
		this.plugin = plugin;

		this.openAction = builder.openAction;
		this.closeAction = builder.closeAction;
	}

	public UI define(Layout layout) {
		slots.clear();
		for(int i = 0; i < inventory.capacity(); i++) {
			setSlot(i, layout.getIcon(i));
		}

		return this;
	}

	public Icon getSlot(int index) {
		return slots.get(index);
	}

	public void setSlot(int index, Icon icon) {
		inventory.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(index))).first().set(icon.getDisplay());
		slots.put(index, icon);
	}

	public void open(Player player) {
		player.openInventory(this.inventory);
		if(this.debugEnabled()) {
			ImpactorCore.getInstance().getLogger().send(Logger.Prefixes.DEBUG, Lists.newArrayList(
					Text.of("Opening Inventory for ", player.getName(), "..."),
					Text.of("  Title: ", this.inventory.getProperty(InventoryTitle.class, InventoryTitle.PROPERTY_NAME).get().getValue()),
					Text.of("  Provider: ", this.plugin.getPluginInfo().getName(), "-", this.plugin.getPluginInfo().getVersion())
			));
		}
	}

	public void close(Player player) {
		player.closeInventory();
		if(this.debugEnabled()) {
			ImpactorCore.getInstance().getLogger().send(Logger.Prefixes.DEBUG, Lists.newArrayList(
						Text.of("Closing Inventory for ", player.getName(), "..."),
						Text.of("  Title: ", this.inventory.getProperty(InventoryTitle.class, InventoryTitle.PROPERTY_NAME).get().getValue()),
						Text.of("  Provider: ", this.plugin.getPluginInfo().getName(), "-", this.plugin.getPluginInfo().getVersion())
			));
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

	public UI attachExtraListener(BiConsumer<ClickInventoryEvent, Player> extra) {
		this.additionals.add(extra);
		return this;
	}

	private void processClick(ClickInventoryEvent event) {
		if(this.debugEnabled()) {
			Player player = event.getCause().first(Player.class).orElse(null);
			ImpactorCore.getInstance().getLogger().send(Logger.Prefixes.DEBUG, Lists.newArrayList(
						Text.of("Processing inventory click event for ", player == null ? "Unknown" : player.getName(), "..."),
						Text.of("  Title: ", this.inventory.getProperty(InventoryTitle.class, InventoryTitle.PROPERTY_NAME).get().getValue()),
						Text.of("  Provider: ", this.plugin.getPluginInfo().getName(), "-", this.plugin.getPluginInfo().getVersion())
			));
		}
		event.setCancelled(true);
		event.getCause().first(Player.class).ifPresent(pl -> {
			event.getTransactions().forEach(transaction -> {
				transaction.getSlot().getProperty(SlotIndex.class, "slotindex").ifPresent(slot -> {
					Icon icon = slots.get(slot.getValue());
					if(icon != null) {
						Sponge.getScheduler().createTaskBuilder()
								.execute(() -> {
									icon.process(new Clickable(pl, event));
								})
								.delayTicks(1)
								.submit(this.plugin);
					}

					for(BiConsumer<ClickInventoryEvent, Player> extra : additionals) {
						Sponge.getScheduler().createTaskBuilder().execute(() -> extra.accept(event, pl)).delayTicks(1).submit(this.plugin);
					}
				});
			});
		});
	}

	private void processOpen(InteractInventoryEvent.Open event) {
		if(openAction != null) {
			if(this.debugEnabled()) {
				Player player = event.getCause().first(Player.class).orElse(null);
				ImpactorCore.getInstance().getLogger().send(Logger.Prefixes.DEBUG, Lists.newArrayList(
						Text.of("Processing inventory open event for ", player == null ? "Unknown" : player.getName(), "..."),
						Text.of("  Title: ", this.inventory.getProperty(InventoryTitle.class, InventoryTitle.PROPERTY_NAME).get().getValue()),
						Text.of("  Provider: ", this.plugin.getPluginInfo().getName(), "-", this.plugin.getPluginInfo().getVersion())
				));
			}
			openAction.accept(event, event.getCause().first(Player.class).orElse(null));
		}
	}

	private void processClose(InteractInventoryEvent.Close event) {
		if(closeAction != null) {
			if(this.debugEnabled()) {
				Player player = event.getCause().first(Player.class).orElse(null);
				ImpactorCore.getInstance().getLogger().send(Logger.Prefixes.DEBUG, Lists.newArrayList(
							Text.of("Processing inventory close event for ", player == null ? "Unknown" : player.getName(), "..."),
							Text.of("  Title: ", this.inventory.getProperty(InventoryTitle.class, InventoryTitle.PROPERTY_NAME).get().getValue()),
							Text.of("  Provider: ", this.plugin.getPluginInfo().getName(), "-", this.plugin.getPluginInfo().getVersion())
				));
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

		private Inventory.Builder builder = Inventory.builder();
		private InventoryArchetype archetype;
		private InventoryDimension dimension;
		private BiConsumer<InteractInventoryEvent.Open, Player> openAction;
		private BiConsumer<InteractInventoryEvent.Close, Player> closeAction;

		public Builder archetype(InventoryArchetype type) {
			this.archetype = type;
			this.builder.of(type);
			return this;
		}

		public Builder dimension(InventoryDimension dimension) {
			this.dimension = dimension;
			this.property(dimension);
			return this;
		}

		public Builder property(InventoryProperty property) {
			if(property instanceof InventoryDimension && dimension == null) {
				this.dimension = (InventoryDimension) property;
			}

			this.builder.property(property);
			return this;
		}

		public Builder title(Text title) {
			return this.property(InventoryTitle.of(title));
		}

		public Builder openAction(BiConsumer<InteractInventoryEvent.Open, Player> task) {
			this.openAction = task;
			return this;
		}

		public Builder closeAction(BiConsumer<InteractInventoryEvent.Close, Player> task) {
			this.closeAction = task;
			return this;
		}

		public UI build(SpongePlugin plugin) {
			return new UI(plugin, this);
		}
	}
}
