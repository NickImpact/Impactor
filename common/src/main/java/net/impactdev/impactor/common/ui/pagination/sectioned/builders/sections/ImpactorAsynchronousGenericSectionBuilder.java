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

package net.impactdev.impactor.common.ui.pagination.sectioned.builders.sections;

import net.impactdev.impactor.api.ui.containers.icons.Icon;
import net.impactdev.impactor.api.ui.containers.pagination.components.TimeoutDetails;
import net.impactdev.impactor.api.ui.containers.pagination.sectioned.builders.SectionBuilder;
import net.impactdev.impactor.common.ui.pagination.sectioned.builders.ImpactorSectionBuilder;
import net.impactdev.impactor.common.ui.pagination.sectioned.builders.ImpactorSectionedPaginationBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public abstract class ImpactorAsynchronousGenericSectionBuilder<T>
            extends ImpactorSectionBuilder<SectionBuilder.Asynchronous.Generic<T>>
            implements SectionBuilder.Asynchronous.Generic<T>
{

    public CompletableFuture<List<Icon.Binding<?, T>>> accumulator;
    public Icon<?> waiting;
    public TimeoutDetails timeout;
    public Predicate<T> filter;
    public Comparator<T> sorter;

    public ImpactorAsynchronousGenericSectionBuilder(ImpactorSectionedPaginationBuilder parent) {
        super(parent);
    }

    @Override
    public Generic<T> waiting(Icon<?> filler) {
        this.waiting = filler;
        return this;
    }

    @Override
    public Generic<T> timeout(long amount, TimeUnit unit, @Nullable Icon<?> filler) {
        this.timeout = new TimeoutDetails(filler, amount, unit);
        return this;
    }

    @Override
    public Generic<T> accumulator(CompletableFuture<List<Icon.Binding<?, T>>> accumulator) {
        this.accumulator = accumulator;
        return this;
    }

    @Override
    public Generic<T> filter(Predicate<T> filter) {
        this.filter = filter;
        return this;
    }

    @Override
    public Generic<T> sort(Comparator<T> sorter) {
        this.sorter = sorter;
        return this;
    }

}
