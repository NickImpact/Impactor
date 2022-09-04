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

package net.impactdev.impactor.ui.containers.views.pagination.views.sectioned.builders;

import com.google.common.collect.Sets;
import net.impactdev.impactor.api.ui.containers.Icon;
import net.impactdev.impactor.api.ui.containers.views.pagination.rules.ContextRuleset;
import net.impactdev.impactor.api.ui.containers.views.pagination.sectioned.builders.SectionBuilder;
import net.impactdev.impactor.api.ui.containers.views.pagination.sectioned.builders.SectionedPaginationBuilder;
import net.impactdev.impactor.api.ui.containers.views.pagination.updaters.PageUpdater;
import net.impactdev.impactor.ui.containers.views.pagination.views.sectioned.ImpactorSection;
import net.kyori.adventure.util.TriState;
import org.spongepowered.math.vector.Vector2i;

import java.util.List;
import java.util.Set;

public class ImpactorSectionBuilder implements SectionBuilder {

    private final ImpactorSectionedPaginationBuilder parent;

    public List<Icon> contents;
    public Vector2i dimensions;
    public Vector2i offsets;
    public ContextRuleset ruleset;
    public Set<PageUpdater> updaters = Sets.newHashSet();
    public TriState style;

    ImpactorSectionBuilder(final ImpactorSectionedPaginationBuilder parent) {
        this.parent = parent;
    }

    @Override
    public SectionBuilder contents(List<Icon> icons) {
        this.contents = icons;
        return this;
    }

    @Override
    public SectionBuilder ruleset(ContextRuleset ruleset) {
        this.ruleset = ruleset;
        return this;
    }

    @Override
    public SectionBuilder dimensions(Vector2i dimensions) {
        this.dimensions = dimensions;
        return this;
    }

    @Override
    public SectionBuilder offset(Vector2i offset) {
        this.offsets = offset;
        return this;
    }

    @Override
    public SectionBuilder updater(PageUpdater updater) {
        this.updaters.add(updater);
        return this;
    }

    @Override
    public SectionBuilder style(TriState state) {
        this.style = state;
        return this;
    }

    @Override
    public SectionedPaginationBuilder complete() {
        return this.parent.with(new ImpactorSection(this));
    }

}
