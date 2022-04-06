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

package net.impactdev.impactor.common.ui.pagination.async;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.scheduler.SchedulerAdapter;
import net.impactdev.impactor.api.ui.icons.Icon;
import net.impactdev.impactor.api.ui.layouts.Layout;
import net.impactdev.impactor.api.ui.pagination.Page;
import net.impactdev.impactor.api.ui.pagination.async.AsyncPagination;
import net.impactdev.impactor.api.ui.pagination.updaters.PageUpdater;
import net.impactdev.impactor.api.utilities.lists.CircularLinkedList;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.math.vector.Vector2i;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class AbstractAsyncPagination implements AsyncPagination {

    private final Key provider;
    private final Layout layout;

    protected final PlatformPlayer viewer;
    protected CircularLinkedList<Page<?>> pages = CircularLinkedList.of();

    protected final Component title;
    protected final boolean readonly;

    protected final CompletableFuture<List<Icon<?>>> accumulator;
    protected final Vector2i zone;
    protected final Vector2i offsets;

    protected final List<PageUpdater> updaters;
    protected final TriState style;

    protected final Icon<?> waiting;
    protected final TimeoutDetails timeout;

    private int page = 1;

    public AbstractAsyncPagination(AbstractAsyncPaginationBuilder builder) {
        this.provider = builder.provider;
        this.viewer = builder.viewer;
        this.layout = builder.layout;
        this.title = builder.title;
        this.readonly = builder.readonly;
        this.timeout = this.provide(builder.details, new TimeoutDetails(this.timeoutIfNotSet(), 5, TimeUnit.SECONDS));

        this.accumulator = builder.accumulator;
        this.zone = this.provide(builder.zone, Vector2i.ZERO);
        this.offsets = this.provide(builder.offsets, Vector2i.ZERO);
        this.waiting = this.provide(builder.waiting, this.waitingIfNotSet());

        this.updaters = builder.updaters;
        this.style = builder.style;
    }

    @Override
    public Key provider() {
        return this.provider;
    }

    @Override
    public Component title() {
        return this.title;
    }

    @Override
    public Layout layout() {
        return this.layout;
    }

    @Override
    public CircularLinkedList<Page<?>> pages() {
        return this.pages;
    }

    @Override
    public Vector2i zone() {
        return this.zone;
    }

    @Override
    public Vector2i offsets() {
        return this.offsets;
    }

    @Override
    public List<PageUpdater> updaters() {
        return this.updaters;
    }

    @Override
    public TriState style() {
        return this.style;
    }

    @Override
    public void page(int target) {
        this.page = target;
    }

    @Override
    public int page() {
        return this.page;
    }

    @Override
    public boolean set(@Nullable Icon<?> icon, int slot) {
        if(!this.within(slot)) {
            this.setUnsafe(icon, slot);
            return true;
        }

        return false;
    }

    protected void queue() {
        SchedulerAdapter scheduler = Impactor.getInstance().getScheduler();
        this.accumulator.acceptEither(this.timeoutAfter(this.timeout.time(), this.timeout.unit()), list -> {
            scheduler.executeSync(() -> {
                this.pages = this.define(list);
                this.page(1);
            });
        }).exceptionally(ex -> {
            scheduler.executeSync(() -> this.fill(this.timeout.filler()));
            return null;
        });
    }

    /**
     * Calculates the pages that will be defined based on the result of the future.
     *
     * @param icons The list of icons generated by the accumulator
     * @return A set of pages managed by a {@link CircularLinkedList}
     */
    protected abstract CircularLinkedList<Page<?>> define(List<Icon<?>> icons);

    /**
     * Fills the content zone with the given icon. This overwrites whatever is within
     * the content zone regardless of actual pagination content, a previous fill, or
     * simply an unset slot.
     *
     * @param icon The icon to fill the zone with.
     */
    protected Layout fill(Icon<?> icon) {
        return Layout.builder()
                .from(this.layout())
                .rectangle(
                        icon,
                        this.zone,
                        this.offsets,
                        false
                )
                .build();
    }

    /**
     * Places an icon at the target slot, but ignores the rules of setting icons within
     * the content zone. By default, {@link #set(Icon, int)} should consider the bounds
     * of the content zone, and disallow setting of icons within that territory, in favor
     * of the actual pagination content. This call is provided outside of the interface
     * as this should only be handled by the implementation.
     *
     * @param icon The icon to set at the target slot
     * @param slot The slot to place the icon at
     */
    protected abstract void setUnsafe(Icon<?> icon, int slot);

    /**
     * If not set, provide an icon that will act as the waiting display to the client
     * within the view.
     *
     * @return The icon to represent the waiting state
     */
    protected abstract Icon<?> waitingIfNotSet();

    /**
     * If not set, provide an icon that will act as the timeout display to the client
     * within the view.
     *
     * @return The icon to represent the timeout state
     */
    protected abstract Icon<?> timeoutIfNotSet();

    private <W> CompletableFuture<W> timeoutAfter(long timeout, TimeUnit unit) {
        CompletableFuture<W> result = new CompletableFuture<>();
        Impactor.getInstance().getScheduler().asyncLater(() -> result.completeExceptionally(new TimeoutException()), timeout, unit);
        return result;
    }

    private <T> T provide(@Nullable T value, T def) {
        return Optional.ofNullable(value).orElse(def);
    }

    private boolean within(int slot) {
        Vector2i location = new Vector2i(slot % 9, slot / 9);
        return this.greaterOrEqual(location, this.offsets) &&
                this.lessOrEqual(location, this.zone.sub(this.offsets));
    }

    private boolean greaterOrEqual(Vector2i query, Vector2i compare) {
        return query.x() >= compare.x() && query.y() >= compare.y();
    }

    private boolean lessOrEqual(Vector2i query, Vector2i compare) {
        return query.x() <= compare.x() && query.y() <= compare.y();
    }

    protected static class TimeoutDetails {

        private final Icon<?> filler;

        private final long time;
        private final TimeUnit unit;

        public TimeoutDetails(Icon<?> filler, long time, TimeUnit unit) {
            this.filler = filler;
            this.time = time;
            this.unit = unit;
        }

        public Icon<?> filler() {
            return this.filler;
        }

        public long time() {
            return this.time;
        }

        public TimeUnit unit() {
            return this.unit;
        }
    }

    public static abstract class AbstractAsyncPaginationBuilder implements AsyncPaginationBuilder {

        private Key provider;
        private PlatformPlayer viewer;

        private Component title;
        private Layout layout;
        private boolean readonly = true;

        private CompletableFuture<List<Icon<?>>> accumulator;
        private Vector2i zone;
        private Vector2i offsets;

        private List<PageUpdater> updaters = Lists.newArrayList();
        private TriState style = TriState.NOT_SET;

        private Icon<?> waiting;
        private TimeoutDetails details;

        @Override
        public AsyncPaginationBuilder title(Component title) {
            this.title = title;
            return this;
        }

        @Override
        public AsyncPaginationBuilder layout(Layout layout) {
            this.layout = layout;
            return this;
        }

        @Override
        public AsyncPaginationBuilder readonly(boolean state) {
            this.readonly = state;
            return this;
        }

        @Override
        public AsyncPaginationBuilder provider(Key key) {
            this.provider = key;
            return this;
        }

        @Override
        public AsyncPaginationBuilder viewer(PlatformPlayer viewer) {
            this.viewer = viewer;
            return this;
        }

        @Override
        public AsyncPaginationBuilder accumulator(CompletableFuture<List<Icon<?>>> provider) {
            this.accumulator = provider;
            return this;
        }

        @Override
        public AsyncPaginationBuilder zone(Vector2i dimensions) {
            this.zone = dimensions;
            return this;
        }

        @Override
        public AsyncPaginationBuilder zone(Vector2i dimensions, @Nullable Vector2i offset) {
            this.zone = dimensions;
            this.offsets = offset;
            return this;
        }

        @Override
        public AsyncPaginationBuilder updater(PageUpdater updater) {
            this.updaters.add(updater);
            return this;
        }

        @Override
        public AsyncPaginationBuilder style(TriState state) {
            this.style = state;
            return this;
        }

        @Override
        public AsyncPaginationBuilder timeout(long time, TimeUnit unit, Icon<?> icon) {
            this.details = new TimeoutDetails(icon, time, unit);
            return this;
        }

        @Override
        public AsyncPaginationBuilder waiting(Icon<?> icon) {
            this.waiting = icon;
            return this;
        }

        @Override
        public AsyncPaginationBuilder from(AsyncPagination input) {
            return this;
        }

        @Override
        public AsyncPagination build() {
            Preconditions.checkNotNull(this.provider, "provider");
            Preconditions.checkNotNull(this.viewer, "viewer");
            Preconditions.checkNotNull(this.accumulator, "accumulator");

            return this.complete();
        }

        protected abstract AsyncPagination complete();
    }
}
