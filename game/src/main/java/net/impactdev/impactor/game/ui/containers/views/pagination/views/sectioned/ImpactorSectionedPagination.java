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

package net.impactdev.impactor.game.ui.containers.views.pagination.views.sectioned;

import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.ui.containers.layouts.ChestLayout;
import net.impactdev.impactor.api.ui.containers.views.pagination.sectioned.SectionedPagination;
import net.impactdev.impactor.api.ui.containers.views.pagination.sectioned.sections.Section;
import net.impactdev.impactor.game.ui.containers.views.layers.ImpactorView;
import net.impactdev.impactor.game.ui.containers.views.pagination.views.sectioned.builders.ImpactorSectionedPaginationBuilder;
import org.checkerframework.common.value.qual.IntRange;
import org.spongepowered.math.vector.Vector2i;

import java.util.List;

public abstract class ImpactorSectionedPagination extends ImpactorView implements SectionedPagination {

    protected final PlatformPlayer viewer;
    private final ChestLayout background;
    private final List<Section> sections;

    protected ImpactorSectionedPagination(ImpactorSectionedPaginationBuilder builder) {
        super(builder.namespace, builder.title, builder.readonly, builder.click, builder.close);
        this.viewer = builder.viewer;
        this.background = builder.background;
        this.sections = builder.sections;
        this.sections.forEach(section -> ((ImpactorSection) section).with(this));
    }

    @Override
    public ChestLayout layout() {
        return this.background;
    }

    @Override
    public @IntRange(from = 0) Section at(int index) {
        return this.sections.get(index);
    }

    @Override
    public List<Section> sections() {
        return this.sections;
    }

    @Override
    public @IntRange(from = 1, to = 6) int rows() {
        return this.background.dimensions().y();
    }

    @Override
    public void refresh(Vector2i dimensions, Vector2i offsets) {

    }
}
