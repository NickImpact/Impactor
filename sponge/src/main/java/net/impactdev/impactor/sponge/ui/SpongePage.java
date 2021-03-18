package net.impactdev.impactor.sponge.ui;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import net.impactdev.impactor.api.gui.Page;
import net.impactdev.impactor.sponge.SpongeImpactorPlugin;
import net.impactdev.impactor.sponge.ui.rework.SpongeIcon;
import net.impactdev.impactor.sponge.ui.rework.SpongeLayout;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ContainerTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.menu.handler.ClickHandler;
import org.spongepowered.api.item.inventory.type.ViewableInventory;
import org.spongepowered.math.vector.Vector2i;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SpongePage<U> implements Page<Player, U, SpongeUI, SpongeIcon> {

	private final ServerPlayer viewer;
	private final SpongeUI view;
	private int page;

	private List<U> contents;
	private Function<U, SpongeIcon> applier;

	private final TextComponent title;

	private final Vector2i contentZone;
	private final Vector2i offsets;

	private SpongeLayout layout;
	private final List<InternalPage> pages = Lists.newArrayList();

	private final Map<PageIconType, PageIcon<ItemType>> pageIcons;

	private SpongePage(SpongePageBuilder builder) {
		this.viewer = builder.viewer;
		this.pageIcons = builder.pageIcons;
		this.title = builder.title;
		this.page = 1;
		this.contentZone = builder.contentZone;
		this.offsets = builder.offsets;
		this.layout = builder.view;
		this.view = this.forgeFromLayout(builder.view);
	}

	private SpongeUI forgeFromLayout(SpongeLayout layout) {
		SpongeLayout.SpongeLayoutBuilder updated = SpongeLayout.builder().from(layout);
		for(Map.Entry<PageIconType, PageIcon<ItemType>> entry : this.pageIcons.entrySet()) {
			ItemStack item = ItemStack.builder().itemType(entry.getValue().getRep()).build();
			item.offer(Keys.DISPLAY_NAME, LegacyComponentSerializer.legacyAmpersand().deserialize(entry.getKey().getTitle().replaceAll("\\{\\{impactor_page_number}}", "" + page)));
			SpongeIcon icon = SpongeIcon.builder().delegate(item).build();
			if(!entry.getKey().equals(PageIconType.CURRENT)) {
				icon.addListener((cause, container, clickType) -> {
					int capacity = this.contentZone.getX() * this.contentZone.getY();
					this.page = entry.getKey().getUpdater().apply(this.page, this.contents.isEmpty() ? 1 : contents.size() % capacity == 0 ? contents.size() / capacity : contents.size() / capacity + 1);
					this.apply();
					return true;
				});
			}

			updated.slot(icon, entry.getValue().getSlot());
		}

		this.layout = updated.build();
		ViewableInventory view = ViewableInventory.builder()
				.type(ContainerTypes.GENERIC_9X6)
				.completeStructure().build();

		return SpongeUI.builder()
				.title(this.title)
				.view(view)
				.build()
				.define(this.layout);
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

	private int calculateSlot(int in) {
		int col = in % this.contentZone.getX() + this.offsets.getX();
		int row = in / this.contentZone.getX() + this.offsets.getY();

		return col + (9 * row);
	}

	@Override
	public void define(List<U> contents) {
		Preconditions.checkNotNull(this.applier, "Applier must be set before page definition!");
		this.contents = contents;

		int capacity = this.view.getBackingMenu().getInventory().capacity();
		int pages = this.contents.isEmpty() ? 1 : contents.size() % capacity == 0 ? contents.size() / capacity : contents.size() / capacity + 1;

		int columns = Math.max(1, Math.min(9, this.contentZone.getX()));
		int rows = Math.max(1, Math.min(9, this.contentZone.getY()));

		List<U> viewable = this.contents.subList((this.page - 1) * capacity, this.page == pages ? this.contents.size() : this.page * capacity);
		List<SpongeIcon> translated = viewable.stream().map(obj -> this.applier.apply(obj)).collect(Collectors.toList());

		for (int i = 0; i < pages; i++) {
			Inventory page = Inventory.builder()
					.grid(columns, rows)
					.completeStructure()
					.build();

			ViewableInventory view = ViewableInventory.builder()
					.type(ContainerTypes.GENERIC_9X6)
					.grid(page.slots(), new Vector2i(columns, rows), this.offsets)
					.completeStructure().build();

			for(int k = 0; k < view.capacity(); k++) {
				final int slot = i;
				layout.getIcon(i).ifPresent(icon -> {
					view.set(slot, icon.getDisplay());
					if(icon.getListeners().size() > 0) {
						icon.getListeners().forEach(listener -> {
							view.getSlot(slot).ifPresent(s -> {
								this.view.getListener().register(slot, listener);
							});
						});
					}
				});
			}

			InternalPage ip = new InternalPage(view);

			int s = 0;
			for(SpongeIcon icon : translated) {
				Slot slot = page.getSlot(s++).orElseThrow(() -> new IllegalStateException("Unable to locate target slot"));
				slot.set(icon.getDisplay());

				int index = this.calculateSlot(slot.get(Keys.SLOT_INDEX).get());
				SpongeImpactorPlugin.getInstance().getPluginLogger().info("Page: Slot Index = " + index);

				ip.handlers.putAll(index, icon.getListeners());
			}

			this.pages.add(ip);
		}

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
	public void apply() {
		InternalPage page = this.pages.get(this.page - 1);

		this.view.getBackingMenu().setCurrentInventory(page.view);
		this.view.getListener().clear();
		for(Map.Entry<Integer, ClickHandler> entry : page.handlers.entries()) {
			this.view.getListener().register(entry.getKey(), entry.getValue());
		}
	}

	public static SpongePageBuilder builder() {
		return new SpongePageBuilder();
	}

	public static class SpongePageBuilder {

		/** The player viewing the inventory */
		private ServerPlayer viewer;

		/** The base view of all pages */
		private SpongeLayout view;

		/** The title of the representation */
		private TextComponent title;

		/** Represents the area the contents can be displayed in the inventory */
		private Vector2i contentZone;

		/** Represents the row offset for the contentZone, if it is defined */
		private Vector2i offsets;

		/** Represents the actual buttons which will be in charge of updating pages */
		private final Map<PageIconType, PageIcon<ItemType>> pageIcons = Maps.newHashMap();

		public SpongePageBuilder viewer(ServerPlayer viewer) {
			this.viewer = viewer;
			return this;
		}

		public SpongePageBuilder view(SpongeLayout view) {
			this.view = view;
			return this;
		}

		public SpongePageBuilder title(TextComponent title) {
			this.title = title;
			return this;
		}

		public SpongePageBuilder contentZone(Vector2i dimension) {
			this.contentZone = dimension;
			return this;
		}

		public SpongePageBuilder offsets(Vector2i offsets) {
			this.offsets = offsets;
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
			return new SpongePage<>(this);
		}
	}

	private static class InternalPage {

		private final ViewableInventory view;
		private final Multimap<Integer, ClickHandler> handlers;

		public InternalPage(ViewableInventory view) {
			this.view = view;
			this.handlers = ArrayListMultimap.create();
		}

		public void register(int slot, ClickHandler listener) {
			this.handlers.put(slot, listener);
		}

	}
}
