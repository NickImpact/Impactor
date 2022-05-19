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

import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ImpactorPluginLauncher {

    private static final CountDownLatch latch = new CountDownLatch(1);
    private static ImpactorPluginLauncher launcher;
    private final JarInJarClassLoader loader;

    public static void initialize(JarInJarClassLoader loader) {
        if(launcher != null) {
            throw new LoadingException("Plugin Launcher already initialized");
        }

        launcher = new ImpactorPluginLauncher(loader);
        latch.countDown();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static ImpactorPluginLauncher get() {
        try {
            latch.await(5, TimeUnit.SECONDS);
            return launcher;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private ImpactorPluginLauncher(JarInJarClassLoader loader) {
        this.loader = loader;
    }

    public LauncherBootstrap bootstrap(ClassLoader loader, LaunchablePlugin plugin) {
        URL url = JarInJarClassLoader.extractJar(loader, plugin.path());
        this.loader.addJarToClasspath(url);

        return plugin.create(this.loader);
    }

}
