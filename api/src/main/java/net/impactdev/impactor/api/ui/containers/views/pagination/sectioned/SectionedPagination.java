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

package net.impactdev.impactor.api.ui.containers.views.pagination.sectioned;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.ui.containers.View;
import net.impactdev.impactor.api.ui.containers.layouts.ChestLayout;
import net.impactdev.impactor.api.ui.containers.views.pagination.Pagination;
import net.impactdev.impactor.api.ui.containers.views.pagination.sectioned.builders.SectionedPaginationBuilder;
import net.impactdev.impactor.api.ui.containers.views.pagination.sectioned.sections.Section;
import org.checkerframework.common.value.qual.IntRange;

import java.util.List;

/**
 * A sectioned pagination is a pagination which features more than one pagination. Unlike a base
 * {@link Pagination}, this type of pagination is composed of sections which act as their own internal
 * pagination. Each section allows for a different combination of items to be cycled amongst each other,
 * and each section need not agree with the other.
 */
public interface SectionedPagination extends View {

    /**
     * Opens the view for the configured viewer, closing any currently opened view the player
     * might be viewing.
     */
    void open();

    /**
     * Closes the view for the configured viewer, only if the view they have open, if one at all,
     * is this view. Otherwise, this call will result in a no-op.
     */
    void close();

    /**
     * Specifies the layout used to create this view.
     *
     * @return The layout of the view
     */
    @Override
    ChestLayout layout();

    /**
     * Provides the section existing at the specified index, based on the order of which the sections
     * were constructed with this pagination.
     *
     * @param index The index position of a section.
     * @return A section optionally wrapped if available, or empty to indicate no existing section
     * at the target location
     * @throws IndexOutOfBoundsException If the index is outside the section count
     */
    @IntRange(from = 0)
    Section at(int index);

    List<Section> sections();

    static SectionedPaginationBuilder builder() {
        return Impactor.instance().builders().provide(SectionedPaginationBuilder.class);
    }

}
