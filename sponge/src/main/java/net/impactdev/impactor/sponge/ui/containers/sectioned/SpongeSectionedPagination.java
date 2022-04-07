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

import com.google.common.collect.Lists;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.platform.players.PlatformPlayerManager;
import net.impactdev.impactor.api.ui.containers.icons.ClickContext;
import net.impactdev.impactor.api.ui.containers.icons.Icon;
import net.impactdev.impactor.api.ui.containers.layouts.Layout;
import net.impactdev.impactor.api.ui.containers.pagination.sectioned.SectionedPage;
import net.impactdev.impactor.api.ui.containers.pagination.sectioned.SectionedPagination;
import net.impactdev.impactor.api.ui.containers.pagination.updaters.PageUpdater;
import net.impactdev.impactor.api.ui.containers.pagination.updaters.PageUpdaterType;
import net.impactdev.impactor.api.utilities.ComponentManipulator;
import net.impactdev.impactor.api.utilities.printing.PrettyPrinter;
import net.impactdev.impactor.common.ui.pagination.sectioned.AbstractSectionedPage;
import net.impactdev.impactor.common.ui.pagination.sectioned.AbstractSectionedPagination;
import net.impactdev.impactor.sponge.SpongeImpactorPlugin;
import net.impactdev.impactor.sponge.ui.containers.components.LayoutTranslator;
import net.impactdev.impactor.sponge.ui.containers.components.SlotContext;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import org.jetbrains.annotations.Nullable;
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
import org.spongepowered.math.vector.Vector2i;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class SpongeSectionedPagination extends AbstractSectionedPagination implements SectionedPagination {

    private final InventoryMenu view;
    private final SlotContext context;

    private SpongeSectionedPagination(SpongeSectionedPaginationBuilder builder) {
        super(builder.provider, builder.viewer, builder.layout, builder.sections);
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

                this.at(index).ifPresent(section -> context.append(Section.class, section));

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
        this.view.open(player);
    }

    @Override
    public void close() {

    }

    @Override
    public boolean set(@Nullable Icon<?> icon, int slot) {
        if(!this.at(slot).isPresent()) {
            this.setUnsafe((Icon<ItemStack>) icon, slot);
            return true;
        }

        return false;
    }

    private void setUnsafe(@Nullable Icon<ItemStack> icon, int slot) {
        if(icon == null) {
            this.view.inventory().set(slot, ItemStack.empty());
        } else {
            this.view.inventory().set(slot, icon.display().provide());
        }

        this.context.track(slot, icon);
    }

    public static class SpongeSectionedPaginationBuilder implements SectionedPaginationBuilder {

        private Key provider;
        private PlatformPlayer viewer;
        private Layout layout;
        private Component title;
        private boolean readonly = true;
        private final Set<Section> sections = new HashSet<>();

        @Override
        public SectionedPaginationBuilder title(Component title) {
            this.title = title;
            return this;
        }

        @Override
        public SectionedPaginationBuilder layout(Layout layout) {
            this.layout = layout;
            return this;
        }

        @Override
        public SectionedPaginationBuilder readonly(boolean state) {
            this.readonly = state;
            return this;
        }

        @Override
        public SectionedPaginationBuilder provider(Key provider) {
            this.provider = provider;
            return this;
        }

        @Override
        public SectionedPaginationBuilder viewer(PlatformPlayer viewer) {
            this.viewer = viewer;
            return this;
        }

        @Override
        public SectionBuilder section() {
            return new SpongeSectionBuilder(this);
        }

        SectionedPaginationBuilder with(Section section) {
            this.sections.add(section);
            return this;
        }

        @Override
        public SectionedPaginationBuilder from(SectionedPagination input) {
            return this;
        }

        @Override
        public SectionedPagination build() {
            return new SpongeSectionedPagination(this);
        }
    }

    public static class SpongeSection extends AbstractSection implements Section {

        private SpongeSectionedPagination parent;

        public SpongeSection(SpongeSectionBuilder builder) {
            super(builder.min, builder.max, builder.contents, builder.updaters, builder.style);
        }

        @Override
        public void page(int target) {
            SectionedPage page = this.pages().at(target - 1);
            int index = 0;
            for(Map.Entry<Integer, Icon<?>> icon : page.drawn().entrySet()) {
                this.parent.setUnsafe((Icon<ItemStack>) icon.getValue(), icon.getKey());

                if(this.within(icon.getKey())) {
                    ++index;
                }
            }

            Vector2i bounds = this.maximum().sub(this.minimum()).add(Vector2i.ONE);
            int size = bounds.x() * bounds.y();

            while(index < size) {
                int t = this.calculateTargetSlot(index++, bounds, this.minimum());

                this.parent.setUnsafe(null, t);
            }

        }

        @Override
        protected void assignTo(SectionedPagination parent) {
            this.parent = (SpongeSectionedPagination) parent;
        }

        @Override
        protected SectionedPage constructPage(List<PageUpdater> updaters, TriState style,
                                     int index, int size, Map<Integer, Icon<?>> working) {
            SpongeSectionedPage page = new SpongeSectionedPage(working);
            page.draw(this, updaters, style, index, size);
            return page;
        }

    }

    public static class SpongeSectionedPage extends AbstractSectionedPage {

        public SpongeSectionedPage(Map<Integer, Icon<?>> icons) {
            super(icons);
        }

        @Override
        protected Icon<?> updater(SectionedPagination.Section parent, PageUpdater updater, int page, int target) {
            return Icon.builder(ItemStack.class)
                    .display(() -> (ItemStack) updater.provider().provide(target))
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
        }
    }

    public static class SpongeSectionBuilder implements SectionBuilder {

        private final SpongeSectionedPaginationBuilder parent;

        private List<Icon<?>> contents = Lists.newArrayList();
        private Vector2i min = Vector2i.ZERO;
        private Vector2i max = Vector2i.ZERO;

        private final List<PageUpdater> updaters = Lists.newArrayList();
        private TriState style = TriState.NOT_SET;

        public SpongeSectionBuilder(SpongeSectionedPaginationBuilder parent) {
            this.parent = parent;
        }

        @Override
        public SectionBuilder contents(List<Icon<?>> contents) {
            this.contents = contents;
            return this;
        }

        @Override
        public SectionBuilder dimensions(Vector2i dimensions) {
            this.max = this.max.add(dimensions);
            return this;
        }

        @Override
        public SectionBuilder offset(Vector2i offset) {
            this.min = this.min.add(offset);
            this.max = this.max.add(offset).sub(Vector2i.ONE);
            return this;
        }

        @Override
        public SectionBuilder updater(PageUpdater updater) {
            this.updaters.add(updater);
            return this;
        }

        @Override
        public SectionBuilder style(TriState state) {
            this.style = state;
            return this;
        }

        @Override
        public SectionedPaginationBuilder complete() {
            return this.parent.with(new SpongeSection(this));
        }
    }
}
