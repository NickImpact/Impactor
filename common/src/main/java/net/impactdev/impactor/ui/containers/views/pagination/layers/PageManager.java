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

package net.impactdev.impactor.ui.containers.views.pagination.layers;

import net.impactdev.impactor.api.ui.containers.Icon;
import net.impactdev.impactor.api.ui.containers.views.pagination.Page;
import net.impactdev.impactor.api.utilities.lists.CircularLinkedList;
import net.impactdev.impactor.plugin.BaseImpactorPlugin;
import net.impactdev.impactor.ui.containers.views.pagination.PaginatedView;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PageManager {

    private final PaginatedView parent;
    private CircularLinkedList<Page> pages;
    private int page = 1;

    public PageManager(PaginatedView parent) {
        this.parent = parent;
        this.pages = this.generatePages();
    }

    public CircularLinkedList<Page> pages() {
        return this.pages;
    }

    public int page() {
        return this.page;
    }

    public void page(int page) {
        this.page = page;
        this.pages.advanceTo(page - 1);
    }

    public void update() {
        this.pages = this.generatePages();
        this.parent.page(Math.min(this.page, this.pages.size()));
    }

    public CircularLinkedList<Page> generatePages() {
        int zone = this.parent.zone().x() * this.parent.zone().y();
        int pages = this.parent.contents().size() / zone + (this.parent.contents().size() % zone == 0 ? 0 : 1);

        List<Icon> focus = this.parent.ruleset().apply(this.parent.contents().stream()).collect(Collectors.toList());
        CircularLinkedList<Page> results = CircularLinkedList.of();
        for(int i = 0; i < pages; i++) {
            ImpactorPage page = ImpactorPage.builder()
                    .parent(this.parent)
                    .contents(focus.subList(i * zone, Math.min(focus.size(), (i + 1) * zone)))
                    .index(i + 1)
                    .updaters(this.parent.updaters())
                    .pages(pages)
                    .zone(this.parent.zone())
                    .offsets(this.parent.offsets())
                    .build();
            results.append(page);
        }

        if(results.empty()) {
            ImpactorPage page = ImpactorPage.builder()
                    .parent(this.parent)
                    .contents(Collections.EMPTY_LIST)
                    .index(1)
                    .updaters(this.parent.updaters())
                    .pages(pages)
                    .zone(this.parent.zone())
                    .offsets(this.parent.offsets())
                    .build();
            results.append(page);
        }

        return results;
    }

}
