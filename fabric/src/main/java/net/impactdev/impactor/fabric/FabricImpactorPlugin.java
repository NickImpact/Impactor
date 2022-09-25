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

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.utilities.printing.PrettyPrinter;
import net.impactdev.impactor.fabric.platform.FabricPlatformModule;
import net.impactdev.impactor.fabric.scheduler.FabricSchedulerModule;
import net.impactdev.impactor.fabric.ui.FabricUIModule;
import net.impactdev.impactor.game.commands.event.ImpactorCommandRegistrationEvent;
import net.impactdev.impactor.game.commands.registration.CommandManager;
import net.impactdev.impactor.game.plugin.GameImpactorPlugin;
import net.impactdev.impactor.modules.ImpactorModule;
import net.impactdev.impactor.plugin.ImpactorBootstrapper;
import net.kyori.event.PostResult;
import net.minecraft.server.MinecraftServer;

import java.util.Optional;
import java.util.Set;

public class FabricImpactorPlugin extends GameImpactorPlugin {

    private MinecraftServer server;

    public FabricImpactorPlugin(ImpactorBootstrapper bootstrapper) {
        super(bootstrapper);
    }

    @Override
    public void construct() throws Exception {
        super.construct();

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            CommandManager manager = new CommandManager();
            PostResult result = Impactor.instance().events().post(new ImpactorCommandRegistrationEvent(manager));
            if(result.wasSuccessful()) {
                manager.registerWithBrigadier(dispatcher);
            }
        });

        ServerLifecycleEvents.SERVER_STARTED.register(server -> this.server = server);

        PrettyPrinter printer = new PrettyPrinter(80);
        printer.title("Platform Information");
        Impactor.instance().platform().info().print(printer);
        printer.log(this.logger(), PrettyPrinter.Level.INFO);
    }

    @Override
    protected Set<Class<? extends ImpactorModule>> modules() {
        Set<Class<? extends ImpactorModule>> parent = super.modules();
        parent.add(FabricPlatformModule.class);
        parent.add(FabricSchedulerModule.class);
        parent.add(FabricUIModule.class);

        return parent;
    }

    public Optional<MinecraftServer> server() {
        return Optional.ofNullable(this.server);
    }

}
