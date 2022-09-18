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

package net.impactdev.impactor.fabric.ui.containers;

import ca.landonjw.gooeylibs2.api.page.GooeyPage;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import net.impactdev.impactor.adventure.AdventureTranslator;
import net.impactdev.impactor.api.ui.containers.Icon;
import net.impactdev.impactor.api.ui.containers.views.pagination.sectioned.SectionedPagination;
import net.impactdev.impactor.fabric.ui.gooey.GooeyIcon;
import net.impactdev.impactor.fabric.ui.gooey.GooeyPageOpenCloser;
import net.impactdev.impactor.ui.containers.views.pagination.views.sectioned.ImpactorSectionedPagination;
import net.impactdev.impactor.ui.containers.views.pagination.views.sectioned.builders.ImpactorSectionedPaginationBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class FabricSectionedPaginationView extends ImpactorSectionedPagination implements GooeyPageOpenCloser {

    private final ChestTemplate template;
    private final GooeyPage delegate;

    public FabricSectionedPaginationView(ImpactorSectionedPaginationBuilder builder) {
        super(builder);

        ChestTemplate.Builder template = ChestTemplate.builder(this.rows());
        this.layout().elements().forEach((slot, icon) -> {
            template.set(slot, new GooeyIcon(icon));
        });
        this.sections().forEach(section -> {
            section.pages().current().icons().forEach((slot, icon) -> {
                template.set(slot, new GooeyIcon(icon));
            });
        });

        this.delegate = GooeyPage.builder()
                .template(this.template = template.build())
                .title(AdventureTranslator.toNative(this.title()))
//                .onClose(action -> view.) // TODO - Add this back to Impactor API, add onClick to Gooey
                .build();
    }

    @Override
    public void set(@Nullable Icon icon, int slot) {
        this.template.set(slot, Optional.ofNullable(icon).map(GooeyIcon::new).orElse(null));
    }

    @Override
    public void open() {
        this.openPage(this.delegate, this.viewer);
    }

    @Override
    public void close() {
        this.closePage(this.viewer);
    }

    public static class FabricSectionedPaginationBuilder extends ImpactorSectionedPaginationBuilder {

        @Override
        public SectionedPagination build() {
            return new FabricSectionedPaginationView(this);
        }

    }
}
