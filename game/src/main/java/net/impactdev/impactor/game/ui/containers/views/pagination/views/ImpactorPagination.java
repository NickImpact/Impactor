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

package net.impactdev.impactor.game.ui.containers.views.pagination.views;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.ui.containers.Icon;
import net.impactdev.impactor.api.ui.containers.layouts.ChestLayout;
import net.impactdev.impactor.api.ui.containers.views.pagination.Pagination;
import net.impactdev.impactor.api.ui.containers.views.pagination.builders.PaginationBuilder;
import net.impactdev.impactor.api.ui.containers.views.pagination.Page;
import net.impactdev.impactor.api.ui.containers.views.pagination.rules.ContextRuleset;
import net.impactdev.impactor.api.ui.containers.views.pagination.updaters.PageUpdater;
import net.impactdev.impactor.api.utilities.lists.CircularLinkedList;
import net.impactdev.impactor.game.ui.containers.views.builders.ImpactorBaseViewBuilder;
import net.impactdev.impactor.game.ui.containers.views.layers.ImpactorView;
import net.impactdev.impactor.game.ui.containers.views.pagination.ImpactorContextRuleset;
import net.impactdev.impactor.game.ui.containers.views.pagination.PaginatedView;
import net.impactdev.impactor.game.ui.containers.views.pagination.layers.PageManager;
import net.kyori.adventure.util.TriState;
import org.checkerframework.common.value.qual.IntRange;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.math.vector.Vector2i;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public abstract class ImpactorPagination extends ImpactorView implements Pagination, PaginatedView {

    protected final PlatformPlayer viewer;
    private final ChestLayout layout;
    private final Vector2i zone;
    private final Vector2i offsets;
    private final Set<PageUpdater> updaters;
    private final TriState style;

    private final ContextRuleset ruleset;

    private final List<Icon> contents;
    private final PageManager manager;

    protected ImpactorPagination(ImpactorPaginationBuilder builder) {
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
        Page current = this.pages().current();
        current.icons().forEach((slot, icon) -> this.set(icon, slot));
    }

    @Override
    public Set<PageUpdater> updaters() {
        return this.updaters;
    }

    @Override
    public TriState style() {
        return this.style;
    }

    public static abstract class ImpactorPaginationBuilder extends ImpactorBaseViewBuilder<PaginationBuilder> implements PaginationBuilder {

        protected List<Icon> contents = Collections.emptyList();
        protected PlatformPlayer viewer;
        protected ChestLayout layout;
        protected Vector2i zone;
        protected Vector2i offset = Vector2i.ZERO;
        protected final Set<PageUpdater> updaters = Sets.newHashSet();
        protected TriState style = TriState.NOT_SET;

        protected ContextRuleset ruleset;

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
    }
}
