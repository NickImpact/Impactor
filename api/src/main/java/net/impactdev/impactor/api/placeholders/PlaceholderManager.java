package net.impactdev.impactor.api.placeholders;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;

import java.util.Optional;

public interface PlaceholderManager<T, S> {

    void register(T parser);

    ImmutableList<T> getAllInternalParsers();

    ImmutableList<S> getAllPlatformParsers();

    void populate();

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    default <E> Optional<E> filterSource(Class<E> want, Optional<Object> source) {
        return source.filter(x -> x.getClass().isAssignableFrom(want)).map(want::cast);
    }

}
