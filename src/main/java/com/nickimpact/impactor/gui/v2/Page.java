package com.nickimpact.impactor.gui.v2;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.AbstractInventoryProperty;
import org.spongepowered.api.item.inventory.property.InventoryCapacity;
import org.spongepowered.api.item.inventory.property.InventoryDimension;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Page {

	public static final Icon FIRST = Icon.ERROR;
	public static final Icon LAST = Icon.ERROR;
	public static final Icon NEXT = Icon.ERROR;
	public static final Icon PREV = Icon.ERROR;
	public static final Icon CURR = Icon.ERROR;

	private final List<UI> views = Lists.newArrayList();
	private final InventoryArchetype archetype;
	private final ImmutableList<InventoryProperty> properties;
	private final Layout layout;
	private final SpongePlugin plugin;

	public Page define(List<Icon> icons) {
		return this.define(icons, archetype.getProperty(InventoryDimension.class).orElse(InventoryDimension.of(9, 6)));
	}

	public Page define(List<Icon> icons, int rows, int cols) {
		return this.define(icons, InventoryDimension.of(rows, cols));
	}

	public Page define(List<Icon> icons, InventoryDimension dimension) {
		return this.define(icons, dimension, 0, 0);
	}

	public Page define(List<Icon> icons, InventoryDimension dimension, int rOffset, int cOffset) {
		views.clear();
		int capacity = dimension.getRows() * dimension.getColumns();
		int pages = icons.isEmpty() ? 1 : icons.size() / capacity + 1;
		for(int i = 1; i <= pages; i++) {
			UI.Builder builder = UI.builder().archetype(archetype);
			Layout.Builder page = Layout.builder().from(layout).page(icons.subList((i - 1) * capacity, i == pages ? icons.size() : i * capacity), dimension, rOffset, cOffset);
			properties.forEach(builder::property);
			views.add(builder.build(plugin).define(page.build()));
		}

		return this;
	}

	public void open(Player player, int page) {
		views.get((page > 1 ? Math.min(page, views.size() - 1) : 0)).open(player);
	}

	/**
	 * Creates a new builder for creating {@link Page}s.
	 */
	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private InventoryArchetype archetype = InventoryArchetypes.DOUBLE_CHEST;
		private List<InventoryProperty> properties = Lists.newArrayList();
		private Layout layout;

		/**
		 * Sets the archetype used for the backing {@link UI}s.
		 */
		public Builder archetype(InventoryArchetype archetype) {
			this.archetype = archetype;
			return this;
		}

		/**
		 * Adds a property used for the backing {@link UI}s.
		 */
		public Builder property(InventoryProperty property) {
			properties.add(property);
			return this;
		}

		/**
		 * Sets the layout used for the template of this view. It is expected
		 * that the layout contains empty slots.
		 */
		public Builder layout(Layout layout) {
			this.layout = layout;
			return this;
		}

		/**
		 * @return the created page
		 */
		public Page build(SpongePlugin plugin) {
			Preconditions.checkState(layout != null, "layout");
			return new Page(archetype, ImmutableList.copyOf(properties), layout, plugin);
		}

	}
}
