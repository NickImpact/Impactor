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

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.builders.Required;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.ui.containers.components.UIComponent;
import net.impactdev.impactor.api.ui.containers.detail.RefreshDetail;
import net.impactdev.impactor.api.ui.containers.detail.RefreshTypes;
import net.impactdev.impactor.api.ui.containers.icons.Icon;
import net.impactdev.impactor.api.ui.containers.layouts.Layout;
import net.impactdev.impactor.api.builders.Builder;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the forward facing UI construct that allows a player to view the contents of a given
 * UI. By nature, this API assumes both a shared interface perspective with the allowance of a unique
 * perspective based on the implementation.
 *
 */
public interface ImpactorUI {

	/**
	 * Represents the key of the plugin providing this UI. This is meant to help identify a particular UI
	 * which might have an issue with its logic chain.
	 *
	 * @return The {@link Key} representing the providing plugin of the UI
	 */
	Key namespace();

	/**
	 * Represents the layout that will be displayed on this particular UI.
	 *
	 * @return The layout of the UI
	 */
	Layout layout();

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
	 * Opens the view for the specified player.
	 *
	 * @param viewer The player who should view the inventory
	 */
	void open(PlatformPlayer viewer);

	/**
	 * Closes the view of the inventory, only if the viewer's open inventory is this inventory.
	 * If the view they currently have open is not this view, then this call is ignored
	 *
	 * @param viewer The player who should have this view closed if they have it open
	 */
	void close(PlatformPlayer viewer);

	/**
	 * Attempts to refresh the contents of the UI based on the given refresh details.
	 *
	 * <p>The valid refresh types for this type of UI are:
	 * <ul>
	 *     <li>{@link RefreshTypes#ALL ALL}</li>
	 *     <li>{@link RefreshTypes#LAYOUT LAYOUT}</li>
	 *     <li>{@link RefreshTypes#SLOT_INDEX SLOT_INDEX}</li>
	 *     <li>{@link RefreshTypes#SLOT_POS SLOT_POS}</li>
	 *     <li>{@link RefreshTypes#GRID}</li>
	 * </ul>
	 *
	 * Any other typed detail will be rejected and will act as a no-op.
	 * <dl>
	 *     <dt><span class="strong">{@link RefreshTypes#SLOT_INDEX SLOT_INDEX} and
	 * 	       {@link RefreshTypes#SLOT_POS SLOT_POS} Notes</span>
	 *     </dt>
	 *     <dd>
	 *         As part of the details, these two refresh types expect additional context to their targets.
	 * 	       This is accomplished by appending data points to the context of the newly created details.
	 *     </dd>
	 * </dl>
	 *
	 * @param detail The details regarding refreshing the inventory
	 */
	void refresh(RefreshDetail detail);

	static UIBuilder builder() {
		return Impactor.instance().builders().provide(UIBuilder.class);
	}

	interface UIBuilder extends UIComponent<UIBuilder>, Builder<ImpactorUI> {

		/**
		 * Sets the key referencing the provider of this pagination. This key provides both a namespace
		 * and a value to identity the type of pagination, as well as a means of reference to a particular
		 * pagination in the event an error occurs during its processing.
		 *
		 * @param key The key containing the namespace and value information of a provider.
		 * @return The updated builder
		 */
		@Required
		UIBuilder provider(Key key);

		UIBuilder from(ImpactorUI parent);

	}

}
