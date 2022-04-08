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

package net.impactdev.impactor.common.plugin;

import com.google.common.collect.Sets;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.dependencies.Dependency;
import net.impactdev.impactor.api.dependencies.DependencyManager;
import net.impactdev.impactor.api.dependencies.ProvidedDependencies;
import net.impactdev.impactor.api.dependencies.classpath.ClassPathAppender;
import net.impactdev.impactor.api.module.Module;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import net.impactdev.impactor.api.plugin.PluginMetadata;
import net.impactdev.impactor.api.plugin.registry.PluginRegistry;
import net.impactdev.impactor.api.registry.Registry;
import net.impactdev.impactor.common.api.ModuleImplementation;
import net.impactdev.impactor.common.dependencies.DependencyContainer;
import net.impactdev.impactor.launcher.LoadingException;
import org.reflections.Reflections;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public abstract class InternalImpactorPlugin implements ImpactorPlugin {

    PluginMetadata METADATA = PluginMetadata.builder()
            .id("impactor")
            .name("Impactor")
            .version("@version@")
            .description("Utility API for plugin management")
            .build();

    @Override
    public PluginMetadata metadata() {
        return METADATA;
    }

    protected void modules() {
        final Registry registry = Impactor.getInstance().getRegistry();

        new Reflections("net.impactdev.impactor")
                .getSubTypesOf(Module.class)
                .stream()
                .filter(module -> module.isAnnotationPresent(ModuleImplementation.class))
                .map(module -> {
                    try {
                        return (Module) module.newInstance();
                    } catch (Exception e) {
                        throw new LoadingException("Failed to initialize a module", e);
                    }
                })
                .sorted(Comparator.comparing(Module::priority).reversed())
                .forEach(module -> {
                    this.logger().info("Loading module: " + module.name());
                    module.builders(registry);
                    module.register(registry);
                });
    }

    protected void download() {
        Registry registry = Impactor.getInstance().getRegistry();
        registry.register(ClassPathAppender.class, this.bootstrapper().appender());
        registry.registerBuilderSupplier(Dependency.DependencyBuilder.class, DependencyContainer.DependencyContainerBuilder::new);

        DependencyManager manager = new DependencyManager(this);
        registry.register(DependencyManager.class, manager);

        this.logger().info("Attempting to load runtime dependencies...");
        this.logger().info("Initializing priority dependencies...");

        Instant start = Instant.now();
        manager.loadDependencies(ProvidedDependencies.JAR_RELOCATOR);

        this.logger().info("Pooling plugin dependencies...");
        Set<Dependency> dependencies = Sets.newHashSet();
        for(ImpactorPlugin plugin : PluginRegistry.getAll()) {
            dependencies.addAll(plugin.dependencies());
            dependencies.addAll(manager.registry().resolveStorageDependencies(plugin.storageRequirements()));
        }
        manager.loadDependencies(dependencies);

        Instant end = Instant.now();
        long ms = Duration.between(start, end).toMillis();
        this.logger().info("Dependency injection complete, took " + String.format("%02d.%03d seconds", TimeUnit.MILLISECONDS.toSeconds(ms), (ms % 1000)));
    }

    public abstract ImpactorBootstrap bootstrapper();

    protected abstract void listeners();

    protected abstract void commands();

    protected abstract void placeholders();

}
