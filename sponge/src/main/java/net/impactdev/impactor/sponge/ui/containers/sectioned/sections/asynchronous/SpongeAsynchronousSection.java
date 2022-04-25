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

package net.impactdev.impactor.sponge.ui.containers.sectioned.sections.asynchronous;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.scheduler.SchedulerAdapter;
import net.impactdev.impactor.api.services.text.MessageService;
import net.impactdev.impactor.api.ui.containers.icons.DisplayProvider;
import net.impactdev.impactor.api.ui.containers.icons.Icon;
import net.impactdev.impactor.api.ui.containers.pagination.components.TimeoutDetails;
import net.impactdev.impactor.api.ui.containers.pagination.sectioned.sections.SectionedPage;
import net.impactdev.impactor.api.ui.containers.pagination.updaters.PageUpdater;
import net.impactdev.impactor.api.utilities.ComponentManipulator;
import net.impactdev.impactor.api.utilities.lists.CircularLinkedList;
import net.impactdev.impactor.common.ui.pagination.sectioned.builders.ImpactorSectionBuilder;
import net.impactdev.impactor.common.ui.pagination.sectioned.sections.AbstractAsynchronousSection;
import net.impactdev.impactor.sponge.SpongeImpactorPlugin;
import net.impactdev.impactor.sponge.ui.containers.sectioned.sections.SpongeSectionedPage;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.util.TriState;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.math.vector.Vector2i;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public abstract class SpongeAsynchronousSection extends AbstractAsynchronousSection {

    public SpongeAsynchronousSection(
            ImpactorSectionBuilder<?> builder,
            CompletableFuture<? extends List<? extends Icon<?>>> accumulator,
            Icon<?> waiting,
            TimeoutDetails timeout
    ) {
        super(builder, accumulator, waiting, timeout);
        this.pages = CircularLinkedList.of(this.fill(this.provide(waiting, this.waitingIfNotSet())));
    }

    protected abstract <E extends Icon<?>> void consume(List<E> icons);

    @Override
    public void queue() {
        SchedulerAdapter scheduler = Impactor.getInstance().getScheduler();
        SpongeImpactorPlugin.instance().logger().info("Queuing pagination contents");
        this.accumulator.acceptEither(this.timeoutAfter(this.timeout.time(), this.timeout.unit()), list -> {
            this.consume(list);
            SpongeImpactorPlugin.instance().logger().info("Contents received, should be updating page");

            scheduler.executeSync(() -> {
                this.pages = this.draft(list);
                this.page(1);
                SpongeImpactorPlugin.instance().logger().info("Printed page");
            });
        }).exceptionally(ex -> {
            scheduler.executeSync(() -> {
                this.pages = CircularLinkedList.of(this.fill(this.provide(this.timeout.filler(), this.timeoutIfNotSet())));
                this.page(1);
            });
            return null;
        });
    }

    @Override
    protected SectionedPage fill(Icon<?> icon) {
        Map<Integer, Icon<?>> icons = Maps.newHashMap();

        Vector2i zone = this.maximum().sub(this.minimum()).add(Vector2i.ONE);
        for(int i = 0; i < zone.x() * zone.y(); i++) {
            icons.put(this.calculateTargetSlot(i, zone, this.minimum()), icon);
        }

        return this.constructPage(this.updaters, this.style, 1, 1, icons);
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
    @SuppressWarnings("DuplicateCode")
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

    @Override
    protected SectionedPage constructPage(List<PageUpdater> updaters, TriState style, int index, int size, Map<Integer, Icon<?>> working) {
        SpongeSectionedPage page = new SpongeSectionedPage(working);
        page.draw(this, updaters, style, index, size);
        return page;
    }
}
