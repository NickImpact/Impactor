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

import net.impactdev.impactor.api.logging.PluginLogger;
import net.impactdev.impactor.launcher.dependencies.DependencyManager;
import net.impactdev.impactor.launcher.dependencies.provided.ProvidedDependencies;
import net.impactdev.impactor.launcher.dependencies.runtime.appenders.JarInJarClassPathAppender;
import net.impactdev.impactor.launcher.loader.JarInJarClassLoader;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public abstract class AbstractLauncher implements PluginLauncher {

    private final PluginLogger logger;
    private final String jar;
    private final String bootstrapper;

    protected AbstractLauncher(String jar, String bootstrapper, PluginLogger logger) {
        this.jar = jar;
        this.bootstrapper = bootstrapper;
        this.logger = logger;
    }

    @Override
    public String path() {
        return this.jar;
    }

    @Override
    public String bootstrapper() {
        return this.bootstrapper;
    }

    protected void download(JarInJarClassLoader loader) {
        DependencyManager manager = new DependencyManager(this.logger, new JarInJarClassPathAppender(loader));
        this.repositories().forEach(manager::repository);

        this.logger.info("Downloading dependencies for " + this.name());

        Instant start = Instant.now();
        manager.loadDependencies(ProvidedDependencies.JAR_RELOCATOR);
        manager.loadDependencies(this.dependencies());
        Instant end = Instant.now();

        long duration = Duration.between(start, end).toMillis();
        this.logger.info("Dependency injection complete, took " + String.format("%02d.%03d seconds", TimeUnit.MILLISECONDS.toSeconds(duration), (duration % 1000)));
    }

}
