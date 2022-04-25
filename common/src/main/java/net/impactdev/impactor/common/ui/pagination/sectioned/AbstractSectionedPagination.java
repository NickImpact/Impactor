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

package net.impactdev.impactor.common.ui.pagination.sectioned;

import com.google.common.collect.ImmutableSet;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.ui.containers.icons.Icon;
import net.impactdev.impactor.api.ui.containers.layouts.Layout;
import net.impactdev.impactor.api.ui.containers.pagination.sectioned.sections.Section;
import net.impactdev.impactor.api.ui.containers.pagination.sectioned.SectionedPagination;
import net.impactdev.impactor.common.ui.pagination.sectioned.builders.ImpactorSectionedPaginationBuilder;
import net.impactdev.impactor.common.ui.pagination.sectioned.sections.AssignableSection;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.math.vector.Vector2i;

import java.util.Optional;
import java.util.Set;

public abstract class AbstractSectionedPagination implements SectionedPagination {

    private final Key provider;
    private final Layout layout;

    protected final PlatformPlayer viewer;
    protected final Set<Section> sections;

    protected AbstractSectionedPagination(ImpactorSectionedPaginationBuilder builder) {
        this.provider = builder.provider;
        this.viewer = builder.viewer;
        this.layout = builder.layout;
        this.sections = builder.sections;
        for(Section section : sections) {
            ((AssignableSection) section).assignTo(this);
        }
    }

    @Override
    public Key provider() {
        return this.provider;
    }

    @Override
    public Layout layout() {
        return this.layout;
    }

    protected Set<Section> sections() {
        return ImmutableSet.copyOf(this.sections);
    }

    @Override
    public Optional<Section> at(int slot) {
        return this.sections.stream()
                .filter(section -> {
                    Vector2i location = new Vector2i(slot % 9, slot / 9);
                    return this.greaterOrEqual(location, section.minimum()) &&
                            this.lessOrEqual(location, section.maximum());
                })
                .findAny();
    }

    public abstract void setUnsafe(@Nullable Icon<?> icon, int slot);

    private boolean greaterOrEqual(Vector2i query, Vector2i compare) {
        return query.x() >= compare.x() && query.y() >= compare.y();
    }

    private boolean lessOrEqual(Vector2i query, Vector2i compare) {
        return query.x() <= compare.x() && query.y() <= compare.y();
    }

}
