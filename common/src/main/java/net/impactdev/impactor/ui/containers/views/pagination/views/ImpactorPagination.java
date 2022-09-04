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

package net.impactdev.impactor.ui.containers.views.pagination.views;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.ui.containers.Icon;
import net.impactdev.impactor.api.ui.containers.layouts.ChestLayout;
import net.impactdev.impactor.api.ui.containers.views.pagination.Pagination;
import net.impactdev.impactor.api.ui.containers.views.pagination.builders.PaginationBuilder;
import net.impactdev.impactor.api.ui.containers.views.pagination.Page;
import net.impactdev.impactor.api.ui.containers.views.pagination.rules.ContextRuleset;
import net.impactdev.impactor.api.ui.containers.views.pagination.updaters.PageUpdater;
import net.impactdev.impactor.api.utilities.lists.CircularLinkedList;
import net.impactdev.impactor.ui.containers.views.builders.ImpactorBaseViewBuilder;
import net.impactdev.impactor.ui.containers.views.layers.ImpactorView;
import net.impactdev.impactor.ui.containers.views.pagination.ImpactorContextRuleset;
import net.impactdev.impactor.ui.containers.views.pagination.PaginatedView;
import net.impactdev.impactor.ui.containers.views.pagination.layers.PageManager;
import net.impactdev.impactor.ui.containers.views.service.ViewingService;
import net.kyori.adventure.util.TriState;
import org.checkerframework.common.value.qual.IntRange;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.math.vector.Vector2i;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ImpactorPagination extends ImpactorView implements Pagination, PaginatedView {

    private final PlatformPlayer viewer;
    private final ChestLayout layout;
    private final Vector2i zone;
    private final Vector2i offsets;
    private final Set<PageUpdater> updaters;
    private final TriState style;

    private final ContextRuleset ruleset;
    private final ViewingService provider;

    private final List<Icon> contents;
    private final PageManager manager;

    private ImpactorPagination(ImpactorPaginationBuilder builder) {
        super(builder.namespace, builder.title, builder.readonly, builder.click, builder.close);
        this.viewer = builder.viewer;
        this.layout = builder.layout;
        this.zone = builder.zone;
        this.offsets = builder.offset;
        this.updaters = builder.updaters;
        this.style = builder.style;

        this.contents = builder.contents;
        this.ruleset = Optional.ofNullable(((ImpactorContextRuleset) builder.ruleset))
                .map(rules -> rules.with(this))
                .orElse(new ImpactorContextRuleset());
        this.provider = Impactor.instance().services().provide(ViewingService.class);
        this.manager = new PageManager(this);
    }

    @Override
    public List<Icon> contents() {
        return this.contents;
    }

    @Override
    public ChestLayout layout() {
        return this.layout;
    }

    @Override
    public @IntRange(from = 1, to = 6) int rows() {
        return this.layout.dimensions().y();
    }

    @Override
    public void set(@Nullable Icon icon, int slot) {
        this.provider.set(icon, slot);
    }

    @Override
    public void refresh(Vector2i dimensions, Vector2i offsets) {
        Page current = this.manager.pages().current();

        int mx = dimensions.x() + offsets.x();
        int my = dimensions.y() + offsets.y();

        for(int row = offsets.y(); row < Math.min(this.rows(), my); row++) {
            for(int column = offsets.x(); column < Math.min(9, mx); column++) {
//                this.set(current.icons().get());
            }
        }
    }

    @Override
    public void open() {
        this.provider.open(this, this.viewer);
    }

    @Override
    public void close() {
        this.provider.close(this.viewer);
    }

    @Override
    public ContextRuleset ruleset() {
        return this.ruleset;
    }

    @Override
    public CircularLinkedList<Page> pages() {
        return this.manager.pages();
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
    public int page() {
        return this.manager.page();
    }

    @Override
    public void update() {
        this.manager.update();
    }

    @Override
    public void page(int target) {
        this.manager.page(target);
        // TODO - Apply page
    }

    @Override
    public Set<PageUpdater> updaters() {
        return this.updaters;
    }

    @Override
    public TriState style() {
        return this.style;
    }

    public static class ImpactorPaginationBuilder extends ImpactorBaseViewBuilder<PaginationBuilder> implements PaginationBuilder {

        private List<Icon> contents;
        private PlatformPlayer viewer;
        private ChestLayout layout;
        private Vector2i zone;
        private Vector2i offset = Vector2i.ZERO;
        private final Set<PageUpdater> updaters = Sets.newHashSet();
        private TriState style = TriState.NOT_SET;

        private ContextRuleset ruleset;

        @Override
        public PaginationBuilder viewer(PlatformPlayer viewer) {
            this.viewer = viewer;
            return this;
        }

        @Override
        public PaginationBuilder layout(ChestLayout layout) {
            this.layout = layout;
            return this;
        }

        @Override
        public PaginationBuilder contents(Collection<Icon> icons) {
            this.contents = ImmutableList.copyOf(icons);
            return this;
        }

        @Override
        public PaginationBuilder zone(Vector2i dimensions, @Nullable Vector2i offset) {
            this.zone = dimensions;
            this.offset = Optional.ofNullable(offset).orElse(Vector2i.ZERO);
            return this;
        }

        @Override
        public PaginationBuilder updater(PageUpdater updater) {
            this.updaters.add(updater);
            return this;
        }

        @Override
        public PaginationBuilder style(TriState state) {
            this.style = state;
            return this;
        }

        @Override
        public PaginationBuilder ruleset(ContextRuleset ruleset) {
            this.ruleset = ruleset;
            return this;
        }

        @Override
        public Pagination build() {
            Preconditions.checkNotNull(this.namespace, "Provider was not specified");
            Preconditions.checkNotNull(this.viewer, "Viewer was not specified");

            if(this.contents == null) {
                this.contents = Collections.emptyList();
            }
            return new ImpactorPagination(this);
        }
    }
}