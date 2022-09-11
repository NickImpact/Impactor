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
import net.impactdev.impactor.api.ui.containers.views.pagination.rules.ContextRuleset;
import net.impactdev.impactor.api.ui.containers.views.pagination.updaters.PageUpdater;
import net.impactdev.impactor.api.ui.containers.views.pagination.updaters.PageUpdaterType;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.util.TriState;
import org.jetbrains.annotations.Contract;
import org.spongepowered.math.vector.Vector2i;

import java.util.List;

public interface SectionBuilder {

    @Contract("_ -> this")
    SectionBuilder contents(List<Icon> icons);

    @Contract("_ -> this")
    SectionBuilder ruleset(ContextRuleset ruleset);

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
    @Contract("_,_ -> this")
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
    @Contract("_ -> this")
    SectionBuilder dimensions(Vector2i dimensions);

    /**
     * Specifies the relocation of the section. This controls where the initial dimension
     * of the viewable section exists in the view. This offset is based on the (0, 0) grid
     * location within the view of the inventory, or otherwise, slot index 0.
     *
     * @param columns The number of columns to offset by
     * @param rows The number of rows to offset by
     * @return The updated builder
     */
    @Contract("_,_ -> this")
    default SectionBuilder offset(int columns, int rows) {
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
    @Contract("_ -> this")
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
    @Contract("_ -> this")
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
    @Contract("_ -> this")
    SectionBuilder style(TriState state);

    /**
     * Marks the section configuration as complete and sound, and returns to the parent builder
     * so that further changes can be applied to that configuration.
     *
     * @return The parent builder that created this builder
     */
    SectionedPaginationBuilder complete();

}
