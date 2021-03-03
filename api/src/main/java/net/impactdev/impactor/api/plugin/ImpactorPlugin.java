package net.impactdev.impactor.api.plugin;

import net.impactdev.impactor.api.logging.Logger;
import net.impactdev.impactor.api.plugin.components.Reloadable;

/**
 * Represents a basic plugin style that'll surround the basis to a plugin that can be deployed off
 * Impactor. Functionality of a plugin can be extended using the additional components available
 * within this containing package.
 *
 * By default, a plugin implementing this interface is expected to provide a set of metadata that'll
 * be used to identify the plugin. Additionally, a plugin is expected to provide the logger it is using,
 * as well as any configs, commands, and listeners it has registered such that they can be reloaded
 * should the plugin implement the {@link Reloadable Reloadable} component.
 *
 *
 */
public interface ImpactorPlugin {

	PluginMetadata getMetadata();

	Logger getPluginLogger();

	default boolean inDebugMode() {
		return false;
	}

}
