package com.nickimpact.impactor.sponge.ui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.nickimpact.impactor.api.gui.*;
import com.nickimpact.impactor.api.plugin.ImpactorPlugin;
import com.nickimpact.impactor.sponge.SpongeImpactorPlugin;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SpongeUI implements UI<Player, ClickInventoryEvent, InteractInventoryEvent.Close, SpongeIcon> {

	private final ImpactorPlugin plugin;

	private Inventory inventory;
	private InventoryDimensions dimensions;

	private SpongeLayout layout;

	private Map<Integer, SpongeIcon> slots;
	private List<BiConsumer<Player, ClickInventoryEvent>> additionals = Lists.newArrayList();
	private List<Consumer<InteractInventoryEvent.Close>> closeAdditionals = Lists.newArrayList();

	private SpongeUI(ImpactorPlugin plugin, SpongeUIBuilder builder) {
		this.plugin = plugin;
		this.slots = Maps.newHashMap();
		this.inventory = builder.builder
				.listener(ClickInventoryEvent.class, this::processClick)
				.build(plugin);

		this.dimensions = new InventoryDimensions(builder.dimension.getRows(), builder.dimension.getColumns());
	}

	@Override
	public ImpactorPlugin getPlugin() {
		return this.plugin;
	}

	@Override
	public SpongeLayout getLayout() {
		return this.layout;
	}

	@Override
	public SpongeUI define(Layout<SpongeIcon> layout) {
		this.layout = (SpongeLayout) layout;
		this.slots.clear();
		for(int i = 0; i < inventory.capacity(); i++) {
			final int slot = i;
			layout.getIcon(i).ifPresent(icon -> this.setSlot(slot, icon));
		}

		return this;
	}

	@Override
	public Optional<SpongeIcon> getIcon(int slot) {
		return Optional.ofNullable(this.slots.get(slot));
	}

	@Override
	public void setSlot(int slot, SpongeIcon icon) {
		this.inventory.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(slot))).first().set(icon.getDisplay());
		this.slots.put(slot, icon);
	}

	@Override
	public void open(Player player) {
		player.openInventory(this.inventory);
	}

	@Override
	public void close(Player player) {
		player.closeInventory();
	}

	@Override
	public void clear() {
		this.slots.clear();
		this.slots.forEach((slot, icon) -> this.inventory.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(slot))).first().clear());
	}

	@Override
	public void clear(int... slots) {
		for(int slot : slots) {
			this.slots.remove(slot);
			this.inventory.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(slot))).first().set(ItemStack.empty());
		}
	}

	@Override
	public UI attachListener(BiConsumer<Player, ClickInventoryEvent> listener) {
		this.additionals.add(listener);
		return this;
	}

	@Override
	public UI attachCloseListener(Consumer<InteractInventoryEvent.Close> listener) {
		this.closeAdditionals.add(listener);
		return this;
	}

	@Override
	public InventoryDimensions getDimension() {
		return this.dimensions;
	}

	private void processClick(ClickInventoryEvent event) {
		event.setCancelled(true);
		event.getCause().first(Player.class).ifPresent(pl -> {
			event.getTransactions().forEach(transaction -> {
				transaction.getSlot().getProperty(SlotIndex.class, "slotindex").ifPresent(slot -> {
					SpongeIcon icon = slots.get(slot.getValue());
					if(icon != null) {
						Sponge.getScheduler().createTaskBuilder()
								.execute(() -> {
									icon.process(new SpongeClickable(pl, event));
								})
								.delayTicks(1)
								.submit(this.plugin);
					}

					for(BiConsumer<Player, ClickInventoryEvent> extra : additionals) {
						Sponge.getScheduler().createTaskBuilder().execute(() -> extra.accept(pl, event)).delayTicks(1).submit(this.plugin);
					}
				});
			});
		});
	}

	public static SpongeUIBuilder builder() {
		return new SpongeUIBuilder();
	}

	public static class SpongeUIBuilder {

		private Inventory.Builder builder = Inventory.builder();
		private InventoryArchetype archetype;
		private InventoryDimension dimension;

		public SpongeUIBuilder archetype(InventoryArchetype type) {
			this.archetype = type;
			this.builder.of(type);
			return this;
		}

		public SpongeUIBuilder dimension(InventoryDimension dimension) {
			this.dimension = dimension;
			this.property(dimension);
			return this;
		}

		public SpongeUIBuilder property(InventoryProperty property) {
			if(property instanceof InventoryDimension && dimension == null) {
				this.dimension = (InventoryDimension) property;
			}

			this.builder.property(property);
			return this;
		}

		public SpongeUIBuilder title(Text title) {
			return this.property(InventoryTitle.of(title));
		}

		public SpongeUI build() {
			return new SpongeUI(SpongeImpactorPlugin.getInstance(), this);
		}
	}
}
