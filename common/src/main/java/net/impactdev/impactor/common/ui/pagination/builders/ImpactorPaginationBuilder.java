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

package net.impactdev.impactor.common.ui.pagination.builders;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.ui.containers.icons.ClickProcessor;
import net.impactdev.impactor.api.ui.containers.icons.Icon;
import net.impactdev.impactor.api.ui.containers.layouts.Layout;
import net.impactdev.impactor.api.ui.containers.pagination.Pagination;
import net.impactdev.impactor.api.ui.containers.pagination.builders.PaginationBuilder;
import net.impactdev.impactor.api.ui.containers.pagination.components.TimeoutDetails;
import net.impactdev.impactor.api.ui.containers.pagination.updaters.PageUpdater;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.math.vector.Vector2i;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public abstract class ImpactorPaginationBuilder implements PaginationBuilder {

    public Key provider;
    public PlatformPlayer viewer;
    public boolean readonly = true;
    public Component title;
    public Layout layout;
    public Vector2i zone;
    public Vector2i offsets;

    public final List<PageUpdater> updaters = Lists.newArrayList();
    public TriState updaterStyle = TriState.NOT_SET;

    public ClickProcessor click;
    public CloseProcessor close;

    @Override
    public PaginationBuilder provider(Key provider) {
        this.provider = provider;
        return this;
    }

    @Override
    public PaginationBuilder viewer(PlatformPlayer viewer) {
        this.viewer = viewer;
        return this;
    }

    @Override
    public PaginationBuilder title(Component title) {
        this.title = title;
        return this;
    }

    @Override
    public PaginationBuilder layout(Layout layout) {
        this.layout = layout;
        return this;
    }

    @Override
    public PaginationBuilder readonly(boolean state) {
        this.readonly = state;
        return this;
    }

    @Override
    public PaginationBuilder zone(Vector2i dimensions, @Nullable Vector2i offset) {
        this.zone = dimensions;
        this.offsets = offset;
        return this;
    }

    @Override
    public PaginationBuilder updater(PageUpdater updater) {
        this.updaters.add(updater);
        return this;
    }

    @Override
    public PaginationBuilder style(TriState state) {
        this.updaterStyle = state;
        return this;
    }

    @Override
    public PaginationBuilder onClick(ClickProcessor processor) {
        this.click = processor;
        return this;
    }

    @Override
    public PaginationBuilder onClose(CloseProcessor processor) {
        this.close = processor;
        return this;
    }

    public interface Provider<T extends Pagination> {

        T complete();

        default void verify(ImpactorPaginationBuilder parent) {
            Preconditions.checkNotNull(parent.provider);
            Preconditions.checkNotNull(parent.viewer);
        }

    }

    public static abstract class ImpactorSynchronousBuilder
            implements Provider<Pagination>,
                        PaginationCompleter<Pagination>,
                        Synchronous.Basic
    {

        public final ImpactorPaginationBuilder parent;
        public List<Icon<?>> contents = Lists.newArrayList();

        public ImpactorSynchronousBuilder(final ImpactorPaginationBuilder parent) {
            this.parent = parent;
        }

        @Override
        public Synchronous.Basic contents(List<Icon<?>> icons) {
            this.contents = icons;
            return this;
        }

        @Override
        public Pagination build() {
            this.verify(this.parent);
            return this.complete();
        }

    }

    public static abstract class ImpactorSynchronousGenericBuilder<T>
            implements Provider<Pagination.Generic<T>>,
                        PaginationCompleter<Pagination.Generic<T>>,
                        Synchronous.Generic<T>
    {

        public final ImpactorPaginationBuilder parent;
        public List<Icon.Binding<?, T>> contents = Lists.newArrayList();
        public Predicate<T> filter = x -> true;
        public Comparator<T> sorter;

        public ImpactorSynchronousGenericBuilder(final ImpactorPaginationBuilder parent) {
            this.parent = parent;
        }

        @Override
        public Generic<T> contents(List<Icon.Binding<?, T>> icons) {
            this.contents = icons;
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

        @Override
        public Pagination.Generic<T> build() {
            this.verify(this.parent);
            return this.complete();
        }
    }

    public static abstract class ImpactorAsynchronousBuilder
            implements Provider<Pagination>,
                        PaginationCompleter<Pagination>,
                        Asynchronous.Basic
    {

        public final ImpactorPaginationBuilder parent;
        public CompletableFuture<List<Icon<?>>> accumulator;
        public TimeoutDetails timeout;
        public Icon<?> waiting;

        public ImpactorAsynchronousBuilder(final ImpactorPaginationBuilder parent) {
            this.parent = parent;
        }

        @Override
        public Basic accumulator(CompletableFuture<List<Icon<?>>> accumulator) {
            this.accumulator = accumulator;
            return this;
        }

        @Override
        public Basic timeout(long time, TimeUnit unit, Icon<?> icon) {
            this.timeout = new TimeoutDetails(icon, time, unit);
            return this;
        }

        @Override
        public Basic waiting(Icon<?> icon) {
            this.waiting = icon;
            return this;
        }

        @Override
        public Pagination build() {
            this.verify(this.parent);
            return this.complete();
        }

    }

    public static abstract class ImpactorAsynchronousGenericBuilder<T>
            implements Provider<Pagination.Generic<T>>,
                        PaginationCompleter<Pagination.Generic<T>>,
                        Asynchronous.Generic<T>
    {

        public final ImpactorPaginationBuilder parent;
        public CompletableFuture<List<Icon.Binding<?, T>>> accumulator;
        public TimeoutDetails timeout;
        public Icon<?> waiting;
        public Predicate<T> filter = x -> true;
        public Comparator<T> comparator;

        public ImpactorAsynchronousGenericBuilder(final ImpactorPaginationBuilder parent) {
            this.parent = parent;
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
            this.comparator = sorter;
            return this;
        }

        @Override
        public Generic<T> timeout(long time, TimeUnit unit, Icon<?> icon) {
            this.timeout = new TimeoutDetails(icon, time, unit);
            return this;
        }

        @Override
        public Generic<T> waiting(Icon<?> icon) {
            this.waiting = icon;
            return this;
        }

        @Override
        public Pagination.Generic<T> build() {
            this.verify(this.parent);
            return this.complete();
        }

    }

}
