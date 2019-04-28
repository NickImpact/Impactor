package com.nickimpact.impactor.sponge.ui;

import com.google.common.collect.Maps;
import com.nickimpact.impactor.api.gui.*;
import com.nickimpact.impactor.api.plugin.ImpactorPlugin;
import com.nickimpact.impactor.sponge.SpongeImpactorPlugin;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SpongePage<U> implements Page<Player, U, SpongeUI, SpongeIcon> {

	private Player viewer;

	private SpongeUI view;

	private SpongeLayout layout;

	private int page;

	private ImpactorPlugin plugin;

	private List<U> contents;

	private Function<U, SpongeIcon> applier;

	private Text title;

	private InventoryDimension contentZone;

	private int rOffset;

	private int cOffset;

	private Map<PageIconType, PageIcon<ItemType>> pageIcons;

	private SpongePage(ImpactorPlugin plugin, SpongePageBuilder builder) {
		this.plugin = plugin;
		this.viewer = builder.viewer;
		this.pageIcons = builder.pageIcons;
		this.title = builder.title;
		this.page = 1;
		this.contentZone = builder.contentZone;
		this.rOffset = builder.rOffset;
		this.cOffset = builder.cOffset;
		this.layout = builder.view;
		this.view = this.forgeFromLayout(builder.view);
	}

	private SpongeUI forgeFromLayout(SpongeLayout layout) {
		SpongeLayout.SpongeLayoutBuilder updated = SpongeLayout.builder().from(layout);
		for(Map.Entry<PageIconType, PageIcon<ItemType>> entry : this.pageIcons.entrySet()) {
			ItemStack item = ItemStack.builder().itemType(entry.getValue().getRep()).build();
			item.offer(Keys.DISPLAY_NAME, TextSerializers.FORMATTING_CODE.deserialize(entry.getKey().getTitle().replaceAll("\\{\\{impactor_page_number}}", "" + page)));
			SpongeIcon icon = new SpongeIcon(item);
			icon.addListener(clickable -> {
				int capacity = this.contentZone.getColumns() * this.contentZone.getRows();
				this.page = entry.getKey().getUpdater().apply(this.page, this.contents.isEmpty() ? 1 : contents.size() % capacity == 0 ? contents.size() / capacity : contents.size() / capacity + 1);
				this.apply();
			});

			updated.slot(icon, entry.getValue().getSlot());
		}

		SpongeLayout modified = updated.build();
		return SpongeUI.builder()
				.title(this.title)
				.dimension(InventoryDimension.of(this.layout.getDimensions().getColumns(), this.layout.getDimensions().getRows()))
				.build()
				.define(modified);
	}

	@Override
	public Player getViewer() {
		return this.viewer;
	}

	@Override
	public SpongeUI getView() {
		return this.view;
	}

	@Override
	public Page<Player, U, SpongeUI, SpongeIcon> applier(Function<U, SpongeIcon> applier) {
		this.applier = applier;
		return this;
	}

	@Override
	public void define(List<U> contents) {
		this.contents = contents;
		this.apply();
	}

	@Override
	public void open() {
		this.view.open(this.viewer);
	}

	@Override
	public void close() {
		this.view.close(this.viewer);
	}

	@Override
	public void clean() {
		int index = cOffset + this.view.getDimension().getColumns() * rOffset;
		for(int r = 0; r < this.contentZone.getRows(); r++) {
			for(int c = 0; c < this.contentZone.getColumns(); c++) {
				int slot = index + c;
				this.view.clear(slot);
			}

			index += 9;
		}
	}

	@Override
	public void apply() {
		this.clean();

		int capacity = this.contentZone.getColumns() * this.contentZone.getRows();
		int pages = this.contents.isEmpty() ? 1 : contents.size() % capacity == 0 ? contents.size() / capacity : contents.size() / capacity + 1;

		if(pages < this.page) {
			this.page = pages;
		}

		List<U> viewable = this.contents.subList((this.page - 1) * capacity, this.page == pages ? this.contents.size() : this.page * capacity);
		List<SpongeIcon> translated = viewable.stream().map(obj -> this.applier.apply(obj)).collect(Collectors.toList());

		int index = cOffset + this.view.getDimension().getColumns() * rOffset;
		int r = 0;
		int cap = index + contentZone.getColumns() - 1 + 9 * (contentZone.getRows() - 1);
		for(SpongeIcon icon : translated) {
			if(index > cap) {
				break;
			}

			if(r == contentZone.getColumns()) {
				index += this.view.getDimension().getColumns() - contentZone.getColumns();
				r = 0;
			}

			this.view.setSlot(index, icon);

			index++;
			r++;
		}
	}

	public static SpongePageBuilder builder() {
		return new SpongePageBuilder();
	}

	public static class SpongePageBuilder {

		/** The player viewing the inventory */
		private Player viewer;

		/** The base view of all pages */
		private SpongeLayout view;

		/** The title of the representation */
		private Text title;

		/** Represents the area the contents can be displayed in the inventory */
		private InventoryDimension contentZone;

		/** Represents the row offset for the contentZone, if it is defined */
		private int rOffset;

		/** Represents the column offset for the contentZone, if it is defined */
		private int cOffset;

		/** Represents the actual buttons which will be in charge of updating pages */
		private Map<PageIconType, PageIcon<ItemType>> pageIcons = Maps.newHashMap();

		public SpongePageBuilder viewer(Player viewer) {
			this.viewer = viewer;
			return this;
		}

		public SpongePageBuilder view(SpongeLayout view) {
			this.view = view;
			return this;
		}

		public SpongePageBuilder title(Text title) {
			this.title = title;
			return this;
		}

		public SpongePageBuilder contentZone(InventoryDimension dimension) {
			this.contentZone = dimension;
			return this;
		}

		public SpongePageBuilder rOffset(int offset) {
			this.rOffset = offset;
			return this;
		}

		public SpongePageBuilder cOffset(int offset) {
			this.cOffset = offset;
			return this;
		}

		public SpongePageBuilder offsets(int offset) {
			this.rOffset = this.cOffset = offset;
			return this;
		}

		public SpongePageBuilder firstPage(ItemType type, int slot) {
			this.pageIcons.put(PageIconType.FIRST, new PageIcon<>(type, slot));
			return this;
		}

		public SpongePageBuilder previousPage(ItemType type, int slot) {
			this.pageIcons.put(PageIconType.PREV, new PageIcon<>(type, slot));
			return this;
		}

		public SpongePageBuilder currentPage(ItemType type, int slot) {
			this.pageIcons.put(PageIconType.CURRENT, new PageIcon<>(type, slot));
			return this;
		}

		public SpongePageBuilder nextPage(ItemType type, int slot) {
			this.pageIcons.put(PageIconType.NEXT, new PageIcon<>(type, slot));
			return this;
		}

		public SpongePageBuilder lastPage(ItemType type, int slot) {
			this.pageIcons.put(PageIconType.LAST, new PageIcon<>(type, slot));
			return this;
		}

		public <T> SpongePage<T> build() {
			return new SpongePage<>(SpongeImpactorPlugin.getInstance(), this);
		}
	}
}
