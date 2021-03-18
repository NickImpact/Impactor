package net.impactdev.impactor.api.gui;

import net.impactdev.impactor.api.plugin.ImpactorPlugin;

public interface UI<V, U extends Icon<?, ?>> {

	ImpactorPlugin getPlugin();

	boolean open(V viewer);

	boolean close(V viewer);

	Layout<U> getLayout();

	UI<V, U> define(Layout<U> layout);

}
