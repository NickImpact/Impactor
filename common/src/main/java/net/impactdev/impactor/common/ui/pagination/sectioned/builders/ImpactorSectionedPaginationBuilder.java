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

import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.ui.containers.icons.ClickProcessor;
import net.impactdev.impactor.api.ui.containers.layouts.Layout;
import net.impactdev.impactor.api.ui.containers.pagination.sectioned.builders.SectionedPaginationBuilder;
import net.impactdev.impactor.api.ui.containers.pagination.sectioned.sections.Section;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class ImpactorSectionedPaginationBuilder implements SectionedPaginationBuilder {

    public Key provider;
    public PlatformPlayer viewer;
    public Component title;
    public Layout layout;
    public boolean readonly;
    public ClickProcessor click;
    public CloseProcessor close;
    public Set<Section> sections = new LinkedHashSet<>();

    @Override
    public SectionedPaginationBuilder provider(Key provider) {
        this.provider = provider;
        return this;
    }

    @Override
    public SectionedPaginationBuilder viewer(PlatformPlayer viewer) {
        this.viewer = viewer;
        return this;
    }

    @Override
    public SectionedPaginationBuilder title(Component title) {
        this.title = title;
        return this;
    }

    @Override
    public SectionedPaginationBuilder layout(Layout layout) {
        this.layout = layout;
        return this;
    }

    @Override
    public SectionedPaginationBuilder readonly(boolean state) {
        this.readonly = state;
        return this;
    }

    @Override
    public SectionedPaginationBuilder onClick(ClickProcessor processor) {
        this.click = processor;
        return this;
    }

    @Override
    public SectionedPaginationBuilder onClose(CloseProcessor processor) {
        this.close = processor;
        return this;
    }

    SectionedPaginationBuilder with(Section section) {
        this.sections.add(section);
        return this;
    }

}
