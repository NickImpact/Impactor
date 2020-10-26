package net.impactdev.impactor.api.gui;

import net.impactdev.impactor.api.plugin.ImpactorPlugin;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface UI<T, V, E, U extends Icon> {

	ImpactorPlugin getPlugin();

	Layout<U> getLayout();

	UI<T, V, E, U> define(Layout<U> layout);

	Optional<U> getIcon(int slot);

	void setSlot(int slot, U icon);

	void open(T player);

	void close(T player);

	void clear();

	void clear(int... slots);

	UI attachListener(BiConsumer<T, V> listener);

	UI attachCloseListener(Consumer<E> listener);

	InventoryDimensions getDimension();

}
