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

import io.leangen.geantyref.TypeToken;
import net.impactdev.impactor.api.ui.containers.Icon;
import net.impactdev.impactor.api.ui.containers.views.pagination.sectioned.SectionedPagination;
import net.impactdev.impactor.api.utilities.lists.CircularLinkedList;
import org.spongepowered.math.vector.Vector2i;

import java.util.Comparator;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * A section is the internal pagination that a {@link SectionedPagination} maintains. Each section is expected
 * to be controlled via its own configured page updaters.
 */
public interface Section {

    /**
     * Represents the minimum slot grid location that this section begins at.
     *
     * @return A {@link Vector2i} representing the minimum grid location of this section.
     */
    Vector2i minimum();

    /**
     * Represents the maximum slot grid location that this section begins at.
     *
     * @return A {@link Vector2i} representing the maximum grid location of this section.
     */
    Vector2i maximum();

    default boolean within(int slot) {
        return this.within(Vector2i.from(slot % 9, slot / 9));
    }

    default boolean within(Vector2i coordinates) {
        Vector2i x = coordinates.sub(this.minimum());
        Vector2i y = this.maximum().sub(coordinates);

        return x.x() >= 0 && x.y() >= 0 && y.x() >= 0 && y.y() >= 0;
    }

    /**
     * Represents the list of pages that this section is composed of.
     *
     * @return The circularly linked list of pages
     */
    CircularLinkedList<SectionedPage> pages();

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

    /**
     * Calculates the location an icon should be placed based on the target index,
     * alongside the content zone with grid and its offsets. Note that the target is not
     * the slot, but rather the index from 0.
     *
     * @param target The index of the content being placed into the pagination
     * @param zone The grid size for the pagination zone
     * @param offsets The offsets of the pagination zone
     * @return The calculated slot position in the pagination zone
     */
    default int calculateTargetSlot(int target, Vector2i zone, Vector2i offsets) {
        int x = target % zone.x();
        int y = target / zone.x();

        Vector2i result = Vector2i.from(x, y).add(offsets);
        return result.x() + (9 * result.y());
    }

    void refresh(BiConsumer<Integer, Icon> consumer);

    interface Generic<T> extends Section {

        default TypeToken<T> type() {
            return new TypeToken<T>() {};
        }

        void filter(Predicate<T> filter);

        void sorter(Comparator<T> comparator);

    }

}
