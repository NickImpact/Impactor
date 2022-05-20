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

package net.impactdev.impactor.api.ui.containers.pagination.builders;

import io.leangen.geantyref.TypeToken;
import net.impactdev.impactor.api.builders.Builder;
import net.impactdev.impactor.api.builders.Required;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.ui.containers.components.UIComponent;
import net.impactdev.impactor.api.ui.containers.icons.Icon;
import net.impactdev.impactor.api.ui.containers.pagination.Pagination;
import net.impactdev.impactor.api.ui.containers.pagination.updaters.PageUpdater;
import net.impactdev.impactor.api.ui.containers.pagination.updaters.PageUpdaterType;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.util.TriState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.math.vector.Vector2i;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public interface PaginationBuilder extends UIComponent<PaginationBuilder>, Builder<Pagination> {

    /**
     * Specifies the provider of the pagination. This is meant to act as an identifier for a plugin
     * as well as the type of pagination in play. If an exception occurs during a tracked scenario
     * with the pagination, the stacktrace will use this key to help indicate the source pagination.
     *
     * <p>This field is required by the builder. Not specifying this detail will invoke an exception
     * once the pagination is built.
     *
     * @param provider The key acting as the identifier of the pagination
     * @return The updated builder
     */
    @Required
    PaginationBuilder provider(Key provider);

    /**
     * Specifies the viewer of the pagination. This is the player that will ultimately view the
     * inventory when it is opened.
     *
     * @param viewer The viewer of the pagination
     * @return The updated builder
     */
    @Required
    PaginationBuilder viewer(PlatformPlayer viewer);

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

    /**
     * Creates a synchronous pagination with no specified binding type. In other words, pagination
     * contents are expected to be available as soon as this builder completes. If you need to
     * wait for an amount of time before the items might be available, but don't wish to block
     * the server thread, consider using an {@link #asynchronous() asynchronous} pagination
     * instead.
     *
     * @return A builder for a synchronous inventory with no binding type
     */
    Synchronous.Basic synchronous();

    default <T> Synchronous.Generic<T> synchronous(Class<T> type) {
        return this.synchronous(TypeToken.get(type));
    }

    <T> Synchronous.Generic<T> synchronous(TypeToken<T> type);

    Asynchronous.Basic asynchronous();

    default <T> Asynchronous.Generic<T> asynchronous(Class<T> type) {
        return this.asynchronous(TypeToken.get(type));
    }

    <T> Asynchronous.Generic<T> asynchronous(TypeToken<T> type);

    /**
     * This method, despite its name, will simply throw an exception in favor of the sub-builders
     * build method. This method is only provided as its a requirement of the builder interface, but
     * in this use case, has no function.
     *
     * @return Nothing
     * @throws UnsupportedOperationException Indicates that you should not use this method at all
     */
    @Override
    default Pagination build() {
        throw new UnsupportedOperationException("Must be built from an implementor of PaginationBase");
    }

    interface PaginationCompleter<T extends Pagination> {

        T build();

    }

    interface Synchronous<T extends Pagination> extends PaginationCompleter<T> {

        interface Basic extends Synchronous<Pagination> {

            /**
             * Sets the contents of the pagination to the following icons. If the list of icons is more
             * than can be carried in a singular page, the following icons will be associated with further
             * pages until no more pages become necessary.
             *
             * @param icons The icons to associate with the pagination
             * @return The updated builder
             */
            Basic contents(List<Icon> icons);

        }

        interface Generic<T> extends Synchronous<Pagination.Generic<T>> {

            Generic<T> contents(List<Icon.Binding<T>> icons);

            Generic<T> filter(Predicate<T> filter);

            Generic<T> sort(Comparator<T> sorter);

        }

    }

    interface Asynchronous<T extends Pagination, B extends Asynchronous<T, B>> extends PaginationCompleter<T> {

        /**
         * Indicates the timeframe this pagination will allow before timing out. In essence,
         * the accumulator specified with {@link Asynchronous.Basic#accumulator(CompletableFuture)}
         * or {@link } must
         * complete within the specified unit of time to populate the pagination. If not,
         * the pagination will be filled with the given icon.
         *
         * @param time The amount of time before the accumulator can time out
         * @param unit The unit of time for the time measurement
         * @param icon The icon to fill the pagination with if the accumulator times out
         * @return The updated builder
         */
        B timeout(long time, TimeUnit unit, Icon icon);

        B waiting(Icon icon);

        interface Basic extends Asynchronous<Pagination, Basic> {

            @Required
            Basic accumulator(CompletableFuture<List<Icon>> provider);

        }

        interface Generic<T> extends Asynchronous<Pagination.Generic<T>, Generic<T>> {

            Generic<T> accumulator(CompletableFuture<List<Icon.Binding<T>>> accumulator);

            Generic<T> filter(Predicate<T> filter);

            Generic<T> sort(Comparator<T> sorter);

        }

    }

}
