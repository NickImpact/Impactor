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
import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import net.impactdev.impactor.forge.bootstrap.ForgeRegistrationHandler;
import net.impactdev.impactor.launcher.LauncherBootstrap;
import net.impactdev.impactor.plugin.ImpactorBootstrapper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;

import java.util.function.Supplier;

public class ForgeImpactorBootstrap extends ImpactorBootstrapper implements LauncherBootstrap {

    private final Supplier<ModContainer> launcher;

    public ForgeImpactorBootstrap(Supplier<ModContainer> launcher) {
        super(new Log4jLogger(LogManager.getLogger("Impactor")));
        this.launcher = launcher;

        MinecraftForge.EVENT_BUS.addListener(ForgeRegistrationHandler::onCommandRegistration);
    }

    @Override
    protected ImpactorPlugin createPlugin() {
        return new ForgeImpactorPlugin(this);
    }

    @Override
    public void shutdown() {

    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {

        @SubscribeEvent
        public void onCommandRegistration(final RegisterCommandsEvent event) {
            ForgeRegistrationHandler.onCommandRegistration(event);
        }

    }
}
