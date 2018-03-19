package com.nickimpact.impactor.api.plugins;

import com.nickimpact.impactor.api.configuration.ConfigBase;

import java.nio.file.Path;

/**
 * Represents a plugin which will be configurable by the user of the plugin
 *
 * @author NickImpact
 */
public interface Configurable {

	Path getConfigDir();

	ConfigBase getConfig();
}
