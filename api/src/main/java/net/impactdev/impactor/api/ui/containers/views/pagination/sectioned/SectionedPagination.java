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
import net.impactdev.impactor.api.ui.containers.Icon;
import net.impactdev.impactor.api.ui.containers.Layout;
import net.impactdev.impactor.api.ui.containers.views.pagination.Pagination;
import net.impactdev.impactor.api.ui.containers.views.pagination.sectioned.builders.SectionedPaginationBuilder;
import net.impactdev.impactor.api.ui.containers.views.pagination.sectioned.sections.Section;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.math.vector.Vector2i;

import java.util.Optional;

/**
 * A sectioned pagination is a pagination which features more than one pagination. Unlike a base
 * {@link Pagination}, this type of pagination is composed of sections which act as their own internal
 * pagination. Each section allows for a different combination of items to be cycled amongst each other,
 * and each section need not agree with the other.
 */
public interface SectionedPagination {

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
     * Specifies the layout used to create this view.
     *
     * @return The layout of the view
     */
    Layout layout();

    /**
     * Attempts to place the icon in the following slot location. If the intended slot exists inside
     * a section, the action will be rejected, and a return value of <code>false</code> will be given
     * to indicate such.
     *
     * @param icon The icon to place at the target slot position
     * @param slot The slot inside the view that should be modified
     * @return <code>true</code> if the action was accepted, <code>false</code> if rejected
     */
    boolean set(@Nullable Icon icon, int slot);

    /**
     * If it exists, provides the section at the target slot. Otherwise, this will result in an empty
     * optional.
     *
     * @param slot The slot position in the view that might exist within a section
     * @return A section optionally wrapped if available, or empty to indicate no existing section
     * at the target location
     */
    Optional<Section> at(int slot);

    /**
     * If it exists, provides the section at the target grid point. Otherwise, this will result in an empty
     * optional.
     *
     * @param x The X coordinate for the grid point
     * @param y The Y coordinate for the grid point
     * @return A section optionally wrapped if available, or empty to indicate no existing section
     * at the target location
     */
    default Optional<Section> at(int x, int y) {
        return this.at(x + (y * 9));
    }

    /**
     * If it exists, provides the section at the target grid point. Otherwise, this will result in an empty
     * optional.
     *
     * @param coordinates A vector representing the grid points for a possible section
     * @return A section optionally wrapped if available, or empty to indicate no existing section
     * at the target location
     */
    default Optional<Section> at(Vector2i coordinates) {
        return this.at(coordinates.x(), coordinates.y());
    }

    static SectionedPaginationBuilder builder() {
        return Impactor.instance().builders().provide(SectionedPaginationBuilder.class);
    }

}
