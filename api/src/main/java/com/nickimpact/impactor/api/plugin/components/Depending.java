package com.nickimpact.impactor.api.plugin.components;

import com.nickimpact.impactor.api.plugin.ImpactorPlugin;
import com.nickimpact.impactor.api.storage.StorageType;
import com.nickimpact.impactor.api.storage.dependencies.DependencyManager;
import com.nickimpact.impactor.api.storage.dependencies.classloader.PluginClassLoader;

import java.util.List;

public interface Depending extends ImpactorPlugin {

    List<StorageType> getStorageRequirements();

}
