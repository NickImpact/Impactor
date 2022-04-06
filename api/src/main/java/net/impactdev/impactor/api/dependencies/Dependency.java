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

package net.impactdev.impactor.api.dependencies;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.dependencies.relocation.Relocation;
import net.impactdev.impactor.api.utilities.Builder;

import java.util.*;

/**
 * A dependency is a runtime downloadable instance that Impactor can fetch and apply to the classpath
 * at runtime, without a user needing to download the tool. A dependency downloaded by Impactor will
 * be downloaded to the server root folder, under a folder tree of ./impactor/libs/. Once downloaded,
 * the dependency will then be injected into the classpath of the running server. Note that this method
 * will not work for plugins, as those are expected to be in the mods/plugins folders respectfully. This
 * system is meant as a means to replace shading, allowing for shaded content to exist outside the plugin
 * jar.
 *
 * Each dependency is expected to exist on a compatible maven repository.
 */
public interface Dependency {

    /**
     * Represents the name of the actual dependency. This is really a display name
     * for the dependency, and is used purely for reference in dependency loading logging.
     *
     * @return The display name of a dependency
     */
    String name();

    /**
     *
     *
     * @return
     */
    String group();

    String artifact();

    String version();

    boolean hashed();

    Optional<byte[]> checksum();

    Set<Relocation> relocations();

    String getFileName();

    String getMavenPath();

    boolean snapshot();

    /**
     * Represents an additional set of dependencies that are bundled with this dependency.
     * For instance, the Adventure API requires the Examination library, but that library must
     * be downloaded separately. That dependency can then indicate that additional dependencies should
     * be downloaded for it.
     *
     * @return A unique set of additional dependencies that should be bundled with this dependency.
     */
    Set<Dependency> bundled();

    static DependencyBuilder builder() {
        return Impactor.getInstance().getRegistry().createBuilder(DependencyBuilder.class);
    }

    interface DependencyBuilder extends Builder<Dependency, DependencyBuilder> {

        DependencyBuilder name(String name);

        DependencyBuilder group(String group);

        DependencyBuilder artifact(String artifact);

        DependencyBuilder version(String version);

        DependencyBuilder checksum(String checksum);

        DependencyBuilder relocation(Relocation relocation);

        DependencyBuilder relocations(Relocation... relocations);

        DependencyBuilder with(Dependency dependency);

        DependencyBuilder with(Dependency... dependencies);
    }

}
