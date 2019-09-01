package com.nickimpact.impactor.spigot.ui;

import com.google.common.collect.Maps;
import com.nickimpact.impactor.api.gui.InventoryDimensions;
import com.nickimpact.impactor.api.gui.Page;
import com.nickimpact.impactor.api.plugin.ImpactorPlugin;
import com.nickimpact.impactor.spigot.SpigotImpactorPlugin;
import com.nickimpact.impactor.spigot.utils.ItemStackUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SpigotPage<U> implements Page<Player, U, SpigotUI, SpigotIcon> {

	private Player viewer;

	private SpigotUI view;

	private SpigotLayout layout;

	private int page;

	private ImpactorPlugin plugin;

	private List<U> contents;

	private Function<U, SpigotIcon> applier;

	private String title;

	private InventoryDimensions dimensions;

	private int rOffset;

	private int cOffset;

	private Map<PageIconType, PageIcon<Material>> pageIcons;

	private SpigotPage(ImpactorPlugin plugin, SpigotPageBuilder builder) {
		this.plugin = plugin;
		this.viewer = builder.viewer;
		this.pageIcons = builder.pageIcons;
		this.title = builder.title;
		this.page = 1;
		this.dimensions = builder.contentZone;
		this.rOffset = builder.rOffset;
		this.cOffset = builder.cOffset;
		this.layout = builder.view;
		this.view = this.forgeFromLayout(builder.view);
	}

	private SpigotUI forgeFromLayout(SpigotLayout layout) {
		SpigotLayout.SpigotLayoutBuilder updated = SpigotLayout.builder().from(layout);
		for(Map.Entry<PageIconType, PageIcon<Material>> entry : this.pageIcons.entrySet()) {
			ItemStack item = new ItemStack(entry.getValue().getRep());
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', entry.getKey().getTitle().replaceAll("\\{\\{impactor_page_number}}", "" + page)));
			item.setItemMeta(meta);

			SpigotIcon icon = new SpigotIcon(item);
			icon.addListener(clickable -> {
				if(this.contents != null) {
					int capacity = this.dimensions.getColumns() * this.dimensions.getRows();
					this.page = entry.getKey().getUpdater().apply(this.page, this.contents.isEmpty() ? 1 : contents.size() % capacity == 0 ? contents.size() / capacity : contents.size() / capacity + 1);
					this.apply();
				}
			});

			updated.slot(icon, entry.getValue().getSlot());
		}

		SpigotLayout modified = updated.build();
		return SpigotUI.builder()
				.title(this.title)
				.size(this.layout.getDimensions().getColumns() * this.layout.getDimensions().getRows())
				.build()
				.define(modified);
	}

	@Override
	public Player getViewer() {
		return this.viewer;
	}

	public SpigotUI getView() {
		return this.view;
	}

	@Override
	public Page<Player, U, SpigotUI, SpigotIcon> applier(Function<U, SpigotIcon> applier) {
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
		for(int r = 0; r < this.dimensions.getRows(); r++) {
			for(int c = 0; c < this.dimensions.getColumns(); c++) {
				int slot = index + c;
				this.view.clear(slot);
			}

			index += 9;
		}
	}

	@Override
	public void apply() {
		this.clean();

		int capacity = this.dimensions.getColumns() * this.dimensions.getRows();
		int pages = this.contents.isEmpty() ? 1 : contents.size() % capacity == 0 ? contents.size() / capacity : contents.size() / capacity + 1;

		if(pages < this.page) {
			this.page = pages;
		}

		List<U> viewable = this.contents.subList((this.page - 1) * capacity, this.page == pages ? this.contents.size() : this.page * capacity);
		List<SpigotIcon> translated = viewable.stream().map(obj -> this.applier.apply(obj)).collect(Collectors.toList());

		int index = cOffset + this.view.getDimension().getColumns() * rOffset;
		int r = 0;
		int cap = index + dimensions.getColumns() - 1 + 9 * (dimensions.getRows() - 1);
		for(SpigotIcon icon : translated) {
			if(index > cap) {
				break;
			}

			if(r == dimensions.getColumns()) {
				index += this.view.getDimension().getColumns() - dimensions.getColumns();
				r = 0;
			}

			this.view.setSlot(index, icon);

			index++;
			r++;
		}

		Material rep = this.pageIcons.get(PageIconType.CURRENT).getRep();
		ItemStack c = ItemStackUtils.itemBuilder()
				.material(rep)
				.name("&eCurrent Page &7(&a" + this.page + "&7)")
				.build();
		this.view.setSlot(this.pageIcons.get(PageIconType.CURRENT).getSlot(), new SpigotIcon(c));
	}

	public static SpigotPageBuilder builder() {
		return new SpigotPageBuilder();
	}

	public static class SpigotPageBuilder {
		/** The player viewing the inventory */
		private Player viewer;

		/** The base view of all pages */
		private SpigotLayout view;

		/** The title of the representation */
		private String title;

		/** Represents the area the contents can be displayed in the inventory */
		private InventoryDimensions contentZone;

		/** Represents the row offset for the contentZone, if it is defined */
		private int rOffset;

		/** Represents the column offset for the contentZone, if it is defined */
		private int cOffset;

		/** Represents the actual buttons which will be in charge of updating pages */
		private Map<PageIconType, PageIcon<Material>> pageIcons = Maps.newHashMap();

		public SpigotPageBuilder viewer(Player viewer) {
			this.viewer = viewer;
			return this;
		}

		public SpigotPageBuilder view(SpigotLayout view) {
			this.view = view;
			return this;
		}

		public SpigotPageBuilder title(String title) {
			this.title = title;
			return this;
		}

		public SpigotPageBuilder contentZone(InventoryDimensions dimension) {
			this.contentZone = dimension;
			return this;
		}

		public SpigotPageBuilder rOffset(int offset) {
			this.rOffset = offset;
			return this;
		}

		public SpigotPageBuilder cOffset(int offset) {
			this.cOffset = offset;
			return this;
		}

		public SpigotPageBuilder offsets(int offset) {
			this.rOffset = this.cOffset = offset;
			return this;
		}

		public SpigotPageBuilder firstPage(Material type, int slot) {
			this.pageIcons.put(PageIconType.FIRST, new PageIcon<>(type, slot));
			return this;
		}

		public SpigotPageBuilder previousPage(Material type, int slot) {
			this.pageIcons.put(PageIconType.PREV, new PageIcon<>(type, slot));
			return this;
		}

		public SpigotPageBuilder currentPage(Material type, int slot) {
			this.pageIcons.put(PageIconType.CURRENT, new PageIcon<>(type, slot));
			return this;
		}

		public SpigotPageBuilder nextPage(Material type, int slot) {
			this.pageIcons.put(PageIconType.NEXT, new PageIcon<>(type, slot));
			return this;
		}

		public SpigotPageBuilder lastPage(Material type, int slot) {
			this.pageIcons.put(PageIconType.LAST, new PageIcon<>(type, slot));
			return this;
		}

		public <T> SpigotPage<T> build() {
			return new SpigotPage<>(SpigotImpactorPlugin.getInstance(), this);
		}
	}
}
