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

import io.leangen.geantyref.TypeToken;
import net.impactdev.impactor.api.ui.containers.icons.Icon;
import net.impactdev.impactor.api.ui.containers.pagination.Pagination;
import net.impactdev.impactor.sponge.ui.containers.pagination.builders.SpongeImpactorPaginationBuilder;
import net.impactdev.impactor.sponge.ui.containers.utility.PageConstructor;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpongeAsynchronousGenericPagination<T>
        extends SpongeAsynchronousPagination
        implements Pagination.Generic<T>
{

    private List<Icon.Binding<?, T>> icons;
    private Predicate<T> filter;
    private Comparator<T> sorter;

    public SpongeAsynchronousGenericPagination(SpongeImpactorPaginationBuilder.SpongeAsynchronousGenericPaginationBuilder<T> builder) {
        super(builder.parent, builder.accumulator, builder.waiting, builder.timeout);

        this.filter = builder.filter;
        this.sorter = builder.comparator;
    }

    @Override
    public TypeToken<T> typing() {
        return new TypeToken<T>() {};
    }

    @Override
    public void filter(Predicate<T> predicate) {
        this.filter = predicate;
    }

    @Override
    public void sort(Comparator<T> comparator) {
        this.sorter = comparator;
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
        this.pages = PageConstructor.construct(results, this);
        this.page(Math.min(this.page(), this.pages.size()));
    }

    @Override
    protected <E extends Icon<?>> void consume(List<E> icons) {
        this.icons = (List<Icon.Binding<?, T>>) icons;
    }
}
