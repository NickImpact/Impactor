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

package net.impactdev.impactor.sponge.ui.containers.sectioned.sections.synchronous;

import io.leangen.geantyref.TypeToken;
import net.impactdev.impactor.api.ui.containers.icons.ClickContext;
import net.impactdev.impactor.api.ui.containers.icons.Icon;
import net.impactdev.impactor.api.ui.containers.pagination.sectioned.sections.Section;
import net.impactdev.impactor.common.ui.pagination.sectioned.builders.sections.ImpactorSynchronousGenericSectionBuilder;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpongeSynchronousGenericSection<T> extends SpongeSynchronousSection implements Section.Generic<T> {

    private final List<Icon.Binding<?, T>> icons;
    private Predicate<T> filter;
    private Comparator<T> sorter;

    public SpongeSynchronousGenericSection(ImpactorSynchronousGenericSectionBuilder<T> builder) {
        super(builder, builder.contents);
        this.icons = builder.contents;
    }

    @Override
    public void filter(Predicate<T> filter) {
        this.filter = filter;
        this.redraw();
    }

    @Override
    public void sorter(Comparator<T> comparator) {
        this.sorter = comparator;
        this.redraw();
    }

    @SuppressWarnings("DuplicatedCode")
    private void redraw() {
        Stream<Icon.Binding<?, T>> translated = this.icons.stream();

        if(this.filter != null) {
            translated = translated.filter(icon -> this.filter.test(icon.binding()));
        }

        if(this.sorter != null) {
            translated = translated.sorted((i1, i2) -> this.sorter.compare(i1.binding(), i2.binding()));
        }

        List<Icon.Binding<?, T>> results = translated.collect(Collectors.toList());
        this.pages = this.draft(results);
        this.page(Math.min(this.page(), this.pages.size()));
    }

    @Override
    public void appendToClickContext(ClickContext context) {
        context.append(new TypeToken<Section.Generic<T>>() {}, this);
    }
}
