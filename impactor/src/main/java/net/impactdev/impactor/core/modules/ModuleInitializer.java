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

package net.impactdev.impactor.core.modules;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.logging.PluginLogger;
import net.impactdev.impactor.api.utility.ExceptionPrinter;

import java.util.LinkedList;
import java.util.Queue;

public final class ModuleInitializer {

    public Queue<Class<? extends ImpactorModule>> definitions = new LinkedList<>();
    public Queue<ImpactorModule> modules = new LinkedList<>();

    public ModuleInitializer with(Class<? extends ImpactorModule> module) {
        this.definitions.add(module);
        return this;
    }

    public void construct(Impactor service) throws Exception {
        while(!this.definitions.isEmpty()) {
            Class<? extends ImpactorModule> type = this.definitions.poll();
            ImpactorModule module = type.getConstructor().newInstance();

            module.factories(service.factories());
            module.builders(service.builders());
            module.services(service.services());
            module.subscribe(service.events());

            this.modules.add(module);
        }
    }

    public void initialize(Impactor service, PluginLogger logger) throws Exception {
        while(!this.modules.isEmpty()) {
            ImpactorModule module = this.modules.poll();
            module.init(service, logger);
        }
    }

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
        ClassGraph graph = new ClassGraph()
                .acceptPackages("net.impactdev.impactor")
                .overrideClassLoaders(this.getClass().getClassLoader());

        try (ScanResult scan = graph.scan()) {
            ClassInfoList list = scan.getClassesImplementing(ImpactorModule.class);
            //this.bootstrapper.logger().info("Scan complete, found " + list.size() + " modules, now loading...");
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
            //this.bootstrapper.logger().info("Module loading complete!");
        } catch (Exception e) {
            //ExceptionPrinter.print(this.logger(), e);
        }
    }
}
