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

package net.impactdev.impactor.fabric;

import ca.landonjw.gooeylibs2.bootstrap.GooeyBootstrapper;
import ca.landonjw.gooeylibs2.fabric.FabricBootstrapper;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.impactdev.impactor.api.logging.Log4jLogger;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import net.impactdev.impactor.plugin.ImpactorBootstrapper;
import org.apache.logging.log4j.LogManager;

public class FabricImpactorBootstrap extends ImpactorBootstrapper implements DedicatedServerModInitializer {

    public FabricImpactorBootstrap() {
        super(new Log4jLogger(LogManager.getLogger("Impactor")));

        GooeyBootstrapper bootstrapper = new FabricBootstrapper();
        bootstrapper.bootstrap();
    }

    @Override
    protected ImpactorPlugin createPlugin() {
        return new FabricImpactorPlugin(this);
    }

    @Override
    public void onInitializeServer() {
        this.construct();
    }
}
