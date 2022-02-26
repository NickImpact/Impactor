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

import net.impactdev.impactor.api.logging.Logger;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import net.impactdev.impactor.api.plugin.PluginMetadata;
import net.impactdev.impactor.forge.logging.ForgeLogger;

public class ForgeImpactorPlugin implements ImpactorPlugin {

    private static ForgeImpactorPlugin instance;

    private final ForgeBootstrap bootstrap;
    private final Logger logger;
    private final PluginMetadata metadata = PluginMetadata.builder()
            .id("impactor")
            .name("Impactor")
            .version("@version@")
            .description("Cross Platform API ")
            .build();

    public ForgeImpactorPlugin(ForgeBootstrap bootstrap, org.apache.logging.log4j.Logger delegate) {
        instance = this;
        this.bootstrap = bootstrap;
        this.logger = new ForgeLogger(delegate);
    }

    public static ForgeImpactorPlugin getInstance() {
        return instance;
    }

    public ForgeBootstrap getBootstrap() {
        return this.bootstrap;
    }

    @Override
    public PluginMetadata getMetadata() {
        return this.metadata;
    }

    @Override
    public Logger getPluginLogger() {
        return this.logger;
    }
}
