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

import net.impactdev.impactor.api.ui.ImpactorUI;
import net.impactdev.impactor.api.ui.icons.ClickContext;
import net.impactdev.impactor.api.ui.icons.Icon;
import net.impactdev.impactor.api.ui.layouts.Layout;
import net.impactdev.impactor.api.utilities.ComponentManipulator;
import net.impactdev.impactor.api.utilities.printing.PrettyPrinter;
import net.impactdev.impactor.sponge.SpongeImpactorPlugin;
import net.impactdev.impactor.sponge.ui.containers.components.SizeMapping;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.menu.ClickType;
import org.spongepowered.api.item.inventory.menu.InventoryMenu;
import org.spongepowered.api.item.inventory.type.ViewableInventory;
import org.spongepowered.api.registry.RegistryTypes;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class SpongeUI implements ImpactorUI<ServerPlayer> {

    private final Key namespace;
    private final Component title;
    private final Layout layout;

    private final InventoryMenu view;

    private SpongeUI(SpongeUIBuilder builder) {
        this.namespace = builder.namespace;
        this.layout = builder.layout;

        this.view = InventoryMenu.of(ViewableInventory.builder()
                .type(SizeMapping.from(this.layout.dimensions().rows()).reference())
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
                Optional<Icon<?>> clicked = this.layout.icon(index);
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
        if(icon == null) {
            this.view.inventory().set(slot, ItemStack.empty());
        } else {
            Icon<ItemStack> translated = (Icon<ItemStack>) icon;
            this.view.inventory().set(slot, translated.display());
        }
    }

    @Override
    public boolean open(ServerPlayer viewer) {
        return this.view.open(viewer).isPresent();
    }

    public static class SpongeUIBuilder implements UIBuilder<ServerPlayer> {

        private Key namespace;
        private Component title;
        private Layout layout;
        private boolean readonly = true;

        @Override
        public UIBuilder<ServerPlayer> provider(Key key) {
            this.namespace = key;
            return this;
        }

        @Override
        public UIBuilder<ServerPlayer> title(Component title) {
            this.title = title;
            return this;
        }

        @Override
        public UIBuilder<ServerPlayer> layout(Layout layout) {
            this.layout = layout;
            return this;
        }

        @Override
        public UIBuilder<ServerPlayer> readonly(boolean state) {
            this.readonly = state;
            return this;
        }

        @Override
        public UIBuilder<ServerPlayer> from(ImpactorUI<ServerPlayer> input) {
            return this;
        }

        @Override
        public ImpactorUI<ServerPlayer> build() {
            return new SpongeUI(this);
        }

    }
}
