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

package net.impactdev.impactor.common.ui.containers.pagination.sectioned.pages;

import com.google.common.base.Suppliers;
import net.impactdev.impactor.api.ui.containers.icons.Icon;
import net.impactdev.impactor.api.ui.containers.pagination.sectioned.sections.Section;
import net.impactdev.impactor.api.ui.containers.pagination.sectioned.sections.SectionedPage;
import net.impactdev.impactor.api.ui.containers.pagination.updaters.PageUpdater;
import net.kyori.adventure.util.TriState;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class LazyPageProvider<E extends Icon<?>> implements SectionedPage {

    private final Supplier<SectionedPage> reference;

    public LazyPageProvider(final Supplier<SectionedPage> provider) {
        this.reference = Suppliers.memoize(provider::get);
    }

    @Override
    public Map<Integer, Icon<?>> icons() {
        return this.reference.get().icons();
    }

    @Override
    public Map<Integer, Icon<?>> drawn() {
        return this.reference.get().drawn();
    }

    @Override
    public void draw(Section parent, List<PageUpdater> updaters, TriState style, int page, int maxPages) {
        this.reference.get().draw(parent, updaters, style, page, maxPages);
    }

    @Override
    public void refresh(BiConsumer<Integer, Icon<?>> consumer) {
        this.reference.get().refresh(consumer);
    }
}
