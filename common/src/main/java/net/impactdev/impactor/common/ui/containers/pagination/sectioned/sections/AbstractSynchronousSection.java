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

package net.impactdev.impactor.common.ui.containers.pagination.sectioned.sections;

import com.google.common.collect.Lists;
import net.impactdev.impactor.api.ui.containers.icons.ClickContext;
import net.impactdev.impactor.api.ui.containers.icons.Icon;
import net.impactdev.impactor.api.ui.containers.pagination.sectioned.sections.Section;
import net.impactdev.impactor.api.ui.containers.pagination.sectioned.sections.SectionedPage;
import net.impactdev.impactor.api.ui.containers.pagination.updaters.PageUpdater;
import net.impactdev.impactor.api.utilities.lists.CircularLinkedList;
import net.impactdev.impactor.common.ui.containers.pagination.builders.PageConstructionDetails;
import net.impactdev.impactor.common.ui.containers.pagination.sectioned.builders.ImpactorSectionBuilder;
import net.impactdev.impactor.common.ui.containers.pagination.sectioned.pages.LazyPageProvider;
import net.kyori.adventure.util.TriState;
import org.spongepowered.math.vector.Vector2i;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class AbstractSynchronousSection extends AssignableSection implements Section {

    protected CircularLinkedList<SectionedPage> pages;

    private final Vector2i minimum;
    private final Vector2i maximum;
    protected final List<PageUpdater> updaters;
    protected final TriState style;

    private int page = 1;

    public AbstractSynchronousSection(ImpactorSectionBuilder<?> builder) {
        this.minimum = builder.minimum;
        this.maximum = builder.maximum;
        this.updaters = builder.updaters;
        this.style = builder.style;
    }

    @Override
    public Vector2i minimum() {
        return this.minimum;
    }

    @Override
    public Vector2i maximum() {
        return this.maximum;
    }

    @Override
    public int page() {
        return this.page;
    }

    @Override
    public void page(int target) {
        this.page = target;

        SectionedPage page = this.pages.at(this.page - 1);
        Map<Integer, Icon<?>> icons = new HashMap<>(page.drawn());
        Vector2i zone = this.maximum.sub(this.minimum).add(Vector2i.ONE);
        for(int i = 0; i < zone.x() * zone.y(); i++) {
            int slot = this.calculateTargetSlot(i, zone, this.minimum);
            this.parent.setUnsafe(icons.remove(slot), slot);
        }

        for(Map.Entry<Integer, Icon<?>> entry : icons.entrySet()) {
            this.parent.setUnsafe(entry.getValue(), entry.getKey());
        }
    }

    @Override
    public CircularLinkedList<SectionedPage> pages() {
        return this.pages;
    }

    protected <E extends Icon<?>> CircularLinkedList<SectionedPage> draft(List<E> icons) {
        CircularLinkedList<SectionedPage> result = CircularLinkedList.of();

        Vector2i zone = this.maximum.sub(this.minimum).add(Vector2i.ONE);
        int max = zone.y() * zone.x();

        int index = 0;
        int page = 1;
        while(index < icons.size()) {
            result.append(this.page(icons, page++, index, index + max));
            index += max;
        }

        if(result.empty()) {
            result.append(this.page(Lists.newArrayList(), 1, 0, 0));
        }

        return result;
    }

    @Override
    public void refresh(BiConsumer<Integer, Icon<?>> consumer) {
        this.pages.at(this.page() - 1).refresh(consumer);
    }

    private <E extends Icon<?>> LazyPageProvider<E> page(List<E> icons, int page, int start, int end) {
        PageConstructionDetails<E> details = PageConstructionDetails.<E>create()
                .icons(icons)
                .zone(this.maximum.sub(this.minimum).add(Vector2i.ONE))
                .offsets(this.minimum)
                .page(page)
                .total(icons.size() / Math.max(1, end - start) + 1)
                .indexes(start, end)
                .updaters(this.updaters)
                .style(this.style);
        return new LazyPageProvider<>(() -> this.constructPage(details));
    }

    protected abstract <E extends Icon<?>> SectionedPage constructPage(PageConstructionDetails<E> details);

    public abstract void appendToClickContext(ClickContext context);

    public abstract void handleClose();

}
