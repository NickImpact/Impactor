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

package net.impactdev.impactor.common.ui.pagination.sectioned.pages;

import com.google.common.collect.Maps;
import net.impactdev.impactor.api.ui.containers.icons.Icon;
import net.impactdev.impactor.api.ui.containers.pagination.sectioned.sections.Section;
import net.impactdev.impactor.api.ui.containers.pagination.sectioned.sections.SectionedPage;
import net.impactdev.impactor.api.ui.containers.pagination.sectioned.SectionedPagination;
import net.impactdev.impactor.api.ui.containers.pagination.updaters.PageUpdater;
import net.kyori.adventure.util.TriState;

import java.util.List;
import java.util.Map;

public abstract class AbstractSectionedPage implements SectionedPage {

    private final Map<Integer, Icon<?>> icons;
    private Map<Integer, Icon<?>> drawn;

    public AbstractSectionedPage(Map<Integer, Icon<?>> icons) {
        this.icons = icons;
    }

    @Override
    public Map<Integer, Icon<?>> icons() {
        return this.icons;
    }

    @Override
    public Map<Integer, Icon<?>> drawn() {
        return this.drawn;
    }

    @Override
    public void refresh() {}

    @Override
    public void draw(Section parent, List<PageUpdater> updaters,
                     TriState style, int page, int maxPages) {
        this.drawn = Maps.newHashMap();
        updaters.forEach(updater -> {
            switch (updater.type()) {
                case PREVIOUS:
                case FIRST:
                    if (page == 1 && style == TriState.FALSE) {
                        return;
                    }
                    break;
                case NEXT:
                case LAST:
                    if (page == maxPages && style == TriState.FALSE) {
                        return;
                    }
                    break;
            }

            int target = updater.type().translate(page, maxPages);

            Icon<?> icon = this.updater(parent, updater, page, target);
            this.drawn.put(updater.slot(), icon);
        });
        this.drawn.putAll(this.icons);
    }

    protected abstract Icon<?> updater(Section parent, PageUpdater updater, int page, int target);

}
