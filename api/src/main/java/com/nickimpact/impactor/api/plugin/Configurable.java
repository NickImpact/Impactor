package com.nickimpact.impactor.api.plugin;

import com.nickimpact.impactor.api.configuration.Config;

import java.nio.file.Path;

public interface Configurable {

	Path getConfigDir();

	Config getConfiguration();

}
