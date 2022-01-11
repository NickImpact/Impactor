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

package net.impactdev.impactor.velocity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.dependencies.Dependency;
import net.impactdev.impactor.api.dependencies.DependencyManager;
import net.impactdev.impactor.api.dependencies.classpath.ClassPathAppender;
import net.impactdev.impactor.api.dependencies.classpath.ReflectionClassPathAppender;
import net.impactdev.impactor.api.event.EventBus;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import net.impactdev.impactor.api.plugin.PluginMetadata;
import net.impactdev.impactor.api.plugin.components.Depending;
import net.impactdev.impactor.api.plugin.registry.PluginRegistry;
import net.impactdev.impactor.api.storage.StorageType;
import net.impactdev.impactor.common.api.ApiRegistrationUtil;
import net.impactdev.impactor.velocity.api.VelocityImpactorAPIProvider;
import net.impactdev.impactor.velocity.event.VelocityEventBus;
import net.impactdev.impactor.velocity.plugin.AbstractVelocityPlugin;
import net.impactdev.impactor.velocity.scheduler.VelocitySchedulerAdapter;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;

public class VelocityImpactorPlugin extends AbstractVelocityPlugin implements Depending {

    public static VelocityImpactorPlugin instance;

    private final VelocityImpactorBootstrap bootstrap;

    public VelocityImpactorPlugin(VelocityImpactorBootstrap bootstrap) {
        super(PluginMetadata.builder()
                .id("impactor")
                .name("Impactor")
                .version("@version@")
                .build());
        this.bootstrap = bootstrap;

        instance = this;
    }

    public void init() {
        ApiRegistrationUtil.register(new VelocityImpactorAPIProvider(new VelocitySchedulerAdapter(this.bootstrap )));

        Impactor.getInstance().getRegistry().register(ImpactorPlugin.class, this);
        Impactor.getInstance().getRegistry().register(ClassPathAppender.class, new ReflectionClassPathAppender(this.getClass().getClassLoader()));
        Impactor.getInstance().getRegistry().register(DependencyManager.class, new DependencyManager(this));

        this.getPluginLogger().info("Pooling plugin dependencies...");
        List<Dependency> toLaunch = Lists.newArrayList();
        for(ImpactorPlugin plugin : PluginRegistry.getAll()) {
            if(plugin instanceof Depending) {
                Depending dependable = (Depending) plugin;

                for(Dependency dependency : dependable.getAllDependencies()) {
                    if(toLaunch.contains(dependency)) {
                        continue;
                    }

                    toLaunch.add(dependency);
                }

                for(Dependency storage : this.getDependencyManager().getRegistry().resolveStorageDependencies(Sets.newHashSet(dependable.getStorageRequirements()))) {
                    if(toLaunch.contains(storage)) {
                        continue;
                    }

                    toLaunch.add(storage);
                }
            }
        }

        this.getPluginLogger().info("Dependencies found, setting these up now...");
        this.getPluginLogger().info("Initializing default dependencies...");
        this.getDependencyManager().loadDependencies(EnumSet.of(Dependency.CONFIGURATE_CORE, Dependency.CONFIGURATE_HOCON, Dependency.HOCON_CONFIG, Dependency.CONFIGURATE_GSON, Dependency.CONFIGURATE_YAML));
        this.getDependencyManager().loadDependencies(new HashSet<>(toLaunch));

        Impactor.getInstance().getRegistry().register(EventBus.class, new VelocityEventBus(this.bootstrap));

    }

    public static VelocityImpactorPlugin getInstance() {
        return instance;
    }

    public VelocityImpactorBootstrap getBootstrap() {
        return bootstrap;
    }

    private DependencyManager getDependencyManager() {
        return Impactor.getInstance().getRegistry().get(DependencyManager.class);
    }

    @Override
    public List<Dependency> getAllDependencies() {
        return ImmutableList.copyOf(Lists.newArrayList(
                Dependency.KYORI_EVENT,
                Dependency.KYORI_EVENT_METHOD,
                Dependency.KYORI_EVENT_METHOD_ASM,
                Dependency.BYTEBUDDY,
                Dependency.OBJECT_WEB
        ));
    }

    @Override
    public List<StorageType> getStorageRequirements() {
        return Lists.newArrayList();
    }
}
