/*
 * This file is part of Impactor, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018-2022 NickImpact
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package net.impactdev.impactor.sponge.ui.containers;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.platform.players.PlatformPlayerManager;
import net.impactdev.impactor.api.ui.icons.ClickContext;
import net.impactdev.impactor.api.ui.icons.Icon;
import net.impactdev.impactor.api.ui.layouts.Layout;
import net.impactdev.impactor.api.ui.pagination.Page;
import net.impactdev.impactor.api.ui.pagination.Pagination;
import net.impactdev.impactor.api.ui.pagination.updaters.PageUpdater;
import net.impactdev.impactor.api.ui.pagination.updaters.PageUpdaterType;
import net.impactdev.impactor.api.utilities.ComponentManipulator;
import net.impactdev.impactor.api.utilities.lists.CircularLinkedList;
import net.impactdev.impactor.api.utilities.printing.PrettyPrinter;
import net.impactdev.impactor.sponge.SpongeImpactorPlugin;
import net.impactdev.impactor.sponge.ui.containers.components.SizeMapping;
import net.impactdev.impactor.sponge.ui.containers.icons.SpongeIcon;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.menu.ClickType;
import org.spongepowered.api.item.inventory.menu.InventoryMenu;
import org.spongepowered.api.item.inventory.type.ViewableInventory;
import org.spongepowered.api.registry.RegistryEntry;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.math.vector.Vector2i;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class SpongePagination implements Pagination {

    private final Key namespace;
    private final PlatformPlayer viewer;
    private final Component title;
    private final Layout layout;
    private final Vector2i zone;
    private final Vector2i offsets;
    private final CircularLinkedList<Page<?>> pages;
    private final List<PageUpdater> updaters;
    private final TriState updaterStyle;

    private final InventoryMenu view;
    private int page = 1;

    private SpongePagination(SpongePaginationBuilder builder) {
        this.namespace = builder.key;
        this.viewer = builder.viewer;
        this.title = builder.title;
        this.layout = builder.layout;
        this.zone = builder.zone;
        this.offsets = builder.offsets;

        this.updaters = builder.updaters;
        this.updaterStyle = builder.updaterStyle;
        this.pages = this.draftPages(builder.contents);

        this.view = ((SpongePage) this.pages.nextOrThrow())
                .view()
                .asMenu();
        this.view.setTitle(this.title);
        this.view.setReadOnly(builder.readonly);
        this.view.registerSlotClick((cause, container, slot, index, clickType) -> {
            try {
                ServerPlayer source = cause.first(ServerPlayer.class)
                        .orElseThrow(() -> new IllegalStateException("Click action without player cause"));
                if(!source.uniqueId().equals(this.viewer.uuid())) {
                    throw new IllegalStateException(String.format(
                            "Click source (%s) does not match viewer",
                            source.uniqueId()
                    ));
                }

                ClickContext context = ClickContext.create();
                context.append(Cause.class, cause);
                context.append(Container.class, container);
                context.append(Slot.class, slot);
                context.append(Integer.class, index);
                context.append(ClickType.class, clickType);
                context.append(ServerPlayer.class, source);

                AtomicBoolean allow = new AtomicBoolean(true);
                Optional<Icon<?>> clicked = Optional.ofNullable(this.pages.at(this.page - 1).icons().get(index));
                clicked.ifPresent(icon -> icon.listeners().forEach(listener -> {
                    boolean result = listener.process(context);
                    if(allow.get() && !result) {
                        allow.set(false);
                    }
                }));
                return allow.get();
            } catch (Throwable error) {
                PrettyPrinter printer = new PrettyPrinter(80);
                printer.newline().add("Exception occurred during click processing!").center().newline();
                printer.hr();
                printer.add("Affected Pagination: " + this.namespace.asString());
                printer.add("Context:");
                printer.kv("Title", ComponentManipulator.flatten(this.title));
                printer.kv("Read Only", builder.readonly);
                printer.kv("Page", this.page);
                printer.kv("Slot Clicked", index);
                printer.kv("Click Type", clickType.key(RegistryTypes.CLICK_TYPE));
                printer.newline();
                printer.add("Viewer:");
                printer.kv("UUID", this.viewer.uuid());
                printer.kv("Name", ComponentManipulator.flatten(this.viewer.name()));
                printer.newline();
                printer.hr();
                printer.newline();
                printer.add("The stacktrace of the error is detailed below:");
                printer.add(error);
                printer.log(SpongeImpactorPlugin.getInstance().getPluginLogger(), PrettyPrinter.Level.ERROR, "ui");
                return false;
            }
        });
    }

    @Override
    public Key provider() {
        return this.namespace;
    }

    @Override
    public void open() {
        this.page(1);

        PlatformPlayerManager<ServerPlayer> manager = (PlatformPlayerManager<ServerPlayer>) Impactor.getInstance().getPlatform().playerManager();
        ServerPlayer player = manager.translate(this.viewer).orElseThrow(() -> new IllegalStateException("Player not available or found"));
        this.view.open(player);
    }

    @Override
    public void close() {
        PlatformPlayerManager<ServerPlayer> manager = (PlatformPlayerManager<ServerPlayer>) Impactor.getInstance().getPlatform().playerManager();
        ServerPlayer player = manager.translate(viewer).orElseThrow(() -> new IllegalStateException("Player not available or found"));
        if(player.isViewingInventory() && player.openInventory().filter(container -> container.containsInventory(this.view.inventory())).isPresent()) {
            player.closeInventory();
        }
    }

    @Override
    public Layout layout() {
        return this.layout;
    }

    @Override
    public void page(int target) {
        this.page = target;
        Page<ViewableInventory> page = (Page<ViewableInventory>) this.pages.at(target - 1);
        this.view.setCurrentInventory(page.view());
    }

    @Override
    public boolean set(@Nullable Icon<?> icon, int slot) {
        if(this.within(slot)) {
            return false;
        }

        if(icon == null) {
            this.view.inventory().set(slot, ItemStack.empty());
        } else {
            Icon<ItemStack> translated = (Icon<ItemStack>) icon;
            this.view.inventory().set(slot, translated.display());
        }

        return true;
    }

    @Override
    public CircularLinkedList<Page<?>> pages() {
        return this.pages;
    }

    private CircularLinkedList<Page<?>> draftPages(List<Icon<?>> icons) {
        CircularLinkedList<Page<?>> result = CircularLinkedList.of();
        int max = this.zone.y() * this.zone.x();
        int size = icons.size() / max + 1;

        int slot = 0;
        Map<Integer, Icon<?>> working = new HashMap<>();
        for(Icon<?> icon : icons) {
            if(slot < max) {
                int target = this.calculateTargetSlot(slot, this.zone, this.offsets);
                working.put(target, icon);
                ++slot;
            } else {
                int target = this.calculateTargetSlot(0, this.zone, this.offsets);

                this.constructPage(result, size, working);
                working = new HashMap<>();
                working.put(target, icon);
                slot = 1;
            }
        }

        if(!working.isEmpty()) {
            this.constructPage(result, size, working);
        }

        if(result.empty()) {
            ViewableInventory view = ViewableInventory.builder()
                    .type(SizeMapping.from(this.layout.dimensions().x()).reference())
                    .completeStructure()
                    .identity(UUID.randomUUID())
                    .plugin(SpongeImpactorPlugin.getInstance().getPluginContainer())
                    .build();
            SpongePage page = new SpongePage(view, new HashMap<>());
            page.draw(this, this.layout, this.updaters, 1, size);
            result.append(page);
        }

        return result;
    }

    private void constructPage(CircularLinkedList<Page<?>> result, int size, Map<Integer, Icon<?>> working) {
        ViewableInventory view = ViewableInventory.builder()
                .type(SizeMapping.from(this.layout.dimensions().x()).reference())
                .completeStructure()
                .identity(UUID.randomUUID())
                .plugin(SpongeImpactorPlugin.getInstance().getPluginContainer())
                .build();

        SpongePage page = new SpongePage(view, working);
        page.draw(this, this.layout, this.updaters, result.size() + 1, size);
        result.append(page);
    }

    private boolean within(int slot) {
        int x = slot % 9;
        int y = slot / 9;

        int mx = this.zone.x() + this.offsets.x();
        int my = this.zone.y() + this.offsets.y();
        if(x >= this.offsets.x() && x <= mx) {
            return y >= this.offsets.y() && y <= my;
        }

        return false;
    }

    public static final class SpongePage implements Page<ViewableInventory> {
        private final ViewableInventory view;
        private final Map<Integer, Icon<?>> icons;

        public SpongePage(ViewableInventory view, Map<Integer, Icon<?>> icons) {
            this.view = view;
            this.icons = icons;
        }

        public void draw(Pagination parent, Layout layout, List<PageUpdater> updaters, int page, int maxPages) {
            layout.elements().forEach((slot, icon) -> {
                view.set(slot, ((SpongeIcon) icon).display());
                icons.put(slot, icon);
            });

            updaters.forEach(updater -> {
                SpongePagination translated = (SpongePagination) parent;
                switch (updater.type()) {
                    case PREVIOUS:
                    case FIRST:
                        if (page == 1 && translated.updaterStyle == TriState.FALSE) {
                            return;
                        }
                        break;
                    case NEXT:
                    case LAST:
                        if (page == maxPages && translated.updaterStyle == TriState.FALSE) {
                            return;
                        }
                        break;
                }

                int target = updater.type().translate(page, maxPages);
                Component title = updater.title(target);
                List<Component> lore = updater.lore(target);

                ItemType type = Sponge.game().registry(RegistryTypes.ITEM_TYPE)
                        .findEntry(ResourceKey.of(updater.key().namespace(), updater.key().value()))
                        .map(RegistryEntry::value)
                        .orElse(ItemTypes.BARRIER.get());
                ItemStack display = ItemStack.builder()
                        .itemType(type)
                        .add(Keys.CUSTOM_NAME, title)
                        .add(Keys.LORE, lore)
                        .build();
                Icon<ItemStack> icon = Icon.builder(ItemStack.class)
                        .display(display)
                        .listener(processor -> {
                            if (!updater.type().equals(PageUpdaterType.CURRENT)) {
                                if (target == page) {
                                    return false;
                                }

                                parent.page(target);
                            }
                            return false;
                        })
                        .build();

                view.set(updater.slot(), icon.display());
                this.icons.put(updater.slot(), icon);
            });

            icons.forEach((slot, icon) -> view.set(slot, ((SpongeIcon) icon).display()));
        }

        public ViewableInventory view() {return view;}

        public Map<Integer, Icon<?>> icons() {return icons;}

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            SpongePage that = (SpongePage) obj;
            return Objects.equals(this.view, that.view) &&
                    Objects.equals(this.icons, that.icons);
        }

        @Override
        public int hashCode() {
            return Objects.hash(view, icons);
        }

        @Override
        public String toString() {
            return "SpongePage[" +
                    "view=" + view + ", " +
                    "icons=" + icons + ']';
        }


    }

    public static class SpongePaginationBuilder implements PaginationBuilder {

        private Key key;
        private PlatformPlayer viewer;
        private boolean readonly = true;
        private Component title;
        private Layout layout;
        private Vector2i zone;
        private Vector2i offsets;

        private List<Icon<?>> contents;
        private final List<PageUpdater> updaters = Lists.newArrayList();
        private TriState updaterStyle = TriState.NOT_SET;

        @Override
        @Required
        public PaginationBuilder provider(Key key) {
            this.key = key;
            return this;
        }

        @Override
        public PaginationBuilder viewer(PlatformPlayer viewer) {
            this.viewer = viewer;
            return this;
        }

        @Override
        public PaginationBuilder readonly(boolean state) {
            this.readonly = state;
            return this;
        }

        @Override
        public PaginationBuilder title(Component title) {
            this.title = title;
            return this;
        }

        @Override
        public PaginationBuilder contents(List<Icon<?>> icons) {
            this.contents = icons;
            return this;
        }

        @Override
        public PaginationBuilder zone(Vector2i dimensions) {
            return this.zone(dimensions, Vector2i.ZERO);
        }

        @Override
        public PaginationBuilder zone(Vector2i dimensions, @Nullable Vector2i offset) {
            this.zone = dimensions;
            this.offsets = offset;
            return this;
        }

        @Override
        public PaginationBuilder layout(Layout layout) {
            this.layout = layout;
            return this;
        }

        @Override
        public PaginationBuilder updater(PageUpdater updater) {
            this.updaters.add(updater);
            return this;
        }

        @Override
        public PaginationBuilder style(TriState state) {
            this.updaterStyle = state;
            return this;
        }

        @Override
        public PaginationBuilder from(Pagination input) {
            return this;
        }

        @Override
        public Pagination build() {
            Preconditions.checkNotNull(this.key);
            Preconditions.checkNotNull(this.viewer);
            return new SpongePagination(this);
        }
    }

}
