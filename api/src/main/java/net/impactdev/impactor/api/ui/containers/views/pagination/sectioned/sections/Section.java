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

package net.impactdev.impactor.api.ui.containers.views.pagination.sectioned.sections;

import net.impactdev.impactor.api.ui.containers.views.pagination.Page;
import net.impactdev.impactor.api.ui.containers.views.pagination.rules.ContextRuleset;
import net.impactdev.impactor.api.ui.containers.views.pagination.sectioned.SectionedPagination;
import net.impactdev.impactor.api.utilities.lists.CircularLinkedList;
import org.spongepowered.math.vector.Vector2i;

/**
 * A section is the internal pagination that a {@link SectionedPagination} maintains. Each section is expected
 * to be controlled via its own configured page updaters.
 */
public interface Section {

    ContextRuleset ruleset();

    Vector2i zone();

    Vector2i offsets();

    default boolean within(int slot) {
        return this.within(Vector2i.from(slot % 9, slot / 9));
    }

    default boolean within(Vector2i coordinates) {
        Vector2i min = this.offsets();
        Vector2i max = this.offsets().add(this.zone());

        Vector2i x = coordinates.sub(min);
        Vector2i y = max.sub(coordinates);

        return x.x() >= 0 && x.y() >= 0 && y.x() >= 0 && y.y() >= 0;
    }

    /**
     * Represents the list of pages that this section is composed of.
     *
     * @return The circularly linked list of pages
     */
    CircularLinkedList<Page> pages();

    /**
     * Specifies the current page of the section.
     *
     * @return The current client-facing page of the section
     */
    int page();

    /**
     * Sets the page of this section to the target page.
     *
     * @param target The target page to view for this section
     */
    void page(int target);

}
