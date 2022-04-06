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
import net.impactdev.impactor.api.ui.detail.RefreshDetail;
import net.impactdev.impactor.api.ui.detail.RefreshType;
import net.impactdev.impactor.api.ui.detail.RefreshTypes;
import net.impactdev.impactor.api.ui.icons.ClickContext;
import net.impactdev.impactor.api.ui.icons.Icon;
import net.impactdev.impactor.api.ui.layouts.Layout;
import net.impactdev.impactor.api.ui.pagination.Page;
import net.impactdev.impactor.api.ui.pagination.Pagination;
import net.impactdev.impactor.api.ui.pagination.updaters.PageUpdater;
import net.impactdev.impactor.api.utilities.ComponentManipulator;
import net.impactdev.impactor.api.utilities.context.Provider;
import net.impactdev.impactor.api.utilities.lists.CircularLinkedList;
import net.impactdev.impactor.api.utilities.printing.PrettyPrinter;
import net.impactdev.impactor.sponge.SpongeImpactorPlugin;
import net.impactdev.impactor.sponge.ui.containers.components.LayoutTranslator;
import net.impactdev.impactor.sponge.ui.containers.components.SlotContext;
import net.impactdev.impactor.sponge.ui.containers.utility.PageConstructor;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.menu.ClickType;
import org.spongepowered.api.item.inventory.menu.InventoryMenu;
import org.spongepowered.api.item.inventory.type.ViewableInventory;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.math.vector.Vector2i;
import org.spongepowered.math.vector.Vector4i;

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

    private final SlotContext context;

    private SpongePagination(SpongePaginationBuilder builder) {
        this.namespace = builder.key;
        this.viewer = builder.viewer;
        this.title = builder.title;
        this.layout = builder.layout;
        this.zone = builder.zone;
        this.offsets = builder.offsets;

        this.context = LayoutTranslator.translate(this.layout);

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

        this.context.trackAll(this.offsets, this.zone, this.pages.getCurrent().orElseThrow(NoSuchElementException::new).icons());
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
    public Component title() {
        return this.title;
    }

    @Override
    public Layout layout() {
        return this.layout;
    }

    @Override
    public Vector2i zone() {
        return this.zone;
    }

    @Override
    public Vector2i offsets() {
        return this.offsets;
    }

    @Override
    public int page() {
        return this.page;
    }

    @Override
    public void page(int target) {
        this.page = target;
        Page<ViewableInventory> page = (Page<ViewableInventory>) this.pages.at(target - 1);
        this.view.setCurrentInventory(page.view());
        this.context.trackAll(this.offsets, this.zone, page.icons());
    }

    @Override
    public List<PageUpdater> updaters() {
        return this.updaters;
    }

    @Override
    public TriState style() {
        return this.updaterStyle;
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
            this.view.inventory().set(slot, translated.display().provide());
        }

        return true;
    }

    @Override
    public void refresh(RefreshDetail detail) {
        RefreshType type = detail.type();
        if(type == RefreshTypes.SLOT_INDEX) {
            int position = detail.context().require(Integer.class).instance();
            this.context.locate(position)
                    .map(icon -> (Icon<ItemStack>) icon)
                    .ifPresent(icon -> {
                        this.view.inventory().set(position, icon.display().provide());
                    });
        } else if(type == RefreshTypes.SLOT_POS) {
            int position = detail.context().get(Vector2i.class)
                    .map(Provider::instance)
                    .map(pos -> pos.x() + (9 * pos.y()))
                    .orElseThrow(NoSuchElementException::new);
            this.context.locate(position)
                    .map(icon -> (Icon<ItemStack>) icon)
                    .ifPresent(icon -> {
                        this.view.inventory().set(position, icon.display().provide());
                    });
        } else if(type == RefreshTypes.GRID) {
            Vector4i base = detail.context().require(Vector4i.class).instance();
            Vector2i grid = base.toVector2();
            Vector2i offset = new Vector2i(base.z(), base.w());

            for(int y = offset.y(); y < grid.y() + offset.y(); y++) {
                for(int x = offset.x(); x < grid.x() + offset.x(); x++) {
                    final int X = x;
                    final int Y = y;
                    this.context.locate(X + (9 * Y))
                            .map(icon -> (Icon<ItemStack>) icon)
                            .ifPresent(icon -> {
                                this.view.inventory().set(X + (9 * Y), icon.display().provide());
                            });
                }
            }
        } else {
            if(type == RefreshTypes.ALL) {
                this.context.tracked()
                        .forEach((slot, icon) -> {
                            this.view.inventory().set(slot, (ItemStack) icon.display().provide());
                        });
            } else if(type == RefreshTypes.LAYOUT) {
                this.layout().elements()
                        .forEach((slot, icon) -> {
                            this.view.inventory().set(slot, (ItemStack) icon.display().provide());
                        });
            }
        }
    }

    @Override
    public CircularLinkedList<Page<?>> pages() {
        return this.pages;
    }

    private CircularLinkedList<Page<?>> draftPages(List<Icon<?>> icons) {
        return PageConstructor.construct(icons, this);
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
