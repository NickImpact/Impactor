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

package net.impactdev.impactor.api.ui.containers.views.pagination.builders;

import net.impactdev.impactor.api.builders.Builder;
import net.impactdev.impactor.api.builders.Required;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.ui.containers.Icon;
import net.impactdev.impactor.api.ui.containers.layouts.ChestLayout;
import net.impactdev.impactor.api.ui.containers.views.BaseViewBuilder;
import net.impactdev.impactor.api.ui.containers.views.pagination.Pagination;
import net.impactdev.impactor.api.ui.containers.views.pagination.rules.ContextRuleset;
import net.impactdev.impactor.api.ui.containers.views.pagination.updaters.PageUpdater;
import net.impactdev.impactor.api.ui.containers.views.pagination.updaters.PageUpdaterType;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.util.TriState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.math.vector.Vector2i;

import java.util.Collection;

public interface PaginationBuilder extends BaseViewBuilder<PaginationBuilder>, Builder<Pagination> {

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

    PaginationBuilder layout(ChestLayout layout);

    PaginationBuilder contents(Collection<Icon> icons);

    /**
     * Indicates the section that a page will draw its contents in. If this section overlaps with the layout,
     * the content zone will override the affected slots of the layout. This will draw the content zone in the
     * top left corner of the interface. If you wish to move this section around, consider using
     * {@link #zone(Vector2i, Vector2i)} instead.
     * <p>
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
     * <p>
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
     *
     *
     * @param ruleset
     * @return
     */
    PaginationBuilder ruleset(ContextRuleset ruleset);

}
