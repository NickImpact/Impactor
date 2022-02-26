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
import net.impactdev.impactor.api.ui.components.Dimensions;
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

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class SpongePagination implements Pagination {

    private final Key namespace;
    private final ServerPlayer viewer;
    private final Component title;
    private final Layout layout;
    private final Dimensions zone;
    private final Dimensions offsets;
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
                if(!source.uniqueId().equals(this.viewer.uniqueId())) {
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
                printer.kv("UUID", this.viewer.uniqueId());
                printer.kv("Name", this.viewer.name());
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
    public Key namespace() {
        return this.namespace;
    }

    @Override
    public boolean open() {
        this.page(1);
        this.view.open(this.viewer);
        return true;
    }

    @Override
    public boolean close() {
        return this.viewer.closeInventory();
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
    public void set(@Nullable Icon<?> icon, int slot) {
        if(icon == null) {
            this.view.inventory().set(slot, ItemStack.empty());
        } else {
            Icon<ItemStack> translated = (Icon<ItemStack>) icon;
            this.view.inventory().set(slot, translated.display());
        }
    }

    @Override
    public CircularLinkedList<Page<?>> pages() {
        return this.pages;
    }

    private CircularLinkedList<Page<?>> draftPages(List<Icon<?>> icons) {
        CircularLinkedList<Page<?>> result = CircularLinkedList.of();
        int max = this.zone.columns() * this.zone.rows();
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
                    .type(SizeMapping.from(this.layout.dimensions().rows()).reference())
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
                .type(SizeMapping.from(this.layout.dimensions().rows()).reference())
                .completeStructure()
                .identity(UUID.randomUUID())
                .plugin(SpongeImpactorPlugin.getInstance().getPluginContainer())
                .build();

        SpongePage page = new SpongePage(view, working);
        page.draw(this, this.layout, this.updaters, result.size() + 1, size);
        result.append(page);
    }

    public record SpongePage(ViewableInventory view, Map<Integer, Icon<?>> icons) implements Page<ViewableInventory> {

        public void draw(Pagination parent, Layout layout, List<PageUpdater> updaters, int page, int maxPages) {
            layout.elements().forEach((slot, icon) -> {
                view.set(slot, ((SpongeIcon) icon).display());
                icons.put(slot, icon);
            });

            updaters.forEach(updater -> {
                SpongePagination translated = (SpongePagination) parent;
                switch(updater.type()) {
                    case PREVIOUS, FIRST -> {
                        if(page == 1 && translated.updaterStyle == TriState.FALSE) {
                            return;
                        }
                    }
                    case NEXT, LAST -> {
                        if(page == maxPages && translated.updaterStyle == TriState.FALSE) {
                            return;
                        }
                    }
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
                            if(!updater.type().equals(PageUpdaterType.CURRENT)) {
                                if(target == page) {
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

    }

    public static class SpongePaginationBuilder implements PaginationBuilder<ServerPlayer> {

        private Key key;
        private ServerPlayer viewer;
        private boolean readonly = true;
        private Component title;
        private Layout layout;
        private Dimensions zone;
        private Dimensions offsets;

        private List<Icon<?>> contents;
        private final List<PageUpdater> updaters = Lists.newArrayList();
        private TriState updaterStyle = TriState.NOT_SET;

        @Override
        @Required
        public PaginationBuilder<ServerPlayer> provider(Key key) {
            this.key = key;
            return this;
        }

        @Override
        public PaginationBuilder<ServerPlayer> viewer(ServerPlayer viewer) {
            this.viewer = viewer;
            return this;
        }

        @Override
        public PaginationBuilder<ServerPlayer> readonly(boolean state) {
            this.readonly = state;
            return this;
        }

        @Override
        public PaginationBuilder<ServerPlayer> title(Component title) {
            this.title = title;
            return this;
        }

        @Override
        public PaginationBuilder<ServerPlayer> contents(List<Icon<?>> icons) {
            this.contents = icons;
            return this;
        }

        @Override
        public PaginationBuilder<ServerPlayer> zone(Dimensions dimensions) {
            return this.zone(dimensions, Dimensions.ZERO);
        }

        @Override
        public PaginationBuilder<ServerPlayer> zone(Dimensions dimensions, @Nullable Dimensions offset) {
            this.zone = dimensions;
            this.offsets = offset;
            return this;
        }

        @Override
        public PaginationBuilder<ServerPlayer> layout(Layout layout) {
            this.layout = layout;
            return this;
        }

        @Override
        public PaginationBuilder<ServerPlayer> updater(PageUpdater updater) {
            this.updaters.add(updater);
            return this;
        }

        @Override
        public PaginationBuilder<ServerPlayer> updaterStyle(TriState state) {
            this.updaterStyle = state;
            return this;
        }

        @Override
        public PaginationBuilder<ServerPlayer> from(Pagination input) {
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
