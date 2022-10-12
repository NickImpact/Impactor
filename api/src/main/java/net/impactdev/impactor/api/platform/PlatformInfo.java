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

package net.impactdev.impactor.api.platform;

import net.impactdev.impactor.api.plugin.PluginMetadata;
import net.impactdev.impactor.api.utilities.printing.PrettyPrinter;
import org.intellij.lang.annotations.Pattern;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PlatformInfo extends PrettyPrinter.IPrettyPrintable {

    /**
     * Specifies the type of platform we are working against. This is based on the priority
     * server software in play. For instance, even if you are using the forge implementation
     * of Impactor, Impactor will specify Sponge if SpongeForge is found within the environment.
     *
     * @return The implementation defined platform type
     */
    PlatformType type();

    /**
     * Specifies components regarding the environment. Typically, this contains data like
     * the minecraft version, as well as the server software versions.
     *
     * @return A set of components useful for detailing the environment the plugin is active in
     */
    Set<PlatformComponent> components();

    /**
     * Fetches a list of mods/plugins in active use on the platform. This returns a mod's metadata,
     * such as the plugin name, version, license, and more.
     *
     * @return A list of metadata detailing all active mods/plugins
     */
    List<PluginMetadata> plugins();

    /**
     * Attempts to locate a mod/plugin with the given ID from the platform environment. If
     * the target is present, a valid metadata will be returned for the mod/plugin. Note
     * that per specification, mod/plugin IDs are meant to be all lowercase, using only
     * alphanumeric characters, with additional support for dashes and underscores. Additionally,
     * the ID must start with an alphanumeric character.
     *
     * @param id The ID of the mod/plugin
     * @return Metadata representing the target, or empty if not found.
     */
    Optional<PluginMetadata> plugin(@Pattern("[a-z][a-z0-9_-]{0,63}") String id);

}
