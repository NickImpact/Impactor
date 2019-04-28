package com.nickimpact.impactor.api.gui;

import com.google.common.collect.ImmutableMap;

import java.util.Optional;

public interface Layout<U extends Icon> {

	ImmutableMap<Integer, U> getElements();

	Optional<U> getIcon(int slot);

	InventoryDimensions getDimensions();
}
