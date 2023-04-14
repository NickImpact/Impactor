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
import com.google.common.collect.Sets;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import net.impactdev.impactor.api.Impactor;
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
import net.impactdev.impactor.core.permissions.LuckPermsPermissionsService;
import net.impactdev.impactor.core.permissions.NoOpPermissionsService;
import net.impactdev.impactor.core.economy.EconomyModule;
import net.impactdev.impactor.core.modules.ImpactorModule;
import net.impactdev.impactor.core.text.TextModule;
import net.impactdev.impactor.core.translations.TranslationsModule;
import net.impactdev.impactor.core.translations.internal.ImpactorTranslations;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class BaseImpactorPlugin implements ImpactorPlugin, Configurable {

    private static ImpactorPlugin instance;

    private final ImpactorBootstrapper bootstrapper;
    private final PluginMetadata metadata = PluginMetadata.builder()
            .id("impactor")
            .name("Impactor")
            .version("@version@")
            .build();

    private final Set<ImpactorModule> modules = Sets.newHashSet();


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
        return Paths.get("impactor");
    }

    @Override
    public ImpactorConfig configuration() {
        return null;
    }

    @Override
    public void construct() {
        this.bootstrapper.logger().info("Initializing API...");
        Impactor service = new ImpactorService();
        APIRegister.register(service);

        this.bootstrapper.logger().info("Registering modules...");
        Set<Class<? extends ImpactorModule>> modules = new LinkedHashSet<>(Lists.newArrayList(
                ConfigModule.class,
                EconomyModule.class,
                TextModule.class,
                TranslationsModule.class
        ));

        modules.addAll(Optional.ofNullable(this.modules()).orElse(Collections.emptySet()));
        Set<ImpactorModule> collection = modules.stream().map(type -> {
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
        }).collect(Collectors.toSet());

        Platform platform = Impactor.instance().platform();
        if(platform.info().plugin("luckperms").isPresent()) {
            service.services().register(PermissionsService.class, new LuckPermsPermissionsService());
        } else {
            service.services().register(PermissionsService.class, new NoOpPermissionsService());
        }

        this.modules.addAll(collection);

        this.logger().info("Registering commands...");
        ImpactorCommandRegistry registry = new ImpactorCommandRegistry();
        registry.registerArgumentParsers();
        registry.registerAllCommands();
        this.registerCommandMappings(registry);

        this.logger().info("Initializing translations...");
        ImpactorTranslations.MANAGER.initialize();
        ImpactorTranslations.MANAGER.refresh();
    }

    protected abstract void registerCommandMappings(ImpactorCommandRegistry registry);

    public void setup() {
        this.bootstrapper.logger().info("Initializing modules...");

        Impactor service = Impactor.instance();
        this.modules.forEach(module -> {
            try {
                module.init(service, this.logger());
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void starting() {

    }

    @Override
    public void started() {

    }

    @Override
    public void shutdown() {

    }

    protected abstract Set<Class<? extends ImpactorModule>> modules();

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

    public InputStream resource(Function<Path, Path> target) {
        Path path = target.apply(Paths.get("impactor").resolve("assets"));
        return Optional.ofNullable(this.getClass().getClassLoader().getResourceAsStream(path.toString().replace("\\", "/")))
                .orElseThrow(() -> new IllegalArgumentException("Target resource not located"));
    }
}
