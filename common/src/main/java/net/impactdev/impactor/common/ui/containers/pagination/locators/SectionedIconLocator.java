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

package net.impactdev.impactor.common.ui.containers.pagination.locators;

import com.google.common.collect.Maps;
import net.impactdev.impactor.api.ui.containers.icons.Icon;
import net.impactdev.impactor.api.ui.containers.layouts.Layout;
import net.impactdev.impactor.api.ui.containers.pagination.sectioned.sections.Section;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class SectionedIconLocator implements IconLocator {

    private final Layout layout;
    private final Set<Section> sections;
    private final Map<Integer, Icon<?>> overrides = Maps.newHashMap();

    public SectionedIconLocator(Layout layout, Set<Section> sections) {
        this.layout = layout;
        this.sections = sections;
    }

    @Override
    public Map<Integer, Icon<?>> view() {
        Map<Integer, Icon<?>> tracked = Maps.newHashMap(layout.elements());
        sections.forEach(section -> {
            Map<Integer, Icon<?>> translated = section.pages().nextOrThrow().icons();
            tracked.putAll(translated);
        });
        return tracked;
    }

    @Override
    public void override(int index, Icon<?> icon) {
        this.overrides.put(index, icon);
    }

    @Override
    public Optional<Icon<?>> locate(int index) {
        if(this.overrides.containsKey(index)) {
            return Optional.of(this.overrides.get(index));
        }

        if(this.layout.elements().containsKey(index)) {
            return this.layout.icon(index);
        }

        for(Section section : this.sections) {
            if(section.within(index)) {
                return section.pages().getCurrent().map(x -> x.drawn().get(index));
            }
        }

        return Optional.empty();
    }
}
