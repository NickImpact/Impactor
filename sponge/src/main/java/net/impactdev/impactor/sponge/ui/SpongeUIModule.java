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

package net.impactdev.impactor.sponge.ui;

import net.impactdev.impactor.api.registry.Registry;
import net.impactdev.impactor.api.ui.UIModule;
import net.impactdev.impactor.api.ui.containers.ImpactorUI;
import net.impactdev.impactor.api.ui.containers.icons.Icon;
import net.impactdev.impactor.api.ui.containers.layouts.Layout;
import net.impactdev.impactor.api.ui.containers.pagination.builders.PaginationBuilder;
import net.impactdev.impactor.api.ui.containers.pagination.sectioned.SectionedPagination;
import net.impactdev.impactor.api.ui.containers.pagination.sectioned.builders.SectionedPaginationBuilder;
import net.impactdev.impactor.api.ui.signs.SignQuery;
import net.impactdev.impactor.common.api.ModuleImplementation;
import net.impactdev.impactor.common.ui.LayoutImpl;
import net.impactdev.impactor.common.ui.pagination.builders.ImpactorPaginationBuilder;
import net.impactdev.impactor.sponge.ui.containers.SpongeUI;
import net.impactdev.impactor.sponge.ui.containers.icons.SpongeIcon;
import net.impactdev.impactor.sponge.ui.containers.pagination.builders.SpongeImpactorPaginationBuilder;
import net.impactdev.impactor.sponge.ui.containers.sectioned.SpongeSectionedPagination;
import net.impactdev.impactor.sponge.ui.containers.sectioned.builders.SpongeSectionedPaginationBuilder;
import net.impactdev.impactor.sponge.ui.signs.SpongeSignQuery;

@ModuleImplementation
public final class SpongeUIModule implements UIModule {

    @Override
    public void register(Registry registry) {}

    @Override
    public void builders(Registry registry) {
        registry.registerBuilderSupplier(Icon.IconBuilder.class, SpongeIcon.SpongeIconBuilder::new);
        registry.registerBuilderSupplier(Layout.LayoutBuilder.class, LayoutImpl.LayoutImplBuilder::new);
        registry.registerBuilderSupplier(PaginationBuilder.class, SpongeImpactorPaginationBuilder::new);
        registry.registerBuilderSupplier(ImpactorUI.UIBuilder.class, SpongeUI.SpongeUIBuilder::new);
        registry.registerBuilderSupplier(SectionedPaginationBuilder.class, SpongeSectionedPaginationBuilder::new);
        registry.registerBuilderSupplier(SignQuery.SignQueryBuilder.class, SpongeSignQuery.SpongeSignQueryBuilder::new);
    }

}
