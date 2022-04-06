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

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.platform.players.PlatformPlayerManager;
import net.impactdev.impactor.api.ui.ImpactorUI;
import net.impactdev.impactor.api.ui.detail.RefreshDetail;
import net.impactdev.impactor.api.ui.detail.RefreshType;
import net.impactdev.impactor.api.ui.detail.RefreshTypes;
import net.impactdev.impactor.api.ui.icons.ClickContext;
import net.impactdev.impactor.api.ui.icons.Icon;
import net.impactdev.impactor.api.ui.layouts.Layout;
import net.impactdev.impactor.api.utilities.ComponentManipulator;
import net.impactdev.impactor.api.utilities.context.Provider;
import net.impactdev.impactor.api.utilities.printing.PrettyPrinter;
import net.impactdev.impactor.sponge.SpongeImpactorPlugin;
import net.impactdev.impactor.sponge.ui.containers.components.LayoutTranslator;
import net.impactdev.impactor.sponge.ui.containers.components.SizeMapping;
import net.impactdev.impactor.sponge.ui.containers.components.SlotContext;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
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

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class SpongeUI implements ImpactorUI {

    private final Key namespace;
    private final Component title;
    private final Layout layout;

    private final InventoryMenu view;
    private final SlotContext context;

    private SpongeUI(SpongeUIBuilder builder) {
        this.namespace = builder.namespace;
        this.layout = builder.layout;

        this.context = LayoutTranslator.translate(this.layout);

        this.view = InventoryMenu.of(ViewableInventory.builder()
                .type(SizeMapping.from(this.layout.dimensions().x()).reference())
                .slots(this.context.slots(), 0)
                .completeStructure()
                .plugin(SpongeImpactorPlugin.getInstance().getPluginContainer())
                .identity(UUID.randomUUID())
                .build()
        );

        this.view.setTitle(this.title = builder.title);
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

                AtomicBoolean allow = new AtomicBoolean(true);
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
                printer.add("Affected Pagination: " + this.namespace.asString());
                printer.add("Context:");
                printer.kv("Title", ComponentManipulator.flatten(this.title));
                printer.kv("Read Only", builder.readonly);
                printer.kv("Slot Clicked", index);
                printer.kv("Click Type", clickType.key(RegistryTypes.CLICK_TYPE));
                printer.newline();
                printer.hr();
                printer.newline();
                printer.add("The stacktrace of the error is detailed below:");
                printer.add(error);
                printer.log(SpongeImpactorPlugin.getInstance().getPluginLogger(), PrettyPrinter.Level.ERROR, "UI");
                return false;
            }
        });
    }

    @Override
    public Key namespace() {
        return this.namespace;
    }

    @Override
    public Layout layout() {
        return this.layout;
    }

    @Override
    public void set(@Nullable Icon<?> icon, int slot) {
        // TODO - In the event of a set, listeners are not updated via the layout,
        // TODO - so replaced icons will still be called rather than the newly set icon.
        // TODO - Change this
        if(icon == null) {
            this.view.inventory().set(slot, ItemStack.empty());
        } else {
            Icon<ItemStack> translated = (Icon<ItemStack>) icon;
            this.view.inventory().set(slot, translated.display().provide());
        }
    }

    @Override
    public void open(PlatformPlayer viewer) {
        PlatformPlayerManager<ServerPlayer> manager = (PlatformPlayerManager<ServerPlayer>) Impactor.getInstance().getPlatform().playerManager();
        ServerPlayer player = manager.translate(viewer).orElseThrow(() -> new IllegalStateException("Player not available or found"));
        this.view.open(player);
    }

    @Override
    public void close(PlatformPlayer viewer) {
        PlatformPlayerManager<ServerPlayer> manager = (PlatformPlayerManager<ServerPlayer>) Impactor.getInstance().getPlatform().playerManager();
        ServerPlayer player = manager.translate(viewer).orElseThrow(() -> new IllegalStateException("Player not available or found"));
        if(player.isViewingInventory() && player.openInventory().filter(container -> container.containsInventory(this.view.inventory())).isPresent()) {
            player.closeInventory();
        }
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

    public static class SpongeUIBuilder implements UIBuilder {

        private Key namespace;
        private Component title;
        private Layout layout;
        private boolean readonly = true;

        @Override
        public UIBuilder provider(Key key) {
            this.namespace = key;
            return this;
        }

        @Override
        public UIBuilder title(Component title) {
            this.title = title;
            return this;
        }

        @Override
        public UIBuilder layout(Layout layout) {
            this.layout = layout;
            return this;
        }

        @Override
        public UIBuilder readonly(boolean state) {
            this.readonly = state;
            return this;
        }

        @Override
        public UIBuilder from(ImpactorUI input) {
            return this;
        }

        @Override
        public ImpactorUI build() {
            return new SpongeUI(this);
        }

    }
}
