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

package net.impactdev.impactor.common.ui.pagination.sectioned.builders;

import com.google.common.collect.Lists;
import net.impactdev.impactor.api.ui.containers.pagination.sectioned.builders.SectionBuilder;
import net.impactdev.impactor.api.ui.containers.pagination.sectioned.builders.SectionedPaginationBuilder;
import net.impactdev.impactor.api.ui.containers.pagination.sectioned.sections.Section;
import net.impactdev.impactor.api.ui.containers.pagination.updaters.PageUpdater;
import net.kyori.adventure.util.TriState;
import org.spongepowered.math.vector.Vector2i;

import java.util.List;

public abstract class ImpactorSectionBuilder<B extends SectionBuilder<B>> implements SectionBuilder<B> {

    private final ImpactorSectionedPaginationBuilder parent;

    public Vector2i minimum = Vector2i.ZERO;
    public Vector2i maximum = Vector2i.ZERO;
    public List<PageUpdater> updaters = Lists.newArrayList();
    public TriState style = TriState.NOT_SET;

    public ImpactorSectionBuilder(ImpactorSectionedPaginationBuilder parent) {
        this.parent = parent;
    }

    @Override
    public B dimensions(Vector2i dimensions) {
        this.maximum = Vector2i.ZERO.add(dimensions);
        return (B) this;
    }

    @Override
    public B offset(Vector2i offset) {
        this.minimum = offset;
        this.maximum = this.maximum.add(offset).sub(Vector2i.ONE);
        return (B) this;
    }

    @Override
    public B updater(PageUpdater updater) {
        this.updaters.add(updater);
        return (B) this;
    }

    @Override
    public B style(TriState state) {
        this.style = state;
        return (B) this;
    }

    @Override
    public SectionedPaginationBuilder complete() {
        return this.parent.with(this.create());
    }

    protected abstract Section create();
}
