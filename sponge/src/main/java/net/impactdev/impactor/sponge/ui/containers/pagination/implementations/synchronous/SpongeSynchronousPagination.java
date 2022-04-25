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

package net.impactdev.impactor.sponge.ui.containers.pagination.implementations.synchronous;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.platform.players.PlatformPlayerManager;
import net.impactdev.impactor.api.ui.containers.detail.RefreshDetail;
import net.impactdev.impactor.api.ui.containers.detail.RefreshType;
import net.impactdev.impactor.api.ui.containers.detail.RefreshTypes;
import net.impactdev.impactor.api.ui.containers.icons.ClickContext;
import net.impactdev.impactor.api.ui.containers.icons.Icon;
import net.impactdev.impactor.api.ui.containers.pagination.components.Page;
import net.impactdev.impactor.api.utilities.ComponentManipulator;
import net.impactdev.impactor.api.utilities.printing.PrettyPrinter;
import net.impactdev.impactor.common.ui.pagination.types.AbstractPagination;
import net.impactdev.impactor.common.ui.pagination.builders.ImpactorPaginationBuilder;
import net.impactdev.impactor.sponge.SpongeImpactorPlugin;
import net.impactdev.impactor.sponge.ui.containers.components.LayoutTranslator;
import net.impactdev.impactor.sponge.ui.containers.components.SlotContext;
import net.impactdev.impactor.sponge.ui.containers.pagination.components.SpongePage;
import net.impactdev.impactor.sponge.ui.containers.utility.PageConstructor;
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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class SpongeSynchronousPagination extends AbstractPagination {

    private final InventoryMenu view;
    private final SlotContext context;

    public SpongeSynchronousPagination(ImpactorPaginationBuilder builder, List<? extends Icon<?>> icons) {
        super(builder);

        this.context = LayoutTranslator.translate(this.layout());
        this.pages = PageConstructor.construct(icons, this);
        SpongePage first = (SpongePage) this.pages.nextOrThrow();
        this.context.trackAll(this.offsets(), this.zone(), first.icons());
        this.view = first.view().asMenu();
        this.view.setTitle(this.title());
        this.view.setReadOnly(this.readonly);
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

                AtomicBoolean allow = new AtomicBoolean(builder.readonly);
                Optional<Icon<?>> clicked = Optional.ofNullable(this.pages.at(this.page() - 1).icons().get(index));
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
                printer.add("Affected Pagination: " + this.provider().asString());
                printer.add("Context:");
                printer.kv("Title", ComponentManipulator.flatten(this.title()));
                printer.kv("Read Only", readonly);
                printer.kv("Page", this.page());
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
                printer.log(SpongeImpactorPlugin.instance().logger(), PrettyPrinter.Level.ERROR, "ui");
                return false;
            }
        });
    }

    @Override
    public void open() {
        PlatformPlayerManager<ServerPlayer> manager = (PlatformPlayerManager<ServerPlayer>) Impactor.getInstance().getPlatform().playerManager();
        ServerPlayer player = manager.translate(this.viewer).orElseThrow(() -> new IllegalStateException("Player not available or found"));
        this.view.open(player);
    }

    @Override
    public void close() {
        PlatformPlayerManager<ServerPlayer> manager = (PlatformPlayerManager<ServerPlayer>) Impactor.getInstance().getPlatform().playerManager();
        ServerPlayer player = manager.translate(this.viewer).orElseThrow(() -> new IllegalStateException("Player not available or found"));
        if(player.isViewingInventory() && player.openInventory().filter(container -> container.containsInventory(this.view.inventory())).isPresent()) {
            player.closeInventory();
        }
    }

    @Override
    public void page(int target) {
        super.page(target);

        Page<ViewableInventory> page = (Page<ViewableInventory>) this.pages.at(target - 1);
        this.view.setCurrentInventory(page.view());
        this.context.trackAll(this.offsets(), this.zone(), page.icons());
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
            int position = detail.context().require(Integer.class);
            this.context.locate(position)
                    .map(icon -> (Icon<ItemStack>) icon)
                    .ifPresent(icon -> {
                        this.view.inventory().set(position, icon.display().provide());
                    });
        } else if(type == RefreshTypes.SLOT_POS) {
            int position = detail.context().get(Vector2i.class)
                    .map(pos -> pos.x() + (9 * pos.y()))
                    .orElseThrow(NoSuchElementException::new);
            this.context.locate(position)
                    .map(icon -> (Icon<ItemStack>) icon)
                    .ifPresent(icon -> {
                        this.view.inventory().set(position, icon.display().provide());
                    });
        } else if(type == RefreshTypes.GRID) {
            Vector4i base = detail.context().require(Vector4i.class);
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
}
