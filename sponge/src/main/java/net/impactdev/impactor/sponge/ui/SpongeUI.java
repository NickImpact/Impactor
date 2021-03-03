package net.impactdev.impactor.sponge.ui;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import net.impactdev.impactor.api.gui.InventoryDimensions;
import net.impactdev.impactor.api.gui.Layout;
import net.impactdev.impactor.api.gui.UI;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import net.impactdev.impactor.sponge.SpongeImpactorPlugin;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
import org.spongepowered.api.text.format.TextColors;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
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

	private Multimap<UUID, TimeMap> cooldowns = ArrayListMultimap.create();

	private SpongeUI(ImpactorPlugin plugin, SpongeUIBuilder builder) {
		this.plugin = plugin;
		this.slots = Maps.newHashMap();
		this.inventory = builder.builder
				.listener(ClickInventoryEvent.class, this::processClick)
				.listener(InteractInventoryEvent.Close.class, this::processClose)
				.build(plugin);

		this.dimensions = new InventoryDimensions(builder.dimension.getColumns(), builder.dimension.getRows());
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
		this.slots.forEach((slot, icon) -> this.inventory.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(slot))).first().clear());
		this.slots.clear();
	}

	@Override
	public void clear(int... slots) {
		for(int slot : slots) {
			this.slots.remove(slot);
			this.inventory.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(slot))).first().set(ItemStack.empty());
		}
	}

	@Override
	public UI<?, ?, ?, ?> attachListener(BiConsumer<Player, ClickInventoryEvent> listener) {
		this.additionals.add(listener);
		return this;
	}

	@Override
	public UI<?, ?, ?, ?> attachCloseListener(Consumer<InteractInventoryEvent.Close> listener) {
		this.closeAdditionals.add(listener);
		return this;
	}

	@Override
	public InventoryDimensions getDimension() {
		return this.dimensions;
	}

	@SuppressWarnings("ConstantConditions")
	private void processClick(ClickInventoryEvent event) {
		event.setCancelled(true);
		event.getCause().first(Player.class).ifPresent(pl -> {
			event.getTransactions().forEach(transaction -> {
				transaction.getSlot().getProperty(SlotIndex.class, "slotindex").ifPresent(slot -> {
					SpongeIcon icon = slots.get(slot.getValue());
					if(icon != null) {
						Optional<TimeMap> cache = this.cooldowns.get(pl.getUniqueId())
								.stream()
								.filter(x -> x.getSlot() == slot.getValue())
								.findAny();

						boolean operate = true;
						if(cache.isPresent()) {
							if(cache.get().getTime().plusMillis(500).isAfter(Instant.now())) {
								operate = false;
							}
						}

						if(operate) {
							cache.ifPresent(timeMap -> this.cooldowns.remove(pl.getUniqueId(), timeMap));
							this.cooldowns.put(pl.getUniqueId(), new TimeMap(slot.getValue(), Instant.now()));
							Sponge.getScheduler().createTaskBuilder()
									.execute(() -> {
										icon.process(new SpongeClickable(pl, event));
									})
									.delayTicks(1)
									.submit(this.plugin);
						} else {
							pl.sendMessage(Text.of(TextColors.RED, "Please wait to click this icon again!"));
						}
					}

					for(BiConsumer<Player, ClickInventoryEvent> extra : additionals) {
						Sponge.getScheduler().createTaskBuilder().execute(() -> extra.accept(pl, event)).delayTicks(1).submit(this.plugin);
					}
				});
			});
		});
	}

	private void processClose(InteractInventoryEvent.Close event) {
		for(Consumer<InteractInventoryEvent.Close> consumer : this.closeAdditionals) {
			consumer.accept(event);
		}
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

	@Getter
	@EqualsAndHashCode
	@RequiredArgsConstructor
	private static class TimeMap {
		private final int slot;
		private final Instant time;
	}
}
