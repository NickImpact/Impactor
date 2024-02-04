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

package net.impactdev.impactor.core.plugin;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.scheduler.AbstractJavaScheduler;
import net.impactdev.impactor.core.commands.CommandsModule;
import net.impactdev.impactor.core.commands.ImpactorCommandRegistry;
import net.impactdev.impactor.core.configuration.ConfigModule;
import net.impactdev.impactor.core.configuration.ImpactorConfig;
import net.impactdev.impactor.api.logging.PluginLogger;
import net.impactdev.impactor.api.platform.Platform;
import net.impactdev.impactor.api.platform.plugins.PluginMetadata;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import net.impactdev.impactor.api.plugin.components.Configurable;
import net.impactdev.impactor.api.services.permissions.PermissionsService;
import net.impactdev.impactor.api.utility.ExceptionPrinter;
import net.impactdev.impactor.core.api.APIRegister;
import net.impactdev.impactor.core.api.ImpactorService;
import net.impactdev.impactor.core.mail.MailModule;
import net.impactdev.impactor.core.modules.ModuleInitializer;
import net.impactdev.impactor.core.permissions.LuckPermsPermissionsService;
import net.impactdev.impactor.core.permissions.NoOpPermissionsService;
import net.impactdev.impactor.core.economy.EconomyModule;
import net.impactdev.impactor.core.permissions.PermissionsModule;
import net.impactdev.impactor.core.text.TextModule;
import net.impactdev.impactor.core.translations.TranslationsModule;
import net.impactdev.impactor.core.utility.future.Futures;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Function;

public abstract class BaseImpactorPlugin implements ImpactorPlugin, Configurable {

    private static ImpactorPlugin instance;

    private final ImpactorBootstrapper bootstrapper;
    private final PluginMetadata metadata = PluginMetadata.builder()
            .id("impactor")
            .name("Impactor")
            .version("@version@")
            .build();

    @MonotonicNonNull
    private ModuleInitializer initializer;

    public BaseImpactorPlugin(ImpactorBootstrapper bootstrapper) {
        instance = this;
        this.bootstrapper = bootstrapper;
    }

    public static BaseImpactorPlugin instance() {
        return (BaseImpactorPlugin) instance;
    }

    @Override
    public PluginMetadata metadata() {
        return this.metadata;
    }

    @Override
    public PluginLogger logger() {
        return this.bootstrapper.logger();
    }

    @Override
    public Path configurationDirectory() {
        return Paths.get("config").resolve("impactor");
    }

    @Override
    public ImpactorConfig configuration() {
        return null;
    }

    protected ModuleInitializer registerModules() {
        ModuleInitializer initializer = new ModuleInitializer();
        return initializer.with(ConfigModule.class)
                .with(CommandsModule.class)
                .with(EconomyModule.class)
                .with(MailModule.class)
                .with(PermissionsModule.class)
                .with(TextModule.class)
                .with(TranslationsModule.class);
    }

    @Override
    public void construct() {
        this.bootstrapper.logger().info("Initializing API...");
        Impactor service = new ImpactorService();
        APIRegister.register(service);

        this.bootstrapper.logger().info("Registering modules...");
        this.initializer = this.registerModules();
        try {
            this.initializer.construct(service);
        } catch (Exception e) {
            ExceptionPrinter.print(this.logger(), e);
        }
    }

    public void setup() {
        this.bootstrapper.logger().info("Initializing modules...");

        Impactor service = Impactor.instance();
        try {
            this.initializer.initialize(service, this.logger());
        } catch (Exception e) {
            ExceptionPrinter.print(this.logger(), e);
        }
    }

    @Override
    public void starting() {

    }

    @Override
    public void started() {

    }

    @Override
    public void shutdown() {
        this.logger().info("Shutting down Impactor scheduler");
        AbstractJavaScheduler scheduler = (AbstractJavaScheduler) Impactor.instance().scheduler();
        scheduler.shutdownExecutor();
        scheduler.shutdownScheduler();

        Futures.shutdown();

        this.logger().info("Scheduler shutdown successfully!");
    }

    public InputStream resource(Function<Path, Path> target) {
        Path path = target.apply(Paths.get("impactor").resolve("assets"));
        return Optional.ofNullable(this.getClass().getClassLoader().getResourceAsStream(path.toString().replace("\\", "/")))
                .orElseThrow(() -> new IllegalArgumentException("Target resource not located"));
    }
}
