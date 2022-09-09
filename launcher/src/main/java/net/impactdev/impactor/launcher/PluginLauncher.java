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

package net.impactdev.impactor.launcher;

import net.impactdev.impactor.launcher.dependencies.Dependency;
import net.impactdev.impactor.launcher.dependencies.repositories.DependencyRepository;
import net.impactdev.impactor.launcher.loader.JarInJarClassLoader;

import java.util.Set;

public interface PluginLauncher {

    String name();

    String path();

    String bootstrapper();

    LauncherBootstrap create(JarInJarClassLoader loader);

    /**
     * Details a set of required dependencies by a plugin when being constructed. These are provided
     * during the construction phase to ensure they are available for all phases of the plugin lifecycle.
     *
     * @return A set of unique dependencies
     */
    Set<Dependency> dependencies();

    /**
     * A specification for additional repositories that might be required for downloading assets
     * from maven-based repositories.
     *
     * @return A set of unique repositories
     */
    Set<DependencyRepository> repositories();

}
