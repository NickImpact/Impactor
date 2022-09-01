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

package net.impactdev.impactor.api.ui.containers.views.pagination.sectioned.builders;

import net.impactdev.impactor.api.ui.containers.Icon;
import net.impactdev.impactor.api.ui.containers.views.pagination.updaters.PageUpdater;
import net.impactdev.impactor.api.ui.containers.views.pagination.updaters.PageUpdaterType;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.util.TriState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.math.vector.Vector2i;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public interface SectionBuilder<T extends SectionBuilder<T>> {

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
    default T dimensions(int columns, int rows) {
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
    T dimensions(Vector2i dimensions);

    /**
     * Specifies the relocation of the section. This controls where the initial dimension
     * of the viewable section exists in the view. This offset is based on the (0, 0) grid
     * location within the view of the inventory, or otherwise, slot index 0.
     *
     * @param columns The number of columns to offset by
     * @param rows The number of rows to offset by
     * @return The updated builder
     */
    default T offset(int columns, int rows) {
        return this.offset(Vector2i.from(columns, rows));
    }

    /**
     * Specifies the relocation of the section. This controls where the initial dimension
     * of the viewable section exists in the view. This offset is based on the (0, 0) grid
     * location within the view of the inventory, or otherwise, slot index 0.
     *
     * @param offset The offset from (0, 0)
     * @return The updated builder
     */
    T offset(Vector2i offset);

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
    T updater(PageUpdater updater);

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
    T style(TriState state);

    /**
     * Marks the section configuration as complete and sound, and returns to the parent builder
     * so that further changes can be applied to that configuration.
     *
     * @return The parent builder that created this builder
     */
    SectionedPaginationBuilder complete();

    /**
     * Represents a set of builders that create a synchronous section. These
     */
    interface Synchronous<B extends Synchronous<B>> extends SectionBuilder<B> {

        interface Basic extends Synchronous<Basic> {

            /**
             * Sets the contents of the pagination to the following icons. If the list of icons is more
             * than can be carried in a singular page, the following icons will be associated with further
             * pages until no more pages become necessary.
             *
             * @param contents The icons to associate with the pagination
             * @return The updated builder
             */
            Basic contents(List<Icon> contents);

        }

        interface Generic<T> extends Synchronous<Generic<T>> {


            Generic<T> filter(Predicate<T> filter);

            Generic<T> sort(Comparator<T> sorter);

        }

    }

    interface Asynchronous<B extends Asynchronous<B>> extends SectionBuilder<B> {

        /**
         * Specifies the icon that will fill the section whilst waiting on the accumulator
         * to complete its collection of icons.
         *
         * @param filler The icon to fill the section with
         * @return The updated builder
         */
        B waiting(Icon filler);

        /**
         * Specifies the amount of time the accumulator is permitted to wait before
         * timing out, and ultimately filling the section with the filler icon specified.
         *
         * <p>By default, this will resolve to a 5-second limit, with an Impactor
         * specified icon.
         *
         * @param amount The amount of time to wait, paired with the given unit
         * @param unit The unit to indicate the actual unit of time to wait
         * @param filler The icon to fill the section with, or null to allow Impactor
         *               to substitute its own icon.
         * @return The updated builder
         */
        B timeout(long amount, TimeUnit unit, @Nullable Icon filler);

        interface Basic extends Asynchronous<Basic> {

            /**
             * Accumulates the icons for the section over time, where they might not necessarily be
             * immediately ready at time of UI opening. Once available, the section will then be
             * populated with the resulting icons.
             *
             * <p>The accumulator given will be bound to a function to control the amount of time
             * the UI will wait before suggesting that the icons could not be received in time.
             * By default, sections are expected to allow only 5 seconds before indicating this
             * result. If you wish to control these details, you can make use of
             * {@link #timeout(long, TimeUnit, Icon)}, which will fill the section with the given
             * replacement icon after the amount of time elapses.
             *
             * <p>This method may not necessarily be supplied. If not supplied, the builder
             * will substitute the future with {@link CompletableFuture#completedFuture(Object)
             * a completed future}, which will use an empty list to indicate its contents.
             *
             * @param accumulator The function responsible for providing the set of icons to
             *                    fill the pagination over time.
             * @return The updated builder
             */
            Basic accumulator(CompletableFuture<List<Icon>> accumulator);

        }

        interface Generic<T> extends Asynchronous<Generic<T>> {

            Generic<T> filter(Predicate<T> filter);

            Generic<T> sort(Comparator<T> sorter);

        }

    }

}
