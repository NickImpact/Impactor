package com.nickimpact.impactor.api.gui;

import java.util.function.Consumer;

public interface Icon<T, U, V> {

	T getDisplay();

	void addListener(Consumer<Clickable<U, V>> listener);

	void process(Clickable<U, V> clickable);

}
