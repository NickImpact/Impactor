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

package net.impactdev.impactor.api.ui;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.ui.components.UIComponent;
import net.impactdev.impactor.api.ui.icons.Icon;
import net.impactdev.impactor.api.ui.layouts.Layout;
import net.impactdev.impactor.api.utilities.Builder;
import net.kyori.adventure.key.Key;

import javax.annotation.Nullable;

/**
 * Represents the forward facing UI construct that allows a player to view the contents of a given
 * UI. By nature, this API assumes both a shared interface perspective with the allowance of a unique
 * perspective based on the implementation.
 *
 * @param <P> The platform specific instance of a player capable of viewing the UI
 */
public interface ImpactorUI<P> {

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
	void set(@Nullable Icon<?> icon, int slot);

	/**
	 * Opens the view for the specified player.
	 *
	 * @param viewer The player who should view the inventory
	 * @return <code>true</code> if the inventory was opened and available, <code>false</code> otherwise
	 */
	boolean open(P viewer);

	static <P> UIBuilder<P> builder(Class<P> typing) {
		return (UIBuilder<P>) Impactor.getInstance().getRegistry().createBuilder(UIBuilder.class);
	}

	interface UIBuilder<P> extends UIComponent<UIBuilder<P>>, Builder<ImpactorUI<P>, UIBuilder<P>> {

		/**
		 * Sets the key referencing the provider of this pagination. This key provides both a namespace
		 * and a value to identity the type of pagination, as well as a means of reference to a particular
		 * pagination in the event an error occurs during its processing.
		 *
		 * @param key The key containing the namespace and value information of a provider.
		 * @return The updated builder
		 */
		@Required
		UIBuilder<P> provider(Key key);

	}

}
