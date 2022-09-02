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

package net.impactdev.impactor.api.ui.containers;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.checkerframework.common.value.qual.IntRange;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.math.vector.Vector2i;

import java.util.Arrays;

public interface View {

    /**
     * Represents the key of the plugin providing this UI. This is meant to help identify a particular UI
     * which might have an issue with its logic chain.
     *
     * @return The {@link Key} representing the providing plugin of the UI
     */
    Key namespace();

    /**
     * Represents the title of the given view. This is what is expected to be displayed
     * at the top of the view interface.
     *
     * @return The title of the view
     */
    Component title();

    /**
     * Represents the layout that will be displayed on this particular UI.
     *
     * @return The layout of the UI
     */
    Layout layout();

    /**
     * Specifies the dimensions of the view. Since columns will typically always be 9
     * for a chest view, this value simply represents the number of rows existing in the view.
     * This number is expected to be between 1 and 6.
     *
     * @return The number of rows in the view
     */
    @IntRange(from = 1, to = 6)
    int rows();

    /**
     * Indicates if the view is readonly, and all click events should be cancelled for all
     * contexts by default. This will override an icon's individual allowance.
     *
     * @return <code>true</code> if in a read only state, <code>false</code> otherwise
     */
    boolean readonly();

    /**
     * Allows for dynamic setting of UI contents after UI construction. To clear a slot, the provided slot
     * can be null. If the given slot is outside the UI's boundaries, this will trigger an {@link IllegalArgumentException}.
     *
     * @param icon The icon to set at the specified slot
     * @param slot The slot to set the icon at
     * @throws IllegalArgumentException If the given slot is outside the boundaries of the UI.
     */
    void set(@Nullable Icon icon, int slot);

    /**
     * Refreshes the entire view, assuming an icon supports refreshing. If not, refreshing
     * for an icon will simply be skipped.
     */
    default void refresh() {
        this.refresh(Vector2i.from(9, this.rows()), Vector2i.ZERO);
    }

    /**
     * Refreshes an individual slot within the view, should that slot accept being refreshed.
     *
     * @param slot The target slot for the refresh action
     */
    default void refresh(int slot) {
        this.refresh(Vector2i.ONE, Vector2i.from(slot / 9, slot % 9));
    }

    default void refresh(int... slots) {
        Arrays.stream(slots).forEach(this::refresh);
    }

    /**
     * Refreshes a particular section of the view, up to the view's max size.
     *
     * @param dimensions The dimension range within the view to refresh
     * @param offsets Offsets to the given dimensions. This is meant to allow you to directly
     *                position the dimensions from the top-left of the view.
     */
    void refresh(Vector2i dimensions, Vector2i offsets);

}
