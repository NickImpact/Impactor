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

package net.impactdev.impactor.api.services.text;

import net.impactdev.impactor.api.services.Service;
import net.impactdev.impactor.api.utilities.context.Context;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a service responsible for raw string based inputs being translated into the platform
 * specific text representation. This service may optionally also make use of input source suppliers
 * to apply them to systems like placeholders.
 */
public interface MessageService extends Service {

	/**
	 * Parses a message with no expectation of sources being present. In other words,
	 * source required text replacements provided will not have any available sources to
	 * work with.
	 *
	 * @param message The message we wish to translate
	 * @return The translated message
	 */
	default Component parse(@NonNull String message) {
		return this.parse(message, Context.empty());
	}

	/**
	 * Parses a message based on the input source suppliers. Each source should be parsed for a required
	 * placeholder in a FIFO order until the result is something other than empty. If empty after all sources,
	 * are parsed, it's up to the implementation to decide how unfilled placeholders are demonstrated.
	 *
	 * @param message The message to translate
	 * @param context The context made available via suppliers for any required contextual information
	 * @return The translated message
	 */
	Component parse(@NonNull String message, Context context);

	/**
	 * Translates a set of input messages with no associated sources. See {@link #parse(String)} to
	 * see how the supplied input will be translated.
	 *
	 * @param input The list of input messages to translate
	 * @return A translated list of messages based on the input
	 */
	default List<Component> parse(@NonNull List<String> input) {
		return this.parse(input, Context.empty());
	}

	/**
	 * Translates a set of input messages with the provided source suppliers. See {@link #parse(String, Context)} to
	 * see how the supplied input will be translated.
	 *
	 * @param input
	 * @param context
	 * @return
	 */
	default List<Component> parse(@NonNull List<String> input, @NonNull Context context) {
		return input.stream().map(s -> this.parse(s, context)).collect(Collectors.toList());
	}

}
