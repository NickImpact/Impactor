package net.impactdev.impactor.api.utilities;

/**
 * A builder follows the concepts of the typical Builder design.
 *
 * @param <T> The output type of this builder
 * @param <B> The builder itself
 */
public interface Builder<T, B> {

    B from(T input);

    T build();

}
