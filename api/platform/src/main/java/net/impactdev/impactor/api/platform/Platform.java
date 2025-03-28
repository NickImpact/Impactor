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

import net.impactdev.impactor.api.platform.details.JavaVersion;
import net.impactdev.impactor.api.platform.details.Loader;
import net.impactdev.impactor.api.platform.metrics.PerformanceService;
import net.impactdev.impactor.loader.logging.PrettyPrinter;
import net.impactdev.impactor.loader.modules.ModuleMetadata;
import org.intellij.lang.annotations.Pattern;

import java.util.Optional;
import java.util.Set;

public interface Platform extends PrettyPrinter.IPrettyPrintable {

    /**
     * Represents an element detailing the Java environment hosting this instance of
     * Impactor. This will include things such as the version of Java as well as the
     * vendor of the particular JDK/JRE in use.
     *
     * @return An element describing the version of Java in use by the platform
     */
    JavaVersion java();

    /**
     * Represents an attribute of the platform, denoting the loader which was responsible
     * for launching Impactor. For instance, if launched by Fabric, the attribute would
     * not the particular API version of Fabric.
     *
     * @return The loader which launched Impactor
     * @since 6.0.0
     */
    Loader loader();

    /**
     * Provides the services responsible for monitoring performance of the platform. This will
     * include details such as memory usage, server tick times, and much more.
     *
     * @return The service responsible for monitoring performance of the platform
     * @since 6.0.0
     */
    PerformanceService performance();

    /**
     * Returns a set of {@link ModuleMetadata} describing mods running on the platform.
     *
     *
     * @return
     */
    Set<ModuleMetadata> mods();

    /**
     * Attempts to locate a registered mod using the given mod ID. The ID is expected to
     * follow the traditional rules for mod IDs.
     *
     * @param id The mod ID to query
     * @return An optionally wrapped set of metadata describing a mod if found, empty otherwise
     */
    Optional<ModuleMetadata> mod(@Pattern("[a-z][a-z0-9_-]{0,63}") String id);

    @Override
    default void print(PrettyPrinter printer) {
        printer.title("Platform Details");
        printer.add("Loader: %s", this.loader().type().displayName());
        printer.add("Version: %s", this.loader().version());
        this.loader().minecraft().ifPresent(version -> printer.add("Minecraft: %s (Protocol: %#X / Snapshot: %b)", version.version(), version.protocol(), version.isSnapshot()));

        printer.hr('-');
        printer.add("Metrics:");


        printer.hr('-');
        printer.add("Mod Tree");

        // TODO
    }
}
