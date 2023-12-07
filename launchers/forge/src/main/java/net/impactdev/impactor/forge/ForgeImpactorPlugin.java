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

import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import net.impactdev.impactor.api.scoreboards.display.resolvers.subscribing.Subscriber;
import net.impactdev.impactor.core.modules.ImpactorModule;
import net.impactdev.impactor.core.modules.ModuleInitializer;
import net.impactdev.impactor.core.plugin.ImpactorBootstrapper;
import net.impactdev.impactor.forge.commands.ForgeCommandModule;
import net.impactdev.impactor.forge.platform.ForgePlatformModule;
import net.impactdev.impactor.forge.scheduler.ForgeSchedulerModule;
import net.impactdev.impactor.forge.ui.ForgeUIModule;
import net.impactdev.impactor.minecraft.plugin.GameImpactorPlugin;
import net.kyori.event.EventSubscription;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

import java.util.Set;
import java.util.function.Consumer;

public class ForgeImpactorPlugin extends GameImpactorPlugin implements ImpactorPlugin {

    public ForgeImpactorPlugin(ImpactorBootstrapper bootstrapper) {
        super(bootstrapper);
    }

    @Override
    public void construct() {
        super.construct();
    }

    @Override
    protected ModuleInitializer registerModules() {
        return super.registerModules()
                .with(ForgeSchedulerModule.class)
                .with(ForgeUIModule.class)
                .with(ForgePlatformModule.class)
                .with(ForgeCommandModule.class);
    }

    public <T extends Event> void test() {
        Subscriber<T> subscriber = new Subscriber<>() {
            @Override
            public void validateEventType(Class<T> event) throws IllegalArgumentException {

            }

            @Override
            public EventSubscription subscribe(Class<T> event, Consumer<T> listener) {
                MinecraftForge.EVENT_BUS.addListener(listener);
                return () -> MinecraftForge.EVENT_BUS.unregister(listener);
            }
        } ;
    }

}
