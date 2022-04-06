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

package net.impactdev.impactor.common.ui.pagination.sectioned;

import com.google.common.collect.ImmutableSet;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.ui.icons.Icon;
import net.impactdev.impactor.api.ui.layouts.Layout;
import net.impactdev.impactor.api.ui.pagination.sectioned.SectionedPage;
import net.impactdev.impactor.api.ui.pagination.sectioned.SectionedPagination;
import net.impactdev.impactor.api.ui.pagination.updaters.PageUpdater;
import net.impactdev.impactor.api.utilities.lists.CircularLinkedList;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.util.TriState;
import org.spongepowered.math.vector.Vector2i;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public abstract class AbstractSectionedPagination implements SectionedPagination {

    private final Key provider;
    private final Layout layout;

    protected final PlatformPlayer viewer;
    protected final Set<Section> sections;

    protected AbstractSectionedPagination(Key provider, PlatformPlayer viewer, Layout layout, Set<Section> sections) {
        this.provider = provider;
        this.viewer = viewer;
        this.layout = layout;
        this.sections = sections;
        for(Section section : sections) {
            ((AbstractSection) section).assignTo(this);
        }
    }

    @Override
    public Key provider() {
        return this.provider;
    }

    @Override
    public Layout layout() {
        return this.layout;
    }

    protected Set<Section> sections() {
        return ImmutableSet.copyOf(sections);
    }

    @Override
    public Optional<Section> at(int slot) {
        return this.sections.stream()
                .filter(section -> {
                    Vector2i location = new Vector2i(slot % 9, slot / 9);
                    return this.greaterOrEqual(location, section.minimum()) &&
                            this.lessOrEqual(location, section.maximum());
                })
                .findAny();
    }

    private boolean greaterOrEqual(Vector2i query, Vector2i compare) {
        return query.x() >= compare.x() && query.y() >= compare.y();
    }

    private boolean lessOrEqual(Vector2i query, Vector2i compare) {
        return query.x() <= compare.x() && query.y() <= compare.y();
    }

    public static abstract class AbstractSection implements Section {

        private final Vector2i min;
        private final Vector2i max;

        private final CircularLinkedList<SectionedPage> pages;
        private final List<PageUpdater> updaters;
        private final TriState style;

        public AbstractSection(Vector2i min, Vector2i max, List<Icon<?>> icons, List<PageUpdater> updaters, TriState style) {
            this.min = min;
            this.max = max;

            this.updaters = updaters;
            this.style = style;
            this.pages = this.draft(icons);
        }

        @Override
        public Vector2i minimum() {
            return this.min;
        }

        @Override
        public Vector2i maximum() {
            return this.max;
        }

        @Override
        public CircularLinkedList<SectionedPage> pages() {
            return this.pages;
        }

        protected abstract void assignTo(SectionedPagination parent);

        protected CircularLinkedList<SectionedPage> draft(List<Icon<?>> icons) {
            CircularLinkedList<SectionedPage> result = CircularLinkedList.of();

            Vector2i zone = this.max.sub(this.min).add(Vector2i.ONE);
            int max = zone.y() * zone.x();
            int size = icons.size() / max + (icons.size() % max == 0 ? 0 : 1);

            int slot = 0;
            Map<Integer, Icon<?>> working = new HashMap<>();
            for(Icon<?> icon : icons) {
                if(slot < max) {
                    int target = this.calculateTargetSlot(slot, zone, this.min);
                    working.put(target, icon);
                    ++slot;
                } else {
                    int target = this.calculateTargetSlot(0, zone, this.min);

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

            }

            return result;
        }

        protected abstract SectionedPage constructPage(List<PageUpdater> updaters, TriState style,
                                              int index, int size, Map<Integer, Icon<?>> working);
    }
}
