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

package net.impactdev.impactor.minecraft.ui.containers.views.pagination.views.sectioned.builders;

import com.google.common.collect.Lists;
import net.impactdev.impactor.api.platform.sources.PlatformPlayer;
import net.impactdev.impactor.api.ui.containers.layouts.ChestLayout;
import net.impactdev.impactor.api.ui.containers.views.pagination.sectioned.builders.SectionBuilder;
import net.impactdev.impactor.api.ui.containers.views.pagination.sectioned.builders.SectionedPaginationBuilder;
import net.impactdev.impactor.api.ui.containers.views.pagination.sectioned.sections.Section;
import net.impactdev.impactor.minecraft.ui.containers.views.builders.ImpactorBaseViewBuilder;

import java.util.List;

public abstract class ImpactorSectionedPaginationBuilder extends ImpactorBaseViewBuilder<SectionedPaginationBuilder> implements SectionedPaginationBuilder {

    public PlatformPlayer viewer;
    public ChestLayout background;
    public final List<Section> sections = Lists.newArrayList();

    @Override
    public SectionedPaginationBuilder viewer(PlatformPlayer viewer) {
        this.viewer = viewer;
        return this;
    }

    @Override
    public SectionedPaginationBuilder layout(ChestLayout layout) {
        this.background = layout;
        return this;
    }

    @Override
    public SectionBuilder section() {
        return new ImpactorSectionBuilder(this);
    }

    SectionedPaginationBuilder with(Section section) {
        this.sections.add(section);
        return this;
    }

}
