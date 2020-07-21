package com.nickimpact.impactor.api.plugin.components;

import com.nickimpact.impactor.api.plugin.ImpactorPlugin;

import java.util.function.Consumer;

public interface Reloadable extends ImpactorPlugin {

    Consumer<ImpactorPlugin> onReload();

}
