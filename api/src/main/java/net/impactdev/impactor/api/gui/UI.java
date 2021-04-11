package net.impactdev.impactor.api.gui;

import net.impactdev.impactor.api.plugin.ImpactorPlugin;

public interface UI<V, I extends Icon<?, ?>> {

	ImpactorPlugin getPlugin();

	boolean open(V viewer);

	boolean close(V viewer);

	Layout<I> getLayout();

	UI<V, I> define(Layout<I> layout);

	void set(I icon, int slot);


}
