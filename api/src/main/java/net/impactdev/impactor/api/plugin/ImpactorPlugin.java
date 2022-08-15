/*
 * This file is part of Impactor, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018-2022 NickImpact
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package net.impactdev.impactor.api.plugin;

import net.impactdev.impactor.api.configuration.Config;
import net.impactdev.impactor.api.logging.PluginLogger;
import net.impactdev.impactor.api.plugin.registry.PluginRegistry;
import net.impactdev.impactor.api.storage.StorageType;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

/**
 * Represents a basic plugin style that'll surround the basis to a plugin that can be deployed off
 * Impactor. Functionality of a plugin can be extended using the additional components available
 * within this containing package.
 * <p>
 * By default, a plugin implementing this interface is expected to provide a set of metadata that'll
 * be used to identify the plugin.
 */
public interface ImpactorPlugin {

	/**
	 * Specifies a set of metadata describing the plugin. This data contains information such
	 * as its actual ID, the display name, its current version, as well as a description of
	 * its purpose.
	 *
	 * @return Metadata describing the plugin
	 */
	PluginMetadata metadata();

	/**
	 *
	 * @return
	 */
	PluginLogger logger();

	void construct() throws Exception;

	void shutdown() throws Exception;

	default void register() {
		PluginRegistry.register(this);
	}

	/**
	 * Represents the path to the configuration directory of the plugin. By default,
	 * this returns an empty optional to symbolize a plugin without configuration.
	 *
	 * @return
	 */
	default Optional<Path> configDirectory() {
		return Optional.empty();
	}

	default Optional<Config> config() {
		return Optional.empty();
	}

	/**
	 * Represents a set of storage options a plugin might require for operation. These
	 * types are then passed to Impactor at time of dependency collection, and provides
	 * the necessary drivers for each storage type to the classpath.
	 *
	 * @return A set of storage types a plugin will require at runtime.
	 */
	default Set<StorageType> storageRequirements() {
		return Collections.emptySet();
	}

}
