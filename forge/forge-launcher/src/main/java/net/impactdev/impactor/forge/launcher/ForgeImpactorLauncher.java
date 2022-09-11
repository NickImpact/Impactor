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

package net.impactdev.impactor.forge.launcher;

import com.google.common.collect.Sets;
import net.impactdev.impactor.api.logging.Log4jLogger;
import net.impactdev.impactor.api.logging.PluginLogger;
import net.impactdev.impactor.launcher.AbstractLauncher;
import net.impactdev.impactor.launcher.LauncherBootstrap;
import net.impactdev.impactor.launcher.dependencies.Dependency;
import net.impactdev.impactor.launcher.dependencies.provided.ProvidedDependencies;
import net.impactdev.impactor.launcher.dependencies.relocations.Relocation;
import net.impactdev.impactor.launcher.dependencies.repositories.DependencyRepository;
import net.impactdev.impactor.launcher.loader.JarInJarClassLoader;
import net.impactdev.impactor.launcher.PluginLauncher;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;

import java.util.Set;
import java.util.function.Supplier;

@Mod("impactor")
public class ForgeImpactorLauncher extends AbstractLauncher implements PluginLauncher, Supplier<ModContainer> {

    private static final PluginLogger LOGGER = new Log4jLogger(LogManager.getLogger("Impactor"));
    private static final String INTERNAL_JAR = "impactor-forge.jarinjar";
    private static final String BOOTSTRAP_CLASS = "net.impactdev.impactor.forge.ForgeImpactorBootstrap";

    private final JarInJarClassLoader loader;
    private final ModContainer container;
    private LauncherBootstrap plugin;

    public ForgeImpactorLauncher() {
        super(INTERNAL_JAR, BOOTSTRAP_CLASS, LOGGER);
        this.container = ModList.get().getModContainerByObject(this).orElse(null);

        this.loader = new JarInJarClassLoader(this.getClass().getClassLoader(), INTERNAL_JAR);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onSetup);
        MinecraftForge.EVENT_BUS.addListener(this::onServerShutdown);
    }

    public void onSetup(FMLCommonSetupEvent event) {
        this.download(this.loader);
        this.plugin = this.create(this.loader);
        this.plugin.construct();
    }

    public void onServerShutdown(FMLServerStoppingEvent event) {
        this.plugin.shutdown();
    }

    @Override
    public ModContainer get() {
        return this.container;
    }

    @Override
    public String name() {
        return "Impactor";
    }

    @Override
    public LauncherBootstrap create(JarInJarClassLoader loader) {
        return loader.instantiatePlugin(this.bootstrapper(), Supplier.class, this);
    }

    @Override
    public Set<Dependency> dependencies() {
        return Sets.newHashSet(
                ProvidedDependencies.SLF4J_API,
                ProvidedDependencies.CAFFEINE,
                ProvidedDependencies.SPONGE_MATH,
                Dependency.builder()
                        .from(ProvidedDependencies.REFLECTIONS)
                        .relocation(Relocation.of("org{}slf4j", "slf4j"))
                        .build(),
                Dependency.builder()
                        .name("GooeyLibs")
                        .group("net{}impactdev")
                        .artifact("GooeyLibs")
                        .version("1.16.5-2.3.3-SNAPSHOT")
                        .relocation(Relocation.of("ca.landonjw.gooeylibs2", "gooeylibs"))
                        .build(),
                Dependency.builder()
                        .name("Geantyref")
                        .group("io{}leangen{}geantyref")
                        .artifact("geantyref")
                        .version("1.3.13")
                        .relocation(Relocation.of("io{}leangen{}geantyref", "geantyref"))
                        .build()
        );
    }

    @Override
    public Set<DependencyRepository> repositories() {
        return Sets.newHashSet();
    }
}
