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

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import net.impactdev.impactor.api.utilities.printing.PrettyPrinter;
import net.impactdev.impactor.plugin.BaseImpactorPlugin;
import net.impactdev.impactor.plugin.ImpactorBootstrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Optional;

public class ForgeImpactorPlugin extends BaseImpactorPlugin implements ImpactorPlugin {

    private MinecraftServer server;

    public ForgeImpactorPlugin(ImpactorBootstrapper bootstrapper) {
        super(bootstrapper);
        MinecraftForge.EVENT_BUS.addListener(this::onServerAboutToStart);
    }

    @Override
    public void construct() throws Exception {
        super.construct();

        PrettyPrinter printer = new PrettyPrinter(80);
        printer.title("Platform Information");
        Impactor.instance().platform().info().print(printer);
        printer.log(this.logger(), PrettyPrinter.Level.INFO);
    }

    @Override
    public void shutdown() throws Exception {

    }

    public Optional<MinecraftServer> server() {
        return Optional.ofNullable(this.server);
    }

    private void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        this.server = event.getServer();
    }

}
