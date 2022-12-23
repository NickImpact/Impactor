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

package net.impactdev.impactor.minecraft.ui.containers.views.pagination.layers;

import com.google.common.collect.Maps;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.ui.containers.Icon;
import net.impactdev.impactor.api.ui.containers.views.pagination.Page;
import net.impactdev.impactor.api.ui.containers.views.pagination.updaters.PageUpdater;
import net.impactdev.impactor.api.ui.containers.views.pagination.updaters.PageUpdaterType;
import net.impactdev.impactor.api.utility.builders.Builder;
import net.impactdev.impactor.minecraft.ui.containers.views.pagination.PaginatedView;
import net.kyori.adventure.util.TriState;
import org.spongepowered.math.vector.Vector2i;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ImpactorPage implements Page {

    private final int index;
    private final Map<Integer, Icon> icons;

    public ImpactorPage(final ImpactorPageBuilder builder) {
        this.index = builder.index;
        this.icons = this.craft(builder);
    }

    @Override
    public int index() {
        return this.index;
    }

    @Override
    public Map<Integer, Icon> icons() {
        return this.icons;
    }

    private Map<Integer, Icon> craft(final ImpactorPageBuilder builder) {
        final Map<Integer, Icon> results = Maps.newHashMap();
        builder.updaters.forEach(updater -> {
            switch (updater.type()) {
                case PREVIOUS:
                case FIRST:
                    if (this.index == 1 && builder.style == TriState.FALSE) {
                        return;
                    }
                    break;
                case NEXT:
                case LAST:
                    if (this.index == builder.pages && builder.style == TriState.FALSE) {
                        return;
                    }
                    break;
            }

            int target = updater.type().translate(this.index, builder.pages);
            Icon icon = Icon.builder()
                    .display(() -> updater.provider().provide(target))
                    .listener(processor -> {
                        if(!updater.type().equals(PageUpdaterType.CURRENT)) {
                            if(target == this.index) {
                                return false;
                            }

                            builder.view.page(target);
                        }
                        return false;
                    })
                    .build();
            results.put(updater.slot(), icon);
        });

        int slot = 0;
        for(Icon icon : builder.contents) {
            results.put(this.calculateTargetSlot(slot++, builder.zone, builder.offsets), icon);
        }

        for(; slot < builder.zone.x() * builder.zone.y(); slot++) {
            results.put(this.calculateTargetSlot(slot, builder.zone, builder.offsets), null);
        }

        return results;
    }

    /**
     * Calculates the location an icon should be placed based on the target index,
     * alongside the content zone with grid and its offsets. Note that the target is not
     * the slot, but rather the index from 0.
     *
     * @param target The index of the content being placed into the pagination
     * @param zone The grid size for the pagination zone
     * @param offsets The offsets of the pagination zone
     * @return The calculated slot position in the pagination zone
     */
    private int calculateTargetSlot(int target, Vector2i zone, Vector2i offsets) {
        int x = target % zone.x();
        int y = target / zone.x();

        Vector2i result = Vector2i.from(x, y).add(offsets);
        return result.x() + (9 * result.y());
    }

    public static ImpactorPageBuilder builder() {
        return Impactor.instance().builders().provide(ImpactorPageBuilder.class);
    }

    public static class ImpactorPageBuilder implements Builder<ImpactorPage> {

        private List<Icon> contents = Collections.EMPTY_LIST;
        private int index;

        private PaginatedView view;
        private Set<PageUpdater> updaters;
        private int pages;
        private Vector2i zone;
        private Vector2i offsets;
        private TriState style;

        public ImpactorPageBuilder contents(final List<Icon> icons) {
            this.contents = icons;
            return this;
        }

        public ImpactorPageBuilder index(final int index) {
            this.index = index;
            return this;
        }

        public ImpactorPageBuilder parent(final PaginatedView view) {
            this.view = view;
            return this;
        }

        public ImpactorPageBuilder updaters(final Set<PageUpdater> updaters) {
            this.updaters = updaters;
            return this;
        }

        public ImpactorPageBuilder pages(final int pages) {
            this.pages = pages;
            return this;
        }

        public ImpactorPageBuilder zone(final Vector2i zone) {
            this.zone = zone;
            return this;
        }

        public ImpactorPageBuilder offsets(final Vector2i offsets) {
            this.offsets = offsets;
            return this;
        }

        public ImpactorPageBuilder style(final TriState style) {
            this.style = style;
            return this;
        }

        @Override
        public ImpactorPage build() {
            return new ImpactorPage(this);
        }
    }

}
