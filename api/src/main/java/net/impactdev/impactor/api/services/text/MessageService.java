package net.impactdev.impactor.api.services.text;

import net.impactdev.impactor.api.services.Service;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 *
 *
 * @param <T> The output type for a message
 */
public interface MessageService<T> extends Service {

	default T parse(@NonNull String message) {
		return this.parse(message, Collections.emptyList());
	}

	T parse(@NonNull String message, @NonNull List<Supplier<Object>> associations);

	default List<T> parse(@NonNull List<String> input) {
		return this.parse(input, Collections.emptyList());
	}

	default List<T> parse(@NonNull List<String> input, @NonNull List<Supplier<Object>> associations) {
		return input.stream().map(s -> this.parse(s, associations)).collect(Collectors.toList());
	}

}
