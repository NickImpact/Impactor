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

import net.impactdev.impactor.api.logging.Log4jLogger;
import net.impactdev.impactor.core.plugin.BaseImpactorPlugin;
import net.impactdev.impactor.core.plugin.ImpactorBootstrapper;
import net.impactdev.impactor.minecraft.api.items.ServerProvider;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import org.apache.logging.log4j.LogManager;

@Mod("impactor")
public final class ForgeImpactorBootstrap extends ImpactorBootstrapper {

    public ForgeImpactorBootstrap(IEventBus bus) {
        super(new Log4jLogger(LogManager.getLogger("Impactor")));

        bus.addListener(this::onConstruct);
        bus.addListener(this::onClientSetup);
        bus.addListener(this::onDedicatedServerSetup);
        NeoForge.EVENT_BUS.addListener(this::onServerStarting);
        NeoForge.EVENT_BUS.addListener(this::onServerShutdown);
    }

    @Override
    protected BaseImpactorPlugin createPlugin() {
        return new ForgeImpactorPlugin(this);
    }

    public void onConstruct(FMLConstructModEvent event) {
        this.construct();
    }

    public void onDedicatedServerSetup(FMLDedicatedServerSetupEvent event) {
        this.setup();
    }

    public void onClientSetup(FMLClientSetupEvent event) {
        this.setup();
    }

    public void onServerStarting(ServerStartingEvent event) {
        ServerProvider.server = event.getServer();
    }

    public void onServerShutdown(ServerStoppingEvent event) {
        this.shutdown();
    }

}
