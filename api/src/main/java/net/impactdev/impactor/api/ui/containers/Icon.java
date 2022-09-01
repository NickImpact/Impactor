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

import io.leangen.geantyref.TypeToken;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.builders.Builder;
import net.impactdev.impactor.api.builders.Required;
import net.impactdev.impactor.api.items.ImpactorItemStack;
import net.impactdev.impactor.api.ui.containers.processors.ClickProcessor;
import net.impactdev.impactor.api.utilities.context.Context;
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
public interface Icon {

	/**
	 * Gets the actual platform representation of the icon. This display simply represents how a client will
	 * see an icon within a UI.
	 *
	 * @return The actual platform respective ItemStack representing the client perspective
	 */
	@NotNull Supplier<ImpactorItemStack> display();

	/**
	 * Represents additional context that might be applied to an icon. This is meant to carry
	 * objects that might be relevant later on for situations like processing click events.
	 *
	 * @return A set of context with potentially a set of additional parameters. If no context is
	 * provided, this will return {@link Context#empty()}
	 */
	@NotNull Context context();

	/**
	 * Provides the set of listeners attached to this icon, as an immutable set.
	 *
	 * @return An immutable set of listeners attached to the icon
	 */
	@NotNull Set<ClickProcessor> listeners();

	/**
	 * Appends a new click processor to the already created icon. This is meant for dynamic utility
	 * where updating an icon variable based on later creation context is not available.
	 *
	 * @param processor The processor to append to the icon
	 * @return This icon with the newly appended processor
	 */
	@Contract("_ -> this")
	Icon listener(ClickProcessor processor);

	/**
	 * Indicates whether a particular icon can be refreshed when requested by a view. If true,
	 * the view will effectively recall {@link #display()} to generate a new version of the display
	 * for the view.
	 *
	 * @return <code>true</code> if refreshable, <code>false</code> otherwise
	 */
	boolean refreshable();

	static IconBuilder builder() {
		return Impactor.instance().builders().provide(IconBuilder.class);
	}

	/**
	 * Constructs an icon based on the following parameters. At a minimum, a display for an icon is required
	 * for it to be valid. Otherwise, listeners can be added as desired.
	 */
	interface IconBuilder extends Builder<Icon> {

		/**
		 * Sets the display of the icon to the following platform-based ItemStack.
		 * <p>
		 * This field is required. Omitting this at request of build will invoke an {@link IllegalStateException}.
		 *
		 * @param display A provider of the viewable ItemStack to act as the icons client-facing display
		 * @return The current builder
		 */
		@Required
		IconBuilder display(Supplier<ImpactorItemStack> display);

		/**
		 * Appends a listener to the icon. This listener will act as a means of handling any click action
		 * made by a player on the icon. Note that an icon can have multiple listeners if desired.
		 *
		 * @param processor The processor to append to the Icon's listener set.
		 * @return The current builder
		 */
		IconBuilder listener(ClickProcessor processor);

		/**
		 * Adds a contextual parameter to the icon, which can then later be requested or required
		 * by a given implementation. For instance, you might have a situation where an icon needs
		 * to have a contextual reference to a particular object. That listener can then request/require
		 * from the context that particular parameter to actually work against it.
		 *
		 * @param key The key to the contextual parameter
		 * @param value The value representing the additional context
		 * @return The current builder
		 * @param <T> The type shared by the key and contextual value
		 */
		default <T> IconBuilder append(Class<T> key, T value) {
			return this.append(TypeToken.get(key), value);
		}

		/**
		 * Adds a contextual parameter to the icon, which can then later be requested or required
		 * by a given implementation. For instance, you might have a situation where an icon needs
		 * to have a contextual reference to a particular object. That listener can then request/require
		 * from the context that particular parameter to actually work against it.
		 *
		 * @param key The key to the contextual parameter
		 * @param value The value representing the additional context
		 * @return The current builder
		 * @param <T> The type shared by the key and contextual value
		 */
		<T> IconBuilder append(TypeToken<T> key, T value);

		/**
		 * Specifies that the following icon is a constant, and should not be affected
		 * by a view refresh.
		 *
		 * @return The current builder
		 */
		IconBuilder constant();

		IconBuilder from(Icon parent);

	}

}
