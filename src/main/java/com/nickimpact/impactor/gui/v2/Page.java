package com.nickimpact.impactor.gui.v2;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.nickimpact.impactor.ImpactorCore;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import com.nickimpact.impactor.configuration.MsgConfigKeys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class Page {

	public final Map<PageIconType, PageIcon> pageIcons = Maps.newHashMap();

	@Getter private final List<UI> views = Lists.newArrayList();
	private final InventoryArchetype archetype;
	private final ImmutableList<InventoryProperty> properties;
	private final Layout layout;
	private final SpongePlugin plugin;

	private Page(Builder builder, SpongePlugin plugin) {
		this.archetype = builder.archetype;
		this.properties = ImmutableList.copyOf(builder.properties);
		this.layout = builder.layout;
		this.plugin = plugin;

		if(builder.first != null) {
			pageIcons.put(PageIconType.FIRST, builder.first);
		}

		if(builder.prev != null) {
			pageIcons.put(PageIconType.PREV, builder.prev);
		}

		if(builder.curr != null) {
			pageIcons.put(PageIconType.CURRENT, builder.curr);
		}

		if(builder.next != null) {
			pageIcons.put(PageIconType.NEXT, builder.next);
		}

		if(builder.last != null) {
			pageIcons.put(PageIconType.LAST, builder.last);
		}
	}

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
		int pages = icons.isEmpty() ? 1 : icons.size() % capacity == 0 ? icons.size() / capacity : icons.size() / capacity + 1;
		for(int i = 1; i <= pages; i++) {
			UI.Builder builder = UI.builder().archetype(archetype);
			Layout.Builder page = Layout.builder().from(layout).page(icons.subList((i - 1) * capacity, i == pages ? icons.size() : i * capacity), dimension, rOffset, cOffset);
			properties.forEach(builder::property);

			// Update Page Icons
			for(Map.Entry<PageIconType, PageIcon> pgt : pageIcons.entrySet()) {
				Icon icon = Icon.from(pgt.getValue().rep.getDisplay());
				int slot = pgt.getValue().slot;
				switch (pgt.getKey()) {
					case FIRST:
						icon = this.update(
								icon,
								TextSerializers.FORMATTING_CODE.deserialize(
										ImpactorCore.getInstance().getMsgConfig().get(MsgConfigKeys.PAGES_FIRST).replaceAll("\\{\\{page}}", "1")
								),
								i,
								1
						);
						break;
					case PREV:
						icon = this.update(
								icon,
								TextSerializers.FORMATTING_CODE.deserialize(
										ImpactorCore.getInstance().getMsgConfig().get(MsgConfigKeys.PAGES_PREV).replaceAll("\\{\\{page}}", "" + (i == 1 ? pages : i - 1))
								),
								i,
								i == 1 ? pages : i - 1
						);
						break;
					case CURRENT:
						icon = this.update(
								icon,
								TextSerializers.FORMATTING_CODE.deserialize(
										ImpactorCore.getInstance().getMsgConfig().get(MsgConfigKeys.PAGES_CURR).replaceAll("\\{\\{page}}", "" + i)
								),
								i,
								i
						);
						break;
					case NEXT:
						icon = this.update(
								icon,
								TextSerializers.FORMATTING_CODE.deserialize(
										ImpactorCore.getInstance().getMsgConfig().get(MsgConfigKeys.PAGES_NEXT).replaceAll("\\{\\{page}}", "" + (i == pages ? 1 : i + 1))
								),
								i,
								i == pages ? 1 : i + 1
						);
						break;
					case LAST:
						icon = this.update(
								icon,
								TextSerializers.FORMATTING_CODE.deserialize(
										ImpactorCore.getInstance().getMsgConfig().get(MsgConfigKeys.PAGES_FIRST).replaceAll("\\{\\{page}}", "" + pages)
								),
								i,
								pages
						);
						break;
				}

				page.slot(icon, slot);
			}

			views.add(builder.build(plugin).define(page.build()));
		}

		return this;
	}

	public Page update(List<Icon> icons, InventoryDimension dimension, int rOffset, int cOffset) {
		return this.define(icons, dimension, rOffset, cOffset);
	}

	private Icon update(Icon icon, Text name, int page, int target) {
		icon.getDisplay().offer(Keys.DISPLAY_NAME, name);
		icon.getDisplay().setQuantity(target);

		if(page != target) {
			icon.addListener(clickable -> {
				this.open(clickable.getPlayer(), target);
			});
		}

		return icon;
	}

	public void open(Player player, int page) {
		views.get((page > 1 ? Math.min(page - 1, views.size() - 1) : 0)).open(player);
	}

	public void close(Player player) {
		player.closeInventory();
	}

	public void apply(Icon icon, int slot) {
		for(UI ui : views) {
			ui.setSlot(slot, icon);
		}
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

		private PageIcon first;
		private PageIcon prev;
		private PageIcon curr;
		private PageIcon next;
		private PageIcon last;

		private BiConsumer<InteractInventoryEvent.Close, Player> closeAction;

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

		public Builder first(Icon icon, int slot) {
			this.first = new PageIcon(icon, slot);
			return this;
		}

		public Builder previous(Icon icon, int slot) {
			this.prev = new PageIcon(icon, slot);
			return this;
		}

		public Builder current(Icon icon, int slot) {
			this.curr = new PageIcon(icon, slot);
			return this;
		}

		public Builder next(Icon icon, int slot) {
			this.next = new PageIcon(icon, slot);
			return this;
		}

		public Builder last(Icon icon, int slot) {
			this.last = new PageIcon(icon, slot);
			return this;
		}

		/**
		 * @return the created page
		 */
		public Page build(SpongePlugin plugin) {
			Preconditions.checkState(layout != null, "layout");
			return new Page(this, plugin);
		}

	}

	@Getter
	@RequiredArgsConstructor
	private static class PageIcon {
		private final Icon rep;
		private final int slot;
	}

	private enum PageIconType {
		FIRST,
		PREV,
		CURRENT,
		NEXT,
		LAST
	}
}
