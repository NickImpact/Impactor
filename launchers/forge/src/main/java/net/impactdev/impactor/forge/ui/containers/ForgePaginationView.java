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

package net.impactdev.impactor.forge.ui.containers;

import ca.landonjw.gooeylibs2.api.page.GooeyPage;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import com.google.common.base.Preconditions;
import net.impactdev.impactor.api.ui.containers.Icon;
import net.impactdev.impactor.api.ui.containers.views.pagination.Pagination;
import net.impactdev.impactor.forge.ui.gooey.GooeyIcon;
import net.impactdev.impactor.forge.ui.gooey.GooeyPageOpenCloser;
import net.impactdev.impactor.minecraft.text.AdventureTranslator;
import net.impactdev.impactor.minecraft.ui.containers.views.pagination.views.ImpactorPagination;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Optional;

public final class ForgePaginationView extends ImpactorPagination implements GooeyPageOpenCloser {

    private final ChestTemplate template;
    private final GooeyPage delegate;

    private ForgePaginationView(ImpactorPaginationBuilder builder) {
        super(builder);
        ChestTemplate.Builder template = ChestTemplate.builder(this.rows());
        this.layout().elements().forEach((slot, icon) -> {
            template.set(slot, new GooeyIcon(icon));
        });
        this.pages().current().icons().forEach((slot, icon) -> {
            template.set(slot, new GooeyIcon(icon));
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

    public static final class ForgePaginationViewBuilder extends ImpactorPaginationBuilder {
        @Override
        public Pagination build() {
            Preconditions.checkNotNull(this.namespace, "Provider was not specified");
            Preconditions.checkNotNull(this.viewer, "Viewer was not specified");

            if(this.contents == null) {
                this.contents = Collections.emptyList();
            }
            return new ForgePaginationView(this);
        }
    }
}
