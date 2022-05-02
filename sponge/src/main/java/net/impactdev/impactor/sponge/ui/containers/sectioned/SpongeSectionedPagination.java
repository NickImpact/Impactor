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

package net.impactdev.impactor.sponge.ui.containers.sectioned;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.platform.players.PlatformPlayerManager;
import net.impactdev.impactor.api.ui.containers.detail.RefreshDetail;
import net.impactdev.impactor.api.ui.containers.detail.RefreshType;
import net.impactdev.impactor.api.ui.containers.detail.RefreshTypes;
import net.impactdev.impactor.api.ui.containers.icons.ClickContext;
import net.impactdev.impactor.api.ui.containers.icons.Icon;
import net.impactdev.impactor.api.ui.containers.pagination.sectioned.sections.Section;
import net.impactdev.impactor.api.ui.containers.pagination.sectioned.SectionedPagination;
import net.impactdev.impactor.api.utilities.ComponentManipulator;
import net.impactdev.impactor.api.utilities.printing.PrettyPrinter;
import net.impactdev.impactor.common.ui.containers.pagination.sectioned.builders.ImpactorSectionedPaginationBuilder;
import net.impactdev.impactor.common.ui.containers.pagination.sectioned.AbstractSectionedPagination;
import net.impactdev.impactor.common.ui.containers.pagination.sectioned.sections.AbstractSynchronousSection;
import net.impactdev.impactor.sponge.SpongeImpactorPlugin;
import net.impactdev.impactor.sponge.ui.containers.components.LayoutTranslator;
import net.impactdev.impactor.sponge.ui.containers.components.SlotContext;
import net.impactdev.impactor.sponge.ui.containers.sectioned.sections.asynchronous.SpongeAsynchronousSection;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.ContainerTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.menu.ClickType;
import org.spongepowered.api.item.inventory.menu.InventoryMenu;
import org.spongepowered.api.item.inventory.type.ViewableInventory;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Ticks;
import org.spongepowered.math.vector.Vector2i;
import org.spongepowered.math.vector.Vector4i;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class SpongeSectionedPagination extends AbstractSectionedPagination implements SectionedPagination {

    private final InventoryMenu view;
    private final SlotContext context;

    public SpongeSectionedPagination(ImpactorSectionedPaginationBuilder builder) {
        super(builder);
        this.context = LayoutTranslator.merge(this.layout(), this.sections());

        ViewableInventory.Builder.BuildingStep viewable = ViewableInventory.builder()
                .type(ContainerTypes.GENERIC_9X6)
                .slots(this.context.slots(), 0);

        this.view = InventoryMenu.of(viewable.completeStructure()
                .identity(UUID.randomUUID())
                .plugin(SpongeImpactorPlugin.instance().bootstrapper().container())
                .build());
        this.view.setTitle(builder.title);
        this.view.setReadOnly(builder.readonly);
        this.view.registerSlotClick((cause, container, slot, index, clickType) -> {
            try {
                ServerPlayer source = cause.first(ServerPlayer.class)
                        .orElseThrow(() -> new IllegalStateException("Click action without player cause"));

                ClickContext context = ClickContext.create();
                context.append(Cause.class, cause);
                context.append(Container.class, container);
                context.append(Slot.class, slot);
                context.append(Integer.class, index);
                context.append(ClickType.class, clickType);
                context.append(ServerPlayer.class, source);

                this.at(index).ifPresent(section -> ((AbstractSynchronousSection) section).appendToClickContext(context));

                AtomicBoolean allow = new AtomicBoolean(builder.readonly);
                Optional<Icon<?>> clicked = this.context.locate(index);
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
                printer.kv("Title", ComponentManipulator.flatten(builder.title));
                printer.kv("Read Only", builder.readonly);
                printer.kv("Slot Clicked", index);
                printer.kv("Click Type", clickType.key(RegistryTypes.CLICK_TYPE));
                printer.newline();
                printer.hr();
                printer.newline();
                printer.add("The stacktrace of the error is detailed below:");
                printer.add(error);
                printer.log(SpongeImpactorPlugin.instance().logger(), PrettyPrinter.Level.ERROR, "UI");
                return false;
            }
        });
    }

    @Override
    public void open() {
        PlatformPlayerManager<ServerPlayer> manager = (PlatformPlayerManager<ServerPlayer>) Impactor.getInstance().getPlatform().playerManager();
        ServerPlayer player = manager.translate(this.viewer).orElseThrow(() -> new IllegalStateException("Player not available or found"));

        for(Section section : this.sections) {
            if(section instanceof SpongeAsynchronousSection) {
                ((SpongeAsynchronousSection) section).queue();
            }
        }

        Sponge.server().scheduler().submit(Task.builder()
                .execute(() -> this.view.open(player))
                .delay(Ticks.single())
                .plugin(SpongeImpactorPlugin.instance().bootstrapper().container())
                .build());
    }

    @Override
    public void close() {
        PlatformPlayerManager<ServerPlayer> manager = (PlatformPlayerManager<ServerPlayer>) Impactor.getInstance().getPlatform().playerManager();
        ServerPlayer player = manager.translate(this.viewer).orElseThrow(() -> new IllegalStateException("Player not available or found"));

        player.closeInventory();
        for(Section section : this.sections) {
            ((AbstractSynchronousSection) section).handleClose();
        }
    }

    @Override
    public boolean set(@Nullable Icon<?> icon, int slot) {
        if(!this.at(slot).isPresent()) {
            this.setUnsafe(icon, slot);
            return true;
        }

        return false;
    }

    @Override
    public void refresh(RefreshDetail details) {
        RefreshType type = details.type();
        if(type == RefreshTypes.SLOT_INDEX) {
            int position = details.context().require(Integer.class);
            this.context.locate(position)
                    .map(icon -> (Icon<ItemStack>) icon)
                    .ifPresent(icon -> {
                        this.view.inventory().set(position, icon.display().provide());
                    });
        } else if(type == RefreshTypes.SLOT_POS) {
            int position = details.context().get(Vector2i.class)
                    .map(pos -> pos.x() + (9 * pos.y()))
                    .orElseThrow(NoSuchElementException::new);
            this.context.locate(position)
                    .map(icon -> (Icon<ItemStack>) icon)
                    .ifPresent(icon -> {
                        this.view.inventory().set(position, icon.display().provide());
                    });
        } else if(type == RefreshTypes.GRID) {
            Vector4i base = details.context().require(Vector4i.class);
            Vector2i grid = base.toVector2();
            Vector2i offset = new Vector2i(base.z(), base.w());

            for (int y = offset.y(); y < grid.y() + offset.y(); y++) {
                for (int x = offset.x(); x < grid.x() + offset.x(); x++) {
                    final int X = x;
                    final int Y = y;
                    this.context.locate(X + (9 * Y))
                            .map(icon -> (Icon<ItemStack>) icon)
                            .ifPresent(icon -> {
                                this.view.inventory().set(X + (9 * Y), icon.display().provide());
                            });
                }
            }
        } else if(type == RefreshTypes.SECTION) {
            Section section = details.context().require(Section.class);
            section.refresh((slot, icon) -> this.view.inventory().set(slot, (ItemStack) icon.display().provide()));
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
    public void setUnsafe(@Nullable Icon<?> icon, int slot) {
        Icon<ItemStack> translated = (Icon<ItemStack>) icon;
        if(icon == null) {
            this.view.inventory().set(slot, ItemStack.empty());
        } else {
            this.view.inventory().set(slot, translated.display().provide());
        }

        this.context.track(slot, translated);
    }

}
