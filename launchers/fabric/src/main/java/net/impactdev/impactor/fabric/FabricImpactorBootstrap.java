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

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.impactdev.impactor.fabric.commands.FabricCommandManager;
import net.impactdev.impactor.api.logging.Log4jLogger;
import net.impactdev.impactor.core.plugin.BaseImpactorPlugin;
import net.impactdev.impactor.core.plugin.ImpactorBootstrapper;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;

import java.util.Optional;

public final class FabricImpactorBootstrap extends ImpactorBootstrapper implements ModInitializer {

    private static FabricImpactorBootstrap instance;
    private MinecraftServer server;

    public FabricImpactorBootstrap() {
        super(new Log4jLogger(LogManager.getLogger("Impactor")));
        instance = this;
    }

    public static FabricImpactorBootstrap instance() {
        return instance;
    }

    @Override
    protected BaseImpactorPlugin createPlugin() {
        return new FabricImpactorPlugin(this);
    }

    @Override
    public void onInitialize() {
        this.construct();
        FabricCommandManager.activate();
        ServerLifecycleEvents.SERVER_STARTING.register(this::onServerStarting);
    }

    private void onServerStarting(MinecraftServer server) {
        this.server = server;
        this.setup();
    }

    public Optional<MinecraftServer> server() {
        return Optional.ofNullable(this.server);
    }
}
