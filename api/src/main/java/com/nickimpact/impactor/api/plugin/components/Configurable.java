package com.nickimpact.impactor.api.plugin.components;

import com.nickimpact.impactor.api.configuration.Config;
import com.nickimpact.impactor.api.plugin.ImpactorPlugin;

import java.nio.file.Path;
import java.util.Optional;

public interface Configurable extends ImpactorPlugin {

	Path getConfigDir();

	Config getConfiguration();

}
