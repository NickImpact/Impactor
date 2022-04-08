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

package net.impactdev.impactor.bungee.launcher;

import net.impactdev.impactor.launcher.ImpactorPluginLauncher;
import net.impactdev.impactor.launcher.JarInJarClassLoader;
import net.impactdev.impactor.launcher.LaunchablePlugin;
import net.impactdev.impactor.launcher.LauncherBootstrap;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeLauncher extends Plugin implements LaunchablePlugin {

    private static final String INTERNAL_JAR = "impactor-bungee.jarinjar";
    private static final String BOOTSTRAP_CLASS = "net.impactdev.impactor.bungee.BungeeImpactorBootstrap";

    private final LauncherBootstrap plugin;

    public BungeeLauncher() {
        JarInJarClassLoader loader = new JarInJarClassLoader(this.getClass().getClassLoader(), INTERNAL_JAR);
        this.plugin = this.create(loader);

        ImpactorPluginLauncher.initialize(loader);
    }

    @Override
    public String path() {
        return INTERNAL_JAR;
    }

    @Override
    public String bootstrapper() {
        return BOOTSTRAP_CLASS;
    }

    @Override
    public LauncherBootstrap create(JarInJarClassLoader loader) {
        return loader.instantiatePlugin(this.bootstrapper(), Plugin.class, this);
    }

    @Override
    public void onLoad() {
        this.plugin.construct();
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

}
