package com.nickimpact.impactor.api.gui;

import com.nickimpact.impactor.api.plugin.ImpactorPlugin;

import java.util.Optional;
import java.util.function.BiConsumer;

public interface UI<T, V, U extends Icon> {

	ImpactorPlugin getPlugin();

	UI<T, V, U> define(Layout<U> layout);

	Optional<U> getIcon(int slot);

	void setSlot(int slot, U icon);

	void open(T player);

	void close(T player);

	void clear();

	void clear(int... slots);

	UI attachListener(BiConsumer<T, V> listener);

	InventoryDimensions getDimension();

}
