package net.impactdev.impactor.common.placeholders;

import com.google.common.collect.Maps;
import io.leangen.geantyref.TypeToken;
import net.impactdev.impactor.api.placeholders.PlaceholderSources;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class PlaceholderSourcesImpl implements PlaceholderSources {

    private final Map<TypeToken<?>, Supplier<?>> sources = Maps.newHashMap();

    private PlaceholderSourcesImpl(PlaceholderSourcesBuilderImpl builder) {
        this.sources.putAll(builder.sources);
    }

    @Override
    public <T> Optional<T> getSource(Class<T> type) {
        return this.getSource(TypeToken.get(type));
    }

    @Override
    public <T> Optional<T> getSource(TypeToken<T> type) {
        return Optional.ofNullable((T) this.sources.get(type).get());
    }

    public static class PlaceholderSourcesBuilderImpl implements SourceBuilder {

        private final Map<TypeToken<?>, Supplier<?>> sources = Maps.newHashMap();

        @Override
        public <T> SourceBuilder append(Class<T> type, Supplier<T> supplier) {
            return this.append(TypeToken.get(type), supplier);
        }

        @Override
        public <T> SourceBuilder append(TypeToken<T> type, Supplier<T> supplier) {
            this.sources.put(type, supplier);
            return this;
        }

        @Override
        public SourceBuilder from(PlaceholderSources input) {
            this.sources.putAll(((PlaceholderSourcesImpl) input).sources);
            return this;
        }

        @Override
        public PlaceholderSources build() {
            return new PlaceholderSourcesImpl(this);
        }
    }
}
