package net.impactdev.impactor.sponge.ui;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import net.impactdev.impactor.api.gui.Page;
import net.impactdev.impactor.sponge.ui.icons.SpongeIcon;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ContainerTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.menu.handler.ClickHandler;
import org.spongepowered.api.item.inventory.type.ViewableInventory;
import org.spongepowered.math.vector.Vector2i;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SpongePage<U> implements Page<ServerPlayer, U, SpongeUI, SpongeIcon> {

	private final ServerPlayer viewer;
	private final SpongeUI view;
	private int page;

	private List<U> contents;
	private Function<U, SpongeIcon> applier;

	private final Component title;

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
			item.offer(Keys.CUSTOM_NAME, LegacyComponentSerializer.legacyAmpersand().deserialize(entry.getKey().getTitle().replaceAll("\\{\\{impactor_page_number}}", "" + page)));
			SpongeIcon icon = SpongeIcon.builder().delegate(item).build();
			if(!entry.getKey().equals(PageIconType.CURRENT)) {
				icon.addListener((cause, container, clickType) -> {
					int capacity = this.contentZone.x() * this.contentZone.y();
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
	public ServerPlayer getViewer() {
		return this.viewer;
	}

	@Override
	public SpongeUI getView() {
		return this.view;
	}

	@Override
	public Page<ServerPlayer, U, SpongeUI, SpongeIcon> applier(Function<U, SpongeIcon> applier) {
		this.applier = applier;
		return this;
	}

	private int calculateSlot(int in) {
		int col = in % this.contentZone.x() + this.offsets.y();
		int row = in / this.contentZone.x() + this.offsets.y();

		return col + (9 * row);
	}

	@Override
	public void define(List<U> contents) {
		Preconditions.checkNotNull(this.applier, "Applier must be set before page definition!");
		this.contents = contents;

		int columns = Math.max(1, Math.min(9, this.contentZone.x()));
		int rows = Math.max(1, Math.min(9, this.contentZone.y()));

		int capacity = columns * rows;
		int pages = this.contents.isEmpty() ? 1 : contents.size() % capacity == 0 ? contents.size() / capacity : contents.size() / capacity + 1;

		for (int i = 0; i < pages; i++) {
			List<U> viewable = this.contents.subList((i) * capacity, i + 1 == pages ? this.contents.size() : (i + 1) * capacity);
			List<SpongeIcon> translated = viewable.stream().map(obj -> this.applier.apply(obj)).collect(Collectors.toList());

			ViewableInventory view = ViewableInventory.builder()
					.type(ContainerTypes.GENERIC_9X6)
					.completeStructure().build();

			InternalPage ip = new InternalPage(view);

			for(int k = 0; k < view.capacity(); k++) {
				final int slot = k;
				layout.getIcon(k).ifPresent(icon -> {
					view.set(slot, icon.getDisplay());
					if(icon.getListeners().size() > 0) {
						icon.getListeners().forEach(listener -> {
							view.slot(slot).ifPresent(s -> {
								ip.register(slot, listener);
							});
						});
					}
				});
			}


			int s = 0;
			for(SpongeIcon icon : translated) {
				int key = this.calculateSlot(s++);
				Slot slot = view.slot(key).orElseThrow(() -> new IllegalStateException("Unable to locate target slot"));
				slot.set(icon.getDisplay());

				ip.handlers.putAll(key, icon.getListeners());
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
		private Component title;

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

		public SpongePageBuilder title(Component title) {
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

		public SpongePageBuilder firstPage(Supplier<? extends ItemType> type, int slot) {
			this.pageIcons.put(PageIconType.FIRST, new PageIcon<>(type.get(), slot));
			return this;
		}

		public SpongePageBuilder previousPage(Supplier<? extends ItemType> type, int slot) {
			this.pageIcons.put(PageIconType.PREV, new PageIcon<>(type.get(), slot));
			return this;
		}

		public SpongePageBuilder currentPage(Supplier<? extends ItemType> type, int slot) {
			this.pageIcons.put(PageIconType.CURRENT, new PageIcon<>(type.get(), slot));
			return this;
		}

		public SpongePageBuilder nextPage(Supplier<? extends ItemType> type, int slot) {
			this.pageIcons.put(PageIconType.NEXT, new PageIcon<>(type.get(), slot));
			return this;
		}

		public SpongePageBuilder lastPage(Supplier<? extends ItemType> type, int slot) {
			this.pageIcons.put(PageIconType.LAST, new PageIcon<>(type.get(), slot));
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
