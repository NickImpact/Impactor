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
 */
public interface ImpactorPlugin {

	PluginMetadata getMetadata();

	Logger getPluginLogger();

	default boolean inDebugMode() {
		return false;
	}

}
