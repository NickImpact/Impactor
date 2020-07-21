package com.nickimpact.impactor.api.plugin;

import co.aikar.commands.BaseCommand;
import com.nickimpact.impactor.api.configuration.Config;
import com.nickimpact.impactor.api.logging.Logger;
import com.nickimpact.impactor.api.plugin.components.Reloadable;

import java.util.List;

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

	List<Config> getConfigs();

	List<BaseCommand> getCommands();

	List<Object> getListeners();


}
