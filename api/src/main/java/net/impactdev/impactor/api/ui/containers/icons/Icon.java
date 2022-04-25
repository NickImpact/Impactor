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

package net.impactdev.impactor.api.ui.containers.icons;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.builders.Builder;
import net.impactdev.impactor.api.builders.Required;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.Supplier;

/**
 * An icon represents an ItemStack that will be used for displaying a component within a UI. Each icon allows
 * for a set of listeners to be attached to the icon so that it may process any particular click events respective
 * to itself. It is not required that an icon provide any listeners at all, and any button without an attached
 * event will simply act as a display icon.
 */
public interface Icon<T> {

	/**
	 * Gets the actual platform representation of the icon. This display simply represents how a client will
	 * see an icon within a UI.
	 *
	 * @return The actual platform respective ItemStack representing the client perspective
	 */
	@NotNull DisplayProvider<T> display();

	/**
	 * Provides the set of listeners attached to this icon, as an immutable set.
	 *
	 * @return An immutable set of listeners attached to the icon
	 */
	Set<ClickProcessor> listeners();

	/**
	 * Appends a new click processor to the already created icon. This is meant for dynamic utility
	 * where updating an icon variable based on later creation context is not available.
	 *
	 * @param processor The processor to append to the icon
	 * @return This icon with the newly appended processor
	 */
	@Contract("_ -> this")
	Icon<T> listener(ClickProcessor processor);

	static <T> IconBuilder<T> builder(Class<T> typing) {
		return (IconBuilder<T>) Impactor.getInstance().getRegistry().createBuilder(IconBuilder.class);
	}

	/**
	 * Represents an icon which is bound to a given object. These types of icons are convenient
	 * for solutions which may attempt to sort icons based on their creating type.
	 *
	 * @param <D> The type of display viewable to the client
	 * @param <T> The type of object bound to this icon
	 */
	interface Binding<D, T> extends Icon<D> {

		/**
		 * The bound object to this icon. This binding will be used for elements such as sorting,
		 * or can even provide additional information related to icon interactions.
		 *
		 * @return The bound object of the icon
		 */
		T binding();

	}

	/**
	 * Constructs an icon based on the following parameters. At a minimum, a display for an icon is required
	 * for it to be valid. Otherwise, listeners can be added as desired.
	 *
	 * @param <T> The platform-based ItemStack typing
	 */
	interface IconBuilder<T> extends Builder<Icon<T>> {

		/**
		 * Sets the display of the icon to the following platform-based ItemStack.
		 *
		 * This field is required. Omitting this at request of build will invoke an {@link IllegalStateException}.
		 *
		 * @param display A provider of the viewable ItemStack to act as the icons client-facing display
		 * @return The current builder
		 */
		@Required
		IconBuilder<T> display(DisplayProvider<T> display);

		/**
		 * Appends a listener to the icon. This listener will act as a means of handling any click action
		 * made by a player on the icon. Note that an icon can have multiple listeners if desired.
		 *
		 * @param processor The processor to append to the Icon's listener set.
		 * @return The current builder
		 */
		IconBuilder<T> listener(ClickProcessor processor);

		IconBuilder<T> from(Icon<?> parent);

		/**
		 * Builds a which is bound to the instance given as a binding object. This object will be used for
		 * operations such as sorting, and allows working with the source object represented through
		 * a displayable item rather than parsing components of the display itself.
		 *
		 * @param binding The object to bind to the icon
		 * @return A new icon built with the bound object
		 * @param <E> The typing of the binding object
		 */
		<E> Icon.Binding<T, E> build(Supplier<E> binding);

	}

}
