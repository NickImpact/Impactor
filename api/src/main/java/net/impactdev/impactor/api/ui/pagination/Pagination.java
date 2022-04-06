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

package net.impactdev.impactor.api.ui.pagination;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.ui.components.UIComponent;
import net.impactdev.impactor.api.ui.icons.Icon;
import net.impactdev.impactor.api.ui.layouts.Layout;
import net.impactdev.impactor.api.ui.detail.RefreshDetail;
import net.impactdev.impactor.api.ui.pagination.updaters.PageUpdater;
import net.impactdev.impactor.api.ui.pagination.updaters.PageUpdaterType;
import net.impactdev.impactor.api.utilities.Builder;
import net.impactdev.impactor.api.utilities.lists.CircularLinkedList;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.util.TriState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.math.vector.Vector2i;

import java.util.List;

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
    boolean set(@Nullable Icon<?> icon, int slot);

    /**
     * Refreshes the pagination based on the given refresh type.
     *
     * @param type The method of refreshing the inventory
     */
    void refresh(RefreshDetail type);

    /**
     * Represents the list of pages that this pagination is composed of.
     *
     * @return The circularly linked list of pages
     */
    CircularLinkedList<Page<?>> pages();

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
        return Impactor.getInstance().getRegistry().createBuilder(PaginationBuilder.class);
    }

    interface PaginationBuilder extends UIComponent<PaginationBuilder>, Builder<Pagination, PaginationBuilder> {

        /**
         * Sets the key referencing the provider of this pagination. This key provides both a namespace
         * and a value to identity the type of pagination, as well as a means of reference to a particular
         * pagination in the event an error occurs during its processing.
         *
         * @param key The key containing the namespace and value information of a provider.
         * @return The updated builder
         */
        @Required
        PaginationBuilder provider(Key key);

        /**
         * Sets the viewer of the pagination. This will control who is capable of controlling and viewing
         * the actual pagination.
         *
         * @param viewer The player that will view this pagination
         * @return The updated builder
         */
        @Required
        PaginationBuilder viewer(PlatformPlayer viewer);

        /**
         * Sets the contents of the pagination to the following icons. If the list of icons is more
         * than can be carried in a singular page, the following icons will be associated with further
         * pages until no more pages become necessary.
         *
         * @param icons The icons to associate with the pagination
         * @return The updated builder
         */
        PaginationBuilder contents(List<Icon<?>> icons);

        /**
         * Indicates the section that a page will draw its contents in. If this section overlaps with the layout,
         * the content zone will override the affected slots of the layout. This will draw the content zone in the
         * top left corner of the interface. If you wish to move this section around, consider using
         * {@link #zone(Vector2i, Vector2i)} instead.
         *
         * If the given dimensions cannot fit within the viewable interface, an {@link IllegalArgumentException}
         * will be invoked to identify the issue.
         *
         * @param dimensions The dimensions for the location where icons should be drawn in the pagination view.
         * @return The updated builder
         */
        default PaginationBuilder zone(Vector2i dimensions) {
            return this.zone(dimensions, Vector2i.ZERO);
        }

        /**
         * Indicates the section that a page will draw its contents in. If this section overlaps with the layout,
         * the content zone will override the affected slots of the layout.
         *
         * If the given dimensions cannot fit within the viewable interface, an {@link IllegalArgumentException}
         * will be invoked to identify the issue.
         *
         * @param dimensions The dimensions for the location where icons should be drawn in the pagination view.
         * @param offset An offset that adjusts the placement of the content zone
         * @return The updated builder
         */
        PaginationBuilder zone(Vector2i dimensions, @Nullable Vector2i offset);

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
        PaginationBuilder updater(PageUpdater updater);

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
        PaginationBuilder style(TriState state);

    }

}
