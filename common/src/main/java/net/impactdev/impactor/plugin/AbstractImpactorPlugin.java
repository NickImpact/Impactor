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

package net.impactdev.impactor.plugin;

import net.impactdev.impactor.api.APIRegister;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.ImpactorService;
import net.impactdev.impactor.api.logging.PluginLogger;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import net.impactdev.impactor.api.plugin.PluginMetadata;
import net.impactdev.impactor.modules.ImpactorModule;
import net.impactdev.impactor.util.ExceptionPrinter;
import net.impactdev.impactor.util.ProvidedExceptionHeaders;
import org.reflections.Reflections;

public class AbstractImpactorPlugin implements ImpactorPlugin {

    private static ImpactorPlugin instance;

    private final ImpactorBootstrapper bootstrapper;
    private final PluginMetadata metadata = PluginMetadata.builder()
            .id("impactor")
            .name("Impactor")
            .version("@version@")
            .build();

    public AbstractImpactorPlugin(ImpactorBootstrapper bootstrapper) {
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

        new Reflections("net.impactdev.impactor")
                .getSubTypesOf(ImpactorModule.class)
                .forEach(m -> {
                    try {
                        ImpactorModule module = m.getDeclaredConstructor().newInstance();
                        module.factories(service.factories());
                        module.builders(service.builders());
                        module.services(service.services());
                    } catch (Exception e) {
                        ExceptionPrinter.print(this, e, ProvidedExceptionHeaders.METADATA);
                    }
                });
    }

    @Override
    public void shutdown() throws Exception {

    }
}
