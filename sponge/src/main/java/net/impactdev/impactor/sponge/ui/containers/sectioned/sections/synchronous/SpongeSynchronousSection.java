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

package net.impactdev.impactor.sponge.ui.containers.sectioned.sections.synchronous;

import com.google.common.collect.Maps;
import net.impactdev.impactor.api.ui.containers.icons.Icon;
import net.impactdev.impactor.api.ui.containers.pagination.sectioned.sections.SectionedPage;
import net.impactdev.impactor.api.ui.containers.pagination.updaters.PageUpdater;
import net.impactdev.impactor.common.ui.containers.pagination.builders.PageConstructionDetails;
import net.impactdev.impactor.common.ui.containers.pagination.sectioned.builders.ImpactorSectionBuilder;
import net.impactdev.impactor.common.ui.containers.pagination.sectioned.sections.AbstractSynchronousSection;
import net.impactdev.impactor.sponge.ui.containers.sectioned.sections.SpongeSectionedPage;
import net.kyori.adventure.util.TriState;
import org.spongepowered.math.vector.Vector2i;

import java.util.List;
import java.util.Map;

public abstract class SpongeSynchronousSection extends AbstractSynchronousSection {

    public SpongeSynchronousSection(ImpactorSectionBuilder<?> builder, List<? extends Icon<?>> icons) {
        super(builder);
        this.pages = this.draft(icons);
    }

//    @Override
//    protected SectionedPage constructPage(PageConstructionDetails<E> ) {
//        SpongeSectionedPage page = new SpongeSectionedPage(working);
//        page.draw(this, updaters, style, index, size);
//        return page;
//    }

    @Override
    protected <E extends Icon<?>> SectionedPage constructPage(PageConstructionDetails<E> details) {
        Vector2i indices = details.getIndexes();
        List<E> icons = details.getIcons().subList(indices.x(), Math.min(indices.y(), details.getIcons().size()));

        Map<Integer, Icon<?>> parameters = Maps.newHashMap();
        for(int i = 0; i < icons.size(); i++) {
            int slot = this.calculateTargetSlot(i, details.getZone(), details.getOffsets());
            parameters.put(slot, icons.get(i));
        }

        SpongeSectionedPage page = new SpongeSectionedPage(parameters);
        page.draw(this, details.getUpdaters(), details.getStyle(), details.getPage(), details.getTotal());
        return page;
    }

    @Override
    public void handleClose() {}
}
