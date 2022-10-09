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

package net.impactdev.impactor.sponge;

import com.google.inject.Inject;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.commands.PermissionsService;
import net.impactdev.impactor.api.logging.Log4jLogger;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import net.impactdev.impactor.commands.permissions.LuckPermsPermissionsService;
import net.impactdev.impactor.commands.permissions.NoOpPermissionsService;
import net.impactdev.impactor.plugin.ImpactorBootstrapper;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.event.lifecycle.StartingEngineEvent;
import org.spongepowered.plugin.builtin.jvm.Plugin;

@Plugin("impactor")
public class SpongeImpactorBootstrap extends ImpactorBootstrapper {

    @Inject
    public SpongeImpactorBootstrap(Logger delegate) {
        super(new Log4jLogger(delegate));
    }

    @Override
    protected ImpactorPlugin createPlugin() {
        return new SpongeImpactorPlugin(this);
    }

    @Listener
    public void onConstruct(ConstructPluginEvent event) {
        this.construct();
    }

    @Listener
    public void onCommandRegistration(RegisterCommandEvent<Command.Raw> event) {

    }

    @Listener
    public void initializePermissionsService(StartingEngineEvent<Server> event) {
        boolean luckperms = Sponge.pluginManager().plugin("luckperms").isPresent();
        if(luckperms) {
            Impactor.instance().services().register(PermissionsService.class, new LuckPermsPermissionsService());
        } else {
            Impactor.instance().services().register(PermissionsService.class, new NoOpPermissionsService());
        }
    }

}
