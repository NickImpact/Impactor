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

package net.impactdev.impactor.minecraft.ui.containers.views.chests.pagination.views.sectioned;

import net.impactdev.impactor.api.ui.containers.Icon;
import net.impactdev.impactor.api.ui.containers.views.pagination.Page;
import net.impactdev.impactor.api.ui.containers.views.pagination.rules.ContextRuleset;
import net.impactdev.impactor.api.ui.containers.views.pagination.sectioned.sections.Section;
import net.impactdev.impactor.api.ui.containers.views.pagination.updaters.PageUpdater;
import net.impactdev.impactor.api.utility.collections.lists.CircularLinkedList;
import net.impactdev.impactor.minecraft.ui.containers.views.chests.pagination.PaginatedView;
import net.impactdev.impactor.minecraft.ui.containers.views.chests.pagination.layers.PageManager;
import net.impactdev.impactor.minecraft.ui.containers.views.chests.pagination.views.sectioned.builders.ImpactorSectionBuilder;
import org.spongepowered.math.vector.Vector2i;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ImpactorSection implements Section, PaginatedView {

    private ImpactorSectionedPagination parent;

    private final Vector2i dimensions;
    private final Vector2i offsets;
    private final ContextRuleset ruleset;
    private final Set<PageUpdater> updaters;

    private final List<Icon> contents;
    private final PageManager manager;

    public ImpactorSection(ImpactorSectionBuilder builder) {
        this.contents = builder.contents;
        this.dimensions = builder.dimensions;
        this.offsets = builder.offsets;
        this.ruleset = Optional.ofNullable(builder.ruleset).orElse(ContextRuleset.create());
        this.updaters = builder.updaters;

        this.manager = new PageManager(this);
    }

    ImpactorSection with(ImpactorSectionedPagination parent) {
        this.parent = parent;
        return this;
    }

    @Override
    public List<Icon> contents() {
        return this.contents;
    }

    @Override
    public Vector2i zone() {
        return this.dimensions;
    }

    @Override
    public Vector2i offsets() {
        return this.offsets;
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
    public int page() {
        return this.manager.page();
    }

    @Override
    public Set<PageUpdater> updaters() {
        return this.updaters;
    }

    @Override
    public void update() {
        this.manager.update();
    }

    @Override
    public void page(int target) {
        this.manager.page(target);
        Page current = this.pages().current();
        current.icons().forEach((slot, icon) -> this.parent.set(icon, slot));
    }

}
