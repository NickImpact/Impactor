package net.impactdev.impactor.api.gui;

import java.util.List;
import java.util.function.Consumer;

public interface Icon<T, L> {

	T getDisplay();

	void addListener(L listener);

	List<L> getListeners();

}
