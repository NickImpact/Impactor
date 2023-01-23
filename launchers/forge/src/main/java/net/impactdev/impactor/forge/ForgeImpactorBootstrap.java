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

import ca.landonjw.gooeylibs2.bootstrap.GooeyBootstrapper;
import ca.landonjw.gooeylibs2.forge.ForgeBootstrapper;
import net.impactdev.impactor.api.logging.Log4jLogger;
import net.impactdev.impactor.core.plugin.BaseImpactorPlugin;
import net.impactdev.impactor.core.plugin.ImpactorBootstrapper;
import net.impactdev.impactor.forge.commands.ForgeCommandManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;

@Mod("impactor")
public class ForgeImpactorBootstrap extends ImpactorBootstrapper {

    public ForgeImpactorBootstrap() {
        super(new Log4jLogger(LogManager.getLogger("Impactor")));

        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.HIGHEST, this::onConstruct);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.HIGHEST, this::onCommonSetup);
        MinecraftForge.EVENT_BUS.addListener(this::onServerShutdown);
        MinecraftForge.EVENT_BUS.register(new RegistryEvents());

        GooeyBootstrapper bootstrapper = new ForgeBootstrapper();
        bootstrapper.bootstrap();
    }

    @Override
    protected BaseImpactorPlugin createPlugin() {
        return new ForgeImpactorPlugin(this);
    }

    public void onConstruct(FMLConstructModEvent event) {
        this.construct();
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        this.setup();
    }

    public void onServerShutdown(FMLServerStoppingEvent event) {
        this.shutdown();
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {

        @SubscribeEvent
        public void onCommandRegistration(final RegisterCommandsEvent event) {
            ForgeCommandManager.activate(event);
        }

    }
}
