package net.impactdev.impactor.api.placeholders;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;

import java.util.Optional;

public interface PlaceholderManager<T, S> {

    void register(T parser);

    ImmutableList<T> getAllInternalParsers();

    ImmutableList<S> getAllPlatformParsers();

    void populate();

}
