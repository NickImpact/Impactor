package net.impactdev.impactor.api.services.text;

import net.impactdev.impactor.api.placeholders.PlaceholderSources;
import net.impactdev.impactor.api.services.Service;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Represents a service responsible for raw string based inputs being translated into the platform
 * specific text representation. This service may optionally also make use of input source suppliers
 * to apply them to systems like placeholders.
 *
 * @param <T> The output type for a message
 */
public interface MessageService<T> extends Service {

	/**
	 * Parses a message with no expectation of sources being present. In other words,
	 * source required text replacements provided will not have any available sources to
	 * work with.
	 *
	 * @param message The message we wish to translate
	 * @return The translated message
	 */
	default T parse(@NonNull String message) {
		return this.parse(message, PlaceholderSources.empty());
	}

	/**
	 * Parses a message based on the input source suppliers. Each source should be parsed for a required
	 * placeholder in a FIFO order until the result is something other than empty. If empty after all sources,
	 * are parsed, it's up to the implementation to decide how unfilled placeholders are demonstrated.
	 *
	 * @param message The message to translate
	 * @param sources The sources made available via suppliers for any required contextual information
	 * @return The translated message
	 */
	T parse(@NonNull String message, @NonNull PlaceholderSources sources);

	/**
	 * Translates a set of input messages with no associated sources. See {@link #parse(String)} to
	 * see how the supplied input will be translated.
	 *
	 * @param input The list of input messages to translate
	 * @return A translated list of messages based on the input
	 */
	default List<T> parse(@NonNull List<String> input) {
		return this.parse(input, PlaceholderSources.empty());
	}

	/**
	 * Translates a set of input messages with the provided source suppliers. See {@link #parse(String, PlaceholderSources)} to
	 * see how the supplied input will be translated.
	 *
	 * @param input
	 * @param sources
	 * @return
	 */
	default List<T> parse(@NonNull List<String> input, @NonNull PlaceholderSources sources) {
		return input.stream().map(s -> this.parse(s, sources)).collect(Collectors.toList());
	}

}
