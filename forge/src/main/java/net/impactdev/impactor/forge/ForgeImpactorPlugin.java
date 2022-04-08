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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.dependencies.Dependency;
import net.impactdev.impactor.api.dependencies.ProvidedDependencies;
import net.impactdev.impactor.api.dependencies.relocation.Relocation;
import net.impactdev.impactor.api.logging.PluginLogger;
import net.impactdev.impactor.api.module.Module;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import net.impactdev.impactor.api.registry.Registry;
import net.impactdev.impactor.common.api.ApiRegistrationUtil;
import net.impactdev.impactor.common.config.ImpactorConfigModule;
import net.impactdev.impactor.common.event.ImpactorEventBusModule;
import net.impactdev.impactor.common.plugin.ImpactorBootstrap;
import net.impactdev.impactor.common.plugin.InternalImpactorPlugin;
import net.impactdev.impactor.forge.api.ForgeImpactorAPIProvider;
import net.impactdev.impactor.forge.commands.CommandProvider;
import net.impactdev.impactor.forge.ui.ForgeUIModule;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;
import java.util.Set;

public class ForgeImpactorPlugin extends InternalImpactorPlugin {

    private static ForgeImpactorPlugin instance;

    private final ForgeImpactorBootstrap bootstrap;

    public ForgeImpactorPlugin(ForgeImpactorBootstrap bootstrap) {
        instance = this;
        this.bootstrap = bootstrap;
        ApiRegistrationUtil.register(new ForgeImpactorAPIProvider());
        this.register();
    }

    public static ForgeImpactorPlugin instance() {
        return instance;
    }

    @Override
    public PluginLogger logger() {
        return this.bootstrap.logger();
    }

    @Override
    public void construct() {
        instance = this;

        Registry registry = Impactor.getInstance().getRegistry();
        registry.register(InternalImpactorPlugin.class, this);
        registry.register(ImpactorPlugin.class, this); // TODO - Temporary

        this.download();
        this.modules();
        this.listeners();
    }

    @Override
    public void shutdown() {}

    @Override
    protected void modules() {
        List<Class<? extends Module>> modules = Lists.newArrayList(
                ForgeUIModule.class,
                ImpactorEventBusModule.class,
                ImpactorConfigModule.class
        );

        Registry registry = Impactor.getInstance().getRegistry();
        modules.forEach(implementation -> {
            try {
                Module module = implementation.newInstance();

                this.logger().info("Loading module: " + module.name());
                module.builders(registry);
                module.register(registry);
            } catch (Exception e) {
                this.logger().error("Failed to instantiate a module with class type: " + implementation.getSimpleName());
            }
        });
    }

    @Override
    public Set<Dependency> dependencies() {
        return ImmutableSet.copyOf(Lists.newArrayList(
                ProvidedDependencies.ADVENTURE_GSON_SERIALIZER,
                ProvidedDependencies.ADVENTURE_MINIMESSAGE,
                Dependency.builder()
                        .name("GooeyLibs")
                        .group("ca{}landonjw")
                        .artifact("GooeyLibs")
                        .version("1.16.5-2.3.0")
                        .checksum("0sBRBZ3W4ezFRB3COytWJQwl/88Atw4MP0D7YVLOr4o=")
                        .relocation(Relocation.of("ca{}landonjw", "landonjw"))
                        .build(),
                ProvidedDependencies.SPONGE_MATH,
                ProvidedDependencies.KYORI_EVENT_API,
                ProvidedDependencies.KYORI_EVENT_METHOD,
                ProvidedDependencies.KYORI_EVENT_METHOD_ASM
        ));
    }

    @Override
    public ImpactorBootstrap bootstrapper() {
        return this.bootstrap;
    }

    @Override
    protected void listeners() {}

    @Override
    protected void commands() {
        MinecraftForge.EVENT_BUS.register(new CommandProvider());
    }

    @Override
    protected void placeholders() {}
}
