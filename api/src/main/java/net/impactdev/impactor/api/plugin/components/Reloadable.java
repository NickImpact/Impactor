package net.impactdev.impactor.api.plugin.components;

import net.impactdev.impactor.api.plugin.ImpactorPlugin;

import java.util.function.Consumer;

public interface Reloadable extends ImpactorPlugin {

    Consumer<ImpactorPlugin> onReload();

}
