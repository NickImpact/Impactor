package net.impactdev.impactor.api.placeholders;

import io.leangen.geantyref.TypeToken;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.utilities.Builder;

import java.util.Optional;
import java.util.function.Supplier;

public interface PlaceholderSources {

    static PlaceholderSources empty() {
        return PlaceholderSources.builder().build();
    }

    <T> Optional<T> getSource(Class<T> type);

    <T> Optional<T> getSource(TypeToken<T> type);

    static SourceBuilder builder() {
        return Impactor.getInstance().getRegistry().createBuilder(SourceBuilder.class);
    }

    interface SourceBuilder extends Builder<PlaceholderSources, SourceBuilder> {

        <T> SourceBuilder append(Class<T> type, Supplier<T> supplier);

        <T> SourceBuilder append(TypeToken<T> type, Supplier<T> supplier);

    }

}
