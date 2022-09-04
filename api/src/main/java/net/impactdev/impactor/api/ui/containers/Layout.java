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

import com.google.common.collect.ImmutableMap;
import net.impactdev.impactor.api.builders.Builder;
import org.jetbrains.annotations.Contract;

import java.util.Optional;
import java.util.function.Consumer;

public interface Layout {

	ImmutableMap<Integer, Icon> elements();

	Optional<Icon> icon(int slot);

	interface LayoutBuilder<T extends Layout, B extends LayoutBuilder<T, B>> extends Builder<T> {

		/**
		 * Sets a singular slot in the layout to the icon given. Whether an icon will be successfully set
		 * is determined on the dimensions of the UI being interacted with. Any values outside this range
		 * will provoke an {@link IllegalArgumentException}.
		 *
		 * @param icon The icon to place in the slot
		 * @param slot The slot to insert the icon at
		 * @return The layout builder following the attempted update
		 * @throws IllegalArgumentException If the given slot is not within the bounds of the container
		 */
		@Contract("_,_ -> this")
		B slot(Icon icon, int slot);

		/**
		 * Sets the following icon to the set of slots given.
		 * <p>
		 * Any slot values outside this range will provoke an {@link IllegalArgumentException}.
		 *
		 * @param icon
		 * @param slots
		 * @return
		 * @throws IllegalArgumentException If a given slot is not within the bounds of the container
		 */
		@Contract("_,_ -> this")
		default B slots(Icon icon, int... slots) {
			for(int slot : slots) {
				this.slot(icon, slot);
			}

			return (B) this;
		}

		/**
		 * Attempts to fill any remaining slots not currently set with the following icon.
		 *
		 * @param icon The icon to fill the inventory with, if any slots are left available
		 * @return The layout builder following the attempted update
		 */
		@Contract("_ -> this")
		B fill(Icon icon);

		/**
		 * Allows for customized drawing patterns to be directly applied to the layout builder. For instance,
		 * you can directly use the builder to have it draw a triangle into the layout versus individual
		 * slot calls. The concept of this method is simply to allow for extension beyond the provided shape
		 * drawing providers.
		 * <p>
		 * The builder is provided such that you can make direct calls to components of the builder itself,
		 * so individual slots could be set with {@link #slot(Icon, int)}, or you can even mess around with
		 * the given shapes for your custom method.
		 *
		 * @param consumer The consumer to enact against the builder, which can draw new slots to the layout
		 * @return The updated builder
		 */
		@Contract("_ -> this")
		B consume(Consumer<B> consumer);

		@Contract("_ -> this")
		B from(Layout layout);

	}
}
