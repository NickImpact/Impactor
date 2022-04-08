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

package net.impactdev.impactor.forge;

import net.impactdev.impactor.api.dependencies.classpath.ClassPathAppender;
import net.impactdev.impactor.api.logging.Log4jLogger;
import net.impactdev.impactor.api.logging.PluginLogger;
import net.impactdev.impactor.api.scheduler.SchedulerAdapter;
import net.impactdev.impactor.api.utilities.printing.PrettyPrinter;
import net.impactdev.impactor.common.plugin.ImpactorBootstrap;
import net.impactdev.impactor.common.plugin.classpath.JarInJarClassPathAppender;
import net.impactdev.impactor.launcher.LauncherBootstrap;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLCommonLaunchHandler;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.versions.forge.ForgeVersion;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

@Mod("impactor")
public class ForgeImpactorBootstrap implements ImpactorBootstrap, LauncherBootstrap {

    private final ForgeImpactorPlugin plugin;

    private final PluginLogger logger;
    private final SchedulerAdapter scheduler;
    private final ClassPathAppender appender;

    public ForgeImpactorBootstrap(Logger logger) {
        this.logger = new Log4jLogger(logger);
        this.scheduler = null;
        this.appender = new JarInJarClassPathAppender(this.getClass().getClassLoader());
        this.plugin = new ForgeImpactorPlugin(this);
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
        return null;
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
        return "Forge Mod Loader";
    }

    @Override
    public String serverVersion() {
        return ForgeVersion.getVersion();
    }
}
