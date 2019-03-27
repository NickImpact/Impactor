package com.nickimpact.impactor.gui.v2;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.nickimpact.impactor.ImpactorCore;
import com.nickimpact.impactor.api.plugins.SpongePlugin;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Like its original counterpart, {@link Page}, a QueriedPage represents a UI indexed by a series
 * of pages. However, this system features an optimized indexing setup, updating and displaying
 * contents only if they appear in the current window. With this concept in mind, largely indexed
 * pages should remain optimal, as the max content that can be displayed in a window stays relatively
 * small, ensuring quick operation.
 *
 * Another advantage to this page system is highlighted by indexed clicks. Rather than opening a new
 * UI, the displayed contents are rather just updated and switched around, based on current
 * recorded index. As such, this view no longer suffers from opening new UIs every page click, or
 * during any click action that updates a page view. Rather, the user can apply their action,
 * and now have their mouse remained unphased.
 *
 * To streamline the effects of this page system, implementation should provide the contents to be
 * filled in on the pages, along with a translator, declared as {@link QueriedPage#applier}. This
 * translator will be used to update all incoming content entries which are currently able to
 * be viewed, and translate them into their {@link Icon} representation.
 *
 * @param <T> The type representation for the contents of the UI
 */
public class QueriedPage<T> {

	/** The player viewing the inventory */
	@Getter private Player viewer;

	/** The represented view of all pages */
	@Getter private UI view;

	/** The currently viewed page */
	@Getter private int page;

	/** The plugin this page system belongs to */
	@Getter private SpongePlugin plugin;

	/** A cache which holds and preserves the current state of the contents ready for translation */
	private List<T> contents;

	/** The application which applies the set of contents into the proper Icon representations */
	private Function<T, Icon> applier;

	/** The title of the representation */
	private Text title;

	/** Represents the area the contents can be displayed in the inventory */
	private InventoryDimension contentZone;

	/** Represents the row offset for the contentZone, if it is defined */
	private int rOffset;

	/** Represents the column offset for the contentZone, if it is defined */
	private int cOffset;

	/** Represents the actual buttons which will be in charge of updating pages */
	private Map<PageIconType, PageIcon> pageIcons;

	private QueriedPage(QueriedPageBuilder builder, SpongePlugin plugin) {
		this.plugin = plugin;
		this.viewer = builder.viewer;
		this.pageIcons = builder.pageIcons;
		this.title = builder.title;
		this.view = this.forgeFromLayout(builder.view);
		this.page = 1;
		this.contentZone = builder.contentZone;
		this.rOffset = builder.rOffset;
		this.cOffset = builder.cOffset;
	}

	private UI forgeFromLayout(Layout layout) {
		Layout.Builder updated = Layout.builder().from(layout);
		for(Map.Entry<PageIconType, PageIcon> entry : this.pageIcons.entrySet()) {
			ItemStack item = ItemStack.builder().itemType(entry.getValue().getRep()).build();
			item.offer(Keys.DISPLAY_NAME, TextSerializers.FORMATTING_CODE.deserialize(entry.getKey().title.replaceAll("\\{\\{impactor_page_number}}", "" + page)));
			Icon icon = Icon.from(item);
			icon.addListener(clickable -> {
				int capacity = this.contentZone.getColumns() * this.contentZone.getRows();
				this.page = entry.getKey().getUpdater().apply(this.page, this.contents.isEmpty() ? 1 : contents.size() % capacity == 0 ? contents.size() / capacity : contents.size() / capacity + 1);
				this.apply();
			});

			updated.slot(icon, entry.getValue().getSlot());
		}

		Layout modified = updated.build();
		return UI.builder()
				.title(this.title)
				.dimension(modified.getDimension())
				.build(this.plugin)
				.define(modified);
	}

	public QueriedPage<T> applier(Function<T, Icon> applier) {
		this.applier = applier;
		return this;
	}

	/**
	 * Updates the contents of the UI to match the passed in value. From there, a general update will
	 * take place which will update the current view.
	 *
	 * @param contents The contents that'll be used to fill the pages
	 */
	public void define(List<T> contents) {
		this.contents = contents;
		this.apply();
	}

	/**
	 * Opens the Inventory view for the page set.
	 *
	 * NOTE: This should really only be used for the first interaction of the Inventory.
	 * To properly handle UI updates, check {@link #apply()}
	 */
	public void open() {
		this.view.open(this.viewer);
	}

	private void clean() {
		int index = cOffset + this.view.getDimension().getColumns() * rOffset;
		for(int r = 0; r < this.contentZone.getRows(); r++) {
			for(int c = 0; c < this.contentZone.getColumns(); c++) {
				int slot = index + c;
				this.view.clear(slot);
			}

			index += 9;
		}
	}

	/**
	 * With the current page declared, strategically update the UI based on current page number,
	 * focusing ONLY on the contents that can actually be currently viewed.
	 */
	public void apply() {
		this.clean();

		int capacity = this.contentZone.getColumns() * this.contentZone.getRows();
		int pages = this.contents.isEmpty() ? 1 : contents.size() % capacity == 0 ? contents.size() / capacity : contents.size() / capacity + 1;

		if(pages < this.page) {
			this.page = pages;
		}

		List<T> viewable = this.contents.subList((this.page - 1) * capacity, this.page == pages ? this.contents.size() : this.page * capacity);
		List<Icon> translated = viewable.stream().map(obj -> this.applier.apply(obj)).collect(Collectors.toList());

		int index = cOffset + this.view.getDimension().getColumns() * rOffset;
		int r = 0;
		int cap = index + contentZone.getColumns() - 1 + 9 * (contentZone.getRows() - 1);
		for(Icon icon : translated) {
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

	public static QueriedPageBuilder builder() {
		return new QueriedPageBuilder();
	}

	public static class QueriedPageBuilder {

		/** The player viewing the inventory */
		private Player viewer;

		/** The base view of all pages */
		private Layout view;

		/** The title of the representation */
		private Text title;

		/** Represents the area the contents can be displayed in the inventory */
		private InventoryDimension contentZone;

		/** Represents the row offset for the contentZone, if it is defined */
		private int rOffset;

		/** Represents the column offset for the contentZone, if it is defined */
		private int cOffset;

		/** Represents the actual buttons which will be in charge of updating pages */
		private Map<PageIconType, PageIcon> pageIcons = Maps.newHashMap();

		public QueriedPageBuilder viewer(Player viewer) {
			this.viewer = viewer;
			return this;
		}

		public QueriedPageBuilder view(Layout view) {
			this.view = view;
			return this;
		}

		public QueriedPageBuilder title(Text title) {
			this.title = title;
			return this;
		}

		public QueriedPageBuilder contentZone(InventoryDimension dimension) {
			this.contentZone = dimension;
			return this;
		}

		public QueriedPageBuilder rOffset(int offset) {
			this.rOffset = offset;
			return this;
		}

		public QueriedPageBuilder cOffset(int offset) {
			this.cOffset = offset;
			return this;
		}

		public QueriedPageBuilder offsets(int offset) {
			this.rOffset = this.cOffset = offset;
			return this;
		}

		public QueriedPageBuilder firstPage(ItemType type, int slot) {
			this.pageIcons.put(PageIconType.FIRST, new PageIcon(type, slot));
			return this;
		}

		public QueriedPageBuilder previousPage(ItemType type, int slot) {
			this.pageIcons.put(PageIconType.PREV, new PageIcon(type, slot));
			return this;
		}

		public QueriedPageBuilder currentPage(ItemType type, int slot) {
			this.pageIcons.put(PageIconType.CURRENT, new PageIcon(type, slot));
			return this;
		}

		public QueriedPageBuilder nextPage(ItemType type, int slot) {
			this.pageIcons.put(PageIconType.NEXT, new PageIcon(type, slot));
			return this;
		}

		public QueriedPageBuilder lastPage(ItemType type, int slot) {
			this.pageIcons.put(PageIconType.LAST, new PageIcon(type, slot));
			return this;
		}

		public <T> QueriedPage<T> build(SpongePlugin plugin) {
			return new QueriedPage<>(this, plugin);
		}
	}

	@Getter
	@RequiredArgsConstructor
	private static class PageIcon {
		private final ItemType rep;
		private final int slot;
	}

	@Getter
	@RequiredArgsConstructor
	private enum PageIconType {
		FIRST("&eFirst Page", (in, total) -> 1),
		PREV("&ePrevious Page", (in, total) -> in == 1 ? total : in - 1),
		CURRENT("&eCurrent Page &7(&a{{impactor_page_number}}&7)", (in, total) -> in),
		NEXT("&eNext Page", (in, total) -> in.intValue() == total.intValue() ? 1 : in + 1),
		LAST("&eLast Page", (in, total) -> total);

		private final String title;
		private final BiFunction<Integer, Integer, Integer> updater;
	}
}
