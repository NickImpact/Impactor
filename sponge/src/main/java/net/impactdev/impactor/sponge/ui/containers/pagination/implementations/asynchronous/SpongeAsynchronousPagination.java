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

package net.impactdev.impactor.sponge.ui.containers.pagination.implementations.asynchronous;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.platform.players.PlatformPlayerManager;
import net.impactdev.impactor.api.scheduler.SchedulerAdapter;
import net.impactdev.impactor.api.services.text.MessageService;
import net.impactdev.impactor.api.ui.containers.detail.RefreshDetail;
import net.impactdev.impactor.api.ui.containers.icons.ClickContext;
import net.impactdev.impactor.api.ui.containers.icons.DisplayProvider;
import net.impactdev.impactor.api.ui.containers.icons.Icon;
import net.impactdev.impactor.api.ui.containers.pagination.components.Page;
import net.impactdev.impactor.api.ui.containers.pagination.components.TimeoutDetails;
import net.impactdev.impactor.api.utilities.ComponentManipulator;
import net.impactdev.impactor.api.utilities.lists.CircularLinkedList;
import net.impactdev.impactor.api.utilities.printing.PrettyPrinter;
import net.impactdev.impactor.common.ui.containers.pagination.types.AbstractAsynchronousPagination;
import net.impactdev.impactor.common.ui.containers.pagination.builders.ImpactorPaginationBuilder;
import net.impactdev.impactor.sponge.SpongeImpactorPlugin;
import net.impactdev.impactor.sponge.ui.containers.components.LayoutTranslator;
import net.impactdev.impactor.sponge.ui.containers.components.SlotContext;
import net.impactdev.impactor.sponge.ui.containers.pagination.components.SpongePage;
import net.impactdev.impactor.sponge.ui.containers.utility.PageConstructor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.ContainerTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.menu.ClickType;
import org.spongepowered.api.item.inventory.menu.InventoryMenu;
import org.spongepowered.api.item.inventory.type.ViewableInventory;
import org.spongepowered.api.registry.RegistryTypes;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public abstract class SpongeAsynchronousPagination extends AbstractAsynchronousPagination {

    private final InventoryMenu view;
    private final SlotContext context;

    public SpongeAsynchronousPagination(
            ImpactorPaginationBuilder builder,
            CompletableFuture<? extends List<? extends Icon<?>>> accumulator,
            @Nullable Icon<?> waiting,
            @Nullable TimeoutDetails timeout
    ) {
        super(builder, accumulator, waiting, timeout);

        SpongePage page = this.waiting();
        this.pages = CircularLinkedList.of(page);
        this.context = LayoutTranslator.translate(page.toLayout());
        this.view = this.waiting().view().asMenu();
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

    protected abstract <E extends Icon<?>> void consume(List<E> icons);

    @Override
    public void page(int target) {
        super.page(target);

        Page<ViewableInventory> page = (Page<ViewableInventory>) this.pages.at(target - 1);
        this.view.setCurrentInventory(page.view());
        this.context.trackAll(this.offsets(), this.zone(), page.icons());
    }

    @Override
    protected void queue() {
        SchedulerAdapter scheduler = Impactor.getInstance().getScheduler();
        this.accumulator.acceptEither(this.timeoutAfter(this.timeout.time(), this.timeout.unit()), list -> {
            this.consume(list);

            scheduler.executeSync(() -> {
                this.pages = this.define(list);
                this.page(1);
            });
        }).exceptionally(ex -> {
            scheduler.executeSync(() -> {
                this.pages = CircularLinkedList.of(this.timeout());
                this.page(1);
            });
            return null;
        });
    }

    @Override
    public void open() {
        PlatformPlayerManager<ServerPlayer> manager = (PlatformPlayerManager<ServerPlayer>) Impactor.getInstance().getPlatform().playerManager();
        ServerPlayer player = manager.translate(this.viewer).orElseThrow(() -> new IllegalStateException("Player not available or found"));

        this.queue();
        this.view.open(player);
    }

    @Override
    public void close() {
        PlatformPlayerManager<ServerPlayer> manager = (PlatformPlayerManager<ServerPlayer>) Impactor.getInstance().getPlatform().playerManager();
        ServerPlayer player = manager.translate(this.viewer).orElseThrow(() -> new IllegalStateException("Player not available or found"));

        player.closeInventory();
        if(!this.accumulator.isDone()) {
            this.accumulator.cancel(true);
        }
    }

    @Override
    public void refresh(RefreshDetail type) {

    }

    @Override
    protected CircularLinkedList<Page<?>> define(List<? extends Icon<?>> icons) {
        return PageConstructor.construct(icons, this);
    }

    @Override
    protected void setUnsafe(Icon<?> icon, int slot) {
        Icon<ItemStack> i = (Icon<ItemStack>) icon;
        if(icon == null) {
            this.view.inventory().set(slot, ItemStack.empty());
        } else {
            this.view.inventory().set(slot, i.display().provide());
        }
    }

    @Override
    protected Icon<?> waitingIfNotSet() {
        return Icon.builder(ItemStack.class)
                .display(new DisplayProvider.Constant<>(ItemStack.builder()
                        .itemType(ItemTypes.STONE_BUTTON)
                        .add(Keys.CUSTOM_NAME, ComponentManipulator.noItalics(MiniMessage.miniMessage().deserialize("<gradient:yellow:blue>Polling, Please Wait...</gradient>")))
                        .build()
                ))
                .build();
    }

    @Override
    protected Icon<?> timeoutIfNotSet() {
        MessageService service = Impactor.getInstance().getRegistry().get(MessageService.class);
        return Icon.builder(ItemStack.class)
                .display(new DisplayProvider.Constant<>(ItemStack.builder()
                        .itemType(ItemTypes.RED_STAINED_GLASS_PANE)
                        .add(Keys.CUSTOM_NAME, ComponentManipulator.noItalics(MiniMessage.miniMessage().deserialize("<gradient:red:gold>Timed Out...</gradient>")))
                        .add(Keys.LORE, service.parse(Lists.newArrayList(
                                "&7It seems we could not receive",
                                "&7contents in time to be displayed!",
                                "",
                                "&7Please attempt to refresh the page!"
                        )).stream().map(ComponentManipulator::noItalics).collect(Collectors.toList()))
                        .build()
                ))
                .build();
    }

    private SpongePage waiting() {
        ViewableInventory view = ViewableInventory.builder()
                .type(ContainerTypes.GENERIC_9X6)
                .completeStructure()
                .identity(UUID.randomUUID())
                .plugin(SpongeImpactorPlugin.instance().bootstrapper().container())
                .build();

        SpongePage page = new SpongePage(view, Maps.newHashMap());
        page.draw(this, this.fill(this.waiting), this.updaters(), 1, 1);
        return page;
    }

    private SpongePage timeout() {
        ViewableInventory view = ViewableInventory.builder()
                .type(ContainerTypes.GENERIC_9X6)
                .completeStructure()
                .identity(UUID.randomUUID())
                .plugin(SpongeImpactorPlugin.instance().bootstrapper().container())
                .build();

        SpongePage page = new SpongePage(view, Maps.newHashMap());
        page.draw(this, this.fill(this.timeout.filler()), this.updaters(), 1, 1);
        return page;
    }
}
