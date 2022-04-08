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

package net.impactdev.impactor.bungee;

import net.impactdev.impactor.api.dependencies.classpath.ClassPathAppender;
import net.impactdev.impactor.api.logging.JavaLogger;
import net.impactdev.impactor.api.logging.PluginLogger;
import net.impactdev.impactor.api.scheduler.SchedulerAdapter;
import net.impactdev.impactor.api.utilities.printing.PrettyPrinter;
import net.impactdev.impactor.bungee.scheduler.BungeeSchedulerAdapter;
import net.impactdev.impactor.common.plugin.ImpactorBootstrap;
import net.impactdev.impactor.common.plugin.classpath.JarInJarClassPathAppender;
import net.impactdev.impactor.launcher.LauncherBootstrap;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.nio.file.Path;

public class BungeeImpactorBootstrap implements ImpactorBootstrap, LauncherBootstrap {

    private final Plugin loader;
    private final BungeeImpactorPlugin plugin;

    private final PluginLogger logger;
    private final BungeeSchedulerAdapter scheduler;
    private final ClassPathAppender appender;

    public BungeeImpactorBootstrap(Plugin loader) {
        this.loader = loader;
        this.plugin = new BungeeImpactorPlugin(this);
        this.logger = new JavaLogger(this.plugin, this.loader.getLogger());
        this.scheduler = new BungeeSchedulerAdapter(this);
        this.appender = new JarInJarClassPathAppender(this.getClass().getClassLoader());
    }

    @Override
    public void construct() {
        try {
            this.plugin.construct();
        } catch (Throwable e) {
            new PrettyPrinter(80)
                    .title("Impactor Encountered an Exception")
                    .add("Server Information").center().newline()
                    .add("Server Brand: " + this.serverBrand() + " (Version: " + this.serverVersion() + ")")
                    .add("Impactor: " + this.version())
                    .hr()
                    .add("During launch, Impactor encountered an error which prevented it from")
                    .add("launching correctly!")
                    .newline()
                    .add("The tracked exception can be viewed below:")
                    .hr('-')
                    .add(e)
                    .log(this.logger(), PrettyPrinter.Level.ERROR);
        }
    }

    @Override
    public void shutdown() {}

    @Override
    public PluginLogger logger() {
        return this.logger;
    }

    @Override
    public Path configDirectory() {
        return this.loader.getDataFolder().toPath();
    }

    @Override
    public SchedulerAdapter scheduler() {
        return this.scheduler;
    }

    @Override
    public ClassPathAppender appender() {
        return this.appender;
    }

    @Override
    public String version() {
        return this.plugin.metadata().version();
    }

    @Override
    public String serverBrand() {
        return this.loader.getProxy().getName();
    }

    @Override
    public String serverVersion() {
        return this.loader.getProxy().getVersion();
    }

    public Plugin loader() {
        return this.loader;
    }

    public ProxyServer proxy() {
        return this.loader.getProxy();
    }
}
