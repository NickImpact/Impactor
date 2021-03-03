package net.impactdev.impactor.api.plugin.components;

import net.impactdev.impactor.api.configuration.Config;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;

import java.nio.file.Path;

public interface Configurable extends ImpactorPlugin {

	Path getConfigDir();

	Config getConfiguration();

}
