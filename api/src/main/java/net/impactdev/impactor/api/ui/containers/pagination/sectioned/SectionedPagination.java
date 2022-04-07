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

package net.impactdev.impactor.api.ui.containers.pagination.sectioned;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.ui.containers.components.UIComponent;
import net.impactdev.impactor.api.ui.containers.icons.DisplayProvider;
import net.impactdev.impactor.api.ui.containers.icons.Icon;
import net.impactdev.impactor.api.ui.containers.layouts.Layout;
import net.impactdev.impactor.api.ui.containers.pagination.Pagination;
import net.impactdev.impactor.api.ui.containers.pagination.updaters.PageUpdater;
import net.impactdev.impactor.api.ui.containers.pagination.updaters.PageUpdaterType;
import net.impactdev.impactor.api.utilities.Builder;
import net.impactdev.impactor.api.utilities.lists.CircularLinkedList;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.util.TriState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.math.vector.Vector2i;

import java.util.List;
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
    boolean set(@Nullable Icon<?> icon, int slot);

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
        return Impactor.getInstance().getRegistry().createBuilder(SectionedPaginationBuilder.class);
    }

    /**
     * A section is the internal pagination that a {@link SectionedPagination} maintains. Each section is expected
     * to be controlled via its own configured page updaters.
     */
    interface Section {

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

    }

    interface SectionedPaginationBuilder extends UIComponent<SectionedPaginationBuilder>, Builder<SectionedPagination, SectionedPaginationBuilder> {

        @Required
        SectionedPaginationBuilder provider(Key provider);

        @Required
        SectionedPaginationBuilder viewer(PlatformPlayer viewer);

        SectionBuilder section();

    }

    interface SectionBuilder {

        /**
         * Sets the contents of the pagination to the following icons. If the list of icons is more
         * than can be carried in a singular page, the following icons will be associated with further
         * pages until no more pages become necessary.
         *
         * @param contents The icons to associate with the pagination
         * @return The updated builder
         */
        SectionBuilder contents(List<Icon<?>> contents);

        /**
         * Specifies the dimensions of this section. This is simply the length and width of the section, but not
         * necessarily the position. To control the position of the section, make use of {@link #offset(Vector2i)}
         * to reposition the section. All paginations use the grid location of (0, 0) as their base grid point.
         * For a section that is 7 columns in length, 4 rows wide, but you want to be in the dead middle of the view
         * which is 9x6, you can use an offset dimension of (1, 1) to position the section into that frame.
         *
         * @param columns The number of columns in the section
         * @param rows The number of rows in the section
         * @see #offset(Vector2i)
         * @return The updated builder
         */
        default SectionBuilder dimensions(int columns, int rows) {
            return this.dimensions(Vector2i.from(columns, rows));
        }

        /**
         * Specifies the dimensions of this section. This is simply the length and width of the section, but not
         * necessarily the position. To control the position of the section, make use of {@link #offset(Vector2i)} 
         * to reposition the section. All paginations use the grid location of (0, 0) as their base grid point.
         * For a section that is 7 columns in length, 4 rows wide, but you want to be in the dead middle of the view
         * which is 9x6, you can use an offset dimension of (1, 1) to position the section into that frame.
         * 
         * @param dimensions The dimensions of the section
         * @see #offset(Vector2i)
         * @return The updated builder
         */
        SectionBuilder dimensions(Vector2i dimensions);

        /**
         * Specifies the relocation of the section. This controls where the initial dimension
         * of the viewable section exists in the view. This offset is based on the (0, 0) grid
         * location within the view of the inventory, or otherwise, slot index 0.
         *
         * @param offset The offset from (0, 0)
         * @return The updated builder
         */
        SectionBuilder offset(Vector2i offset);

        /**
         * Supplies an icon that controls interactions among pages within the pagination. These buttons act
         * as the second layer of the UI, and can only be overridden by the content zone.
         *
         * <p>The only button that will have no actionable aspects is via {@link PageUpdaterType#CURRENT}, which
         * is simply meant to display the current page number.
         *
         * <p>To allow for dynamic parsing of components, these icons are compatible with {@link MiniMessage} parsing
         * mechanics, so you can control the actual language output of each option. Additionally, a placeholder with
         * the following tag &lt;target-page&gt; will be available for all parsable components so your text can be
         * dynamically styled and complete with parsed placeholders.
         *
         * @param updater The updater that will be used for the inventory interaction
         * @return The updated builder
         */
        SectionBuilder updater(PageUpdater updater);

        /**
         * Indicates whether any {@link PageUpdater PageUpdaters} with typings of {@link PageUpdaterType#PREVIOUS}
         * or {@link PageUpdaterType#NEXT} are allowed to recycle to the appropriate boundary of pages should
         * the current page already be on the boundary. For instance, on page 1, previous could reroute to the
         * actual max page of the pagination rather than do nothing. Additionally, this will allow for possibly
         * even hiding the icons if they would serve no use. So on page 1, with a setting of {@link TriState#FALSE},
         * the updater for {@link PageUpdaterType#PREVIOUS} would just be hidden. If not set, this will default
         * to {@link TriState#NOT_SET}.
         *
         * <br><br>Below you can see the outcomes of each state:
         * <pre>
         +----------------------------------------------+
         | State                | Result               	|
         | ---------------------|----------------------	|
         | {@link TriState#TRUE}    	| Shown and Cycle      	|
         | {@link TriState#NOT_SET} 	| Shown but do nothing 	|
         | {@link TriState#FALSE}   	| Hidden               	|
         +----------------------------------------------+
         * </pre>
         *
         * @param state The state to apply to updaters and how they appear and act within the Pagination
         * @return The updated builder
         */
        SectionBuilder style(TriState state);

        /**
         * Marks the section configuration as complete and sound, and returns to the parent builder
         * so that further changes can be applied to that configuration.
         *
         * @return The parent builder that created this builder
         */
        SectionedPaginationBuilder complete();

    }

}
