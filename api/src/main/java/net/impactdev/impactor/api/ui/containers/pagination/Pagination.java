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

package net.impactdev.impactor.api.ui.containers.pagination;

import io.leangen.geantyref.TypeToken;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.ui.containers.icons.Icon;
import net.impactdev.impactor.api.ui.containers.layouts.Layout;
import net.impactdev.impactor.api.ui.containers.detail.RefreshDetail;
import net.impactdev.impactor.api.ui.containers.pagination.builders.PaginationBuilder;
import net.impactdev.impactor.api.ui.containers.pagination.components.Page;
import net.impactdev.impactor.api.ui.containers.pagination.updaters.PageUpdater;
import net.impactdev.impactor.api.utilities.lists.CircularLinkedList;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.math.vector.Vector2i;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

/**
 * Represents a UI which features a set of pages of contents loaded dynamically based on the viewer's
 * perspective.
 *
 * By nature of a pagination, each pagination should be considered a unique instance of itself. In other
 * words, it can only be bound to one player at a time.
 */
public interface Pagination {

    /**
     * Represents the provider of this pagination through a {@link Key}. This Key is meant to help
     * identify the provider of a pagination in the event an error occurs during processing
     * of the view.
     *
     * @return A {@link Key} representing the provider of the pagination
     */
    Key provider();

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
     * Specifies the title to this pagination.
     *
     * @return The title of the pagination
     */
    Component title();

    /**
     * Specifies the layout used to create this view.
     *
     * @return The layout of the view
     */
    Layout layout();

    /**
     * Represents the list of pages that this pagination is composed of.
     *
     * @return The circularly linked list of pages
     */
    CircularLinkedList<Page<?>> pages();

    /**
     * Indicates the size of the overall grid that this pagination can draw contents within.
     * Without offsets, this grid is based around (0, 0), or slot index 0.
     *
     * @return The size of the drawable pagination zone
     */
    Vector2i zone();

    /**
     * Indicates an offset grid position where the pagination will base the drawable pagination
     * zone around. For instance, an offset of (1, 1) will position the content zone starting
     * at index 10.
     *
     * @return The offset position of the drawable pagination zone
     */
    Vector2i offsets();

    /**
     * Indicates the current page of the pagination view.
     *
     * @return The current page of the pagination
     */
    int page();

    /**
     * Sets the page of this section to the target page.
     *
     * @param target The target page to view for this section
     */
    void page(int target);

    /**
     * Represents the list of updaters that are attached to this pagination. These control
     * how the page is capable of indexing into the next page.
     *
     * @return A list of updaters for the pagination
     */
    List<PageUpdater> updaters();

    /**
     * Indicates the style for the pagination updaters. See {@link PaginationBuilder#style(TriState)}
     * for further details on what each state represents.
     *
     * @return The state indicating how pagination updaters process pagination updates
     */
    TriState style();

    /**
     * Attempts to place the icon in the following slot location. If the intended slot exists inside
     * the pagination zone, the action will be rejected, and a return value of <code>false</code> will be
     * given to indicate such.
     *
     * @param icon The icon to place at the target slot position
     * @param slot The slot inside the view that should be modified
     * @return <code>true</code> if the action was accepted, <code>false</code> if rejected
     */
    boolean set(@Nullable Icon icon, int slot);

    /**
     * Refreshes the pagination based on the given refresh type.
     *
     * @param type The method of refreshing the inventory
     */
    void refresh(RefreshDetail type);

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

    static PaginationBuilder builder() {
        return Impactor.instance().builders().provide(PaginationBuilder.class);
    }

    interface Generic<T> extends Pagination {

        TypeToken<T> typing();

        void filter(Predicate<T> predicate);

        void sort(Comparator<T> comparator);

    }

}
