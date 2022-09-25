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

package net.impactdev.impactor.api.ui.containers.views;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.builders.Builder;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.ui.containers.View;
import net.impactdev.impactor.api.ui.containers.layouts.ChestLayout;
import org.jetbrains.annotations.Contract;

/**
 * Represents the forward facing UI construct that allows a player to view the contents of a given
 * UI. By nature, this API assumes both a shared interface perspective with the allowance of a unique
 * perspective based on the implementation.
 *
 */
public interface ChestView extends View {

	@Override
    ChestLayout layout();

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

	static ChestViewBuilder builder() {
		return Impactor.instance().builders().provide(ChestViewBuilder.class);
	}

	interface ChestViewBuilder extends BaseViewBuilder<ChestViewBuilder>, Builder<ChestView> {

		/**
		 * Defines the layout of the UI.
		 *
		 * @param layout The layout that'll be used for the UI
		 * @return The updated builder
		 */
		@Contract("_ -> this")
		ChestViewBuilder layout(ChestLayout layout);

		@Contract("_ -> this")
		ChestViewBuilder from(ChestView parent);

	}

}
