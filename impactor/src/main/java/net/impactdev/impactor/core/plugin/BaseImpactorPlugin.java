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

import com.google.common.collect.Lists;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.logging.PluginLogger;
import net.impactdev.impactor.api.platform.PluginMetadata;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import net.impactdev.impactor.api.utility.ExceptionPrinter;
import net.impactdev.impactor.core.api.APIRegister;
import net.impactdev.impactor.core.api.ImpactorService;
import net.impactdev.impactor.core.configuration.ConfigModule;
import net.impactdev.impactor.core.economy.EconomyModule;
import net.impactdev.impactor.core.modules.ImpactorModule;
import net.impactdev.impactor.core.text.TextModule;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

public abstract class BaseImpactorPlugin implements ImpactorPlugin {

    private static ImpactorPlugin instance;

    private final ImpactorBootstrapper bootstrapper;
    private final PluginMetadata metadata = PluginMetadata.builder()
            .id("impactor")
            .name("Impactor")
            .version("@version@")
            .build();

    public BaseImpactorPlugin(ImpactorBootstrapper bootstrapper) {
        instance = this;
        this.bootstrapper = bootstrapper;
    }

    public static ImpactorPlugin instance() {
        return instance;
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
    public void construct() throws Exception {
        this.bootstrapper.logger().info("Initializing API...");
        Impactor service = new ImpactorService();
        APIRegister.register(service);

        this.bootstrapper.logger().info("Initializing plugin modules...");
        Set<Class<? extends ImpactorModule>> modules = new LinkedHashSet<>(Lists.newArrayList(
                ConfigModule.class,
                TextModule.class,
                EconomyModule.class
        ));

        modules.addAll(Optional.ofNullable(this.modules()).orElse(Collections.emptySet()));
        modules.stream().map(type -> {
            try {
                return type.getConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Failed to load class module", e);
            }
        }).peek(module -> {
            module.factories(service.factories());
            module.builders(service.builders());
            module.services(service.services());
            module.subscribe(service.events());
        }).forEach(module -> {
            try {
                module.init(service, this.logger());
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    protected abstract Set<Class<? extends ImpactorModule>> modules();

    @Override
    public void shutdown() throws Exception {}

    /**
     * For 1.16.5, this code works perfectly in a non-forge environment. However, with forge, there's
     * a discrepancy with the TransformingClassLoader which prevents this tool from finding
     * the class file assets. Per forums, this should be fixed with 1.17.1+ due to forge switching
     * to the java module system. This code will be reactivated then, but for now, we are stuck with a
     * manual solution.
     * <p>
     * This problem affects sponge and forge only (SpongeForge, SpongeVanilla, Forge).
     */
    private void initializeModules(Impactor service) {
        ClassGraph graph = new ClassGraph().verbose().acceptPackages("net.impactdev.impactor");
        try (ScanResult scan = graph.scan()) {
            ClassInfoList list = scan.getClassesImplementing(ImpactorModule.class);
            this.bootstrapper.logger().info("Scan complete, found " + list.size() + " modules, now loading...");
            list.stream()
                    .map(info -> info.loadClass(ImpactorModule.class))
                    .map(type -> {
                        try {
                            return type.getConstructor().newInstance();
                        }
                        catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .forEach(module -> {
                        module.factories(service.factories());
                        module.builders(service.builders());
                        module.services(service.services());
                    });
            this.bootstrapper.logger().info("Module loading complete!");
        } catch (Exception e) {
            ExceptionPrinter.print(this.logger(), e);
        }
    }
}
