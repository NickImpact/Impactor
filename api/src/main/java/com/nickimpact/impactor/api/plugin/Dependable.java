package com.nickimpact.impactor.api.plugin;

import com.nickimpact.impactor.api.storage.StorageType;
import com.nickimpact.impactor.api.storage.dependencies.DependencyManager;
import com.nickimpact.impactor.api.storage.dependencies.classloader.PluginClassLoader;

import java.util.List;

public interface Dependable extends ImpactorPlugin {

	PluginClassLoader getPluginClassLoader();

	DependencyManager getDependencyManager();

	List<StorageType> getStorageTypes();

}
