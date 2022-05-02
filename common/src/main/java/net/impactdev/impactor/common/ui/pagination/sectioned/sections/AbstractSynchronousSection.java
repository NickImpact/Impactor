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

package net.impactdev.impactor.common.ui.pagination.sectioned.sections;

import com.google.common.collect.Maps;
import net.impactdev.impactor.api.ui.containers.icons.ClickContext;
import net.impactdev.impactor.api.ui.containers.icons.Icon;
import net.impactdev.impactor.api.ui.containers.pagination.sectioned.sections.Section;
import net.impactdev.impactor.api.ui.containers.pagination.sectioned.sections.SectionedPage;
import net.impactdev.impactor.api.ui.containers.pagination.updaters.PageUpdater;
import net.impactdev.impactor.api.utilities.lists.CircularLinkedList;
import net.impactdev.impactor.common.ui.pagination.sectioned.builders.ImpactorSectionBuilder;
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

    protected  <E extends Icon<?>> CircularLinkedList<SectionedPage> draft(List<E> icons) {
        CircularLinkedList<SectionedPage> result = CircularLinkedList.of();

        Vector2i zone = this.maximum.sub(this.minimum).add(Vector2i.ONE);
        int max = zone.y() * zone.x();
        int size = icons.size() / max + (icons.size() % max == 0 ? 0 : 1);

        int slot = 0;
        Map<Integer, Icon<?>> working = new HashMap<>();
        for(Icon<?> icon : icons) {
            if(slot < max) {
                int target = this.calculateTargetSlot(slot, zone, this.minimum);
                working.put(target, icon);
                ++slot;
            } else {
                int target = this.calculateTargetSlot(0, zone, this.maximum);

                result.append(this.constructPage(this.updaters, this.style, result.size() + 1, size, working));
                working = new HashMap<>();
                working.put(target, icon);
                slot = 1;
            }
        }

        if(!working.isEmpty()) {
            result.append(this.constructPage(this.updaters, this.style, result.size() + 1, size, working));
        }

        if(result.empty()) {
            result.append(this.constructPage(this.updaters, this.style, 1, 1, Collections.emptyMap()));
        }

        return result;
    }

    @Override
    public void refresh(BiConsumer<Integer, Icon<?>> consumer) {
        this.pages.at(this.page() - 1).refresh(consumer);
    }

    protected abstract SectionedPage constructPage(List<PageUpdater> updaters, TriState style, int index, int size, Map<Integer, Icon<?>> working);

    public abstract void appendToClickContext(ClickContext context);
}
