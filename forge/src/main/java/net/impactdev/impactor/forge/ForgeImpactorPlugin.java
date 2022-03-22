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

import com.google.common.collect.Lists;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.dependencies.Dependency;
import net.impactdev.impactor.api.dependencies.DependencyManager;
import net.impactdev.impactor.api.dependencies.ProvidedDependencies;
import net.impactdev.impactor.api.dependencies.classpath.ClassPathAppender;
import net.impactdev.impactor.api.dependencies.relocation.Relocation;
import net.impactdev.impactor.api.logging.Logger;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import net.impactdev.impactor.api.plugin.PluginMetadata;
import net.impactdev.impactor.api.plugin.components.Depending;
import net.impactdev.impactor.api.registry.Registry;
import net.impactdev.impactor.api.storage.StorageType;
import net.impactdev.impactor.api.ui.ImpactorUI;
import net.impactdev.impactor.api.ui.icons.Icon;
import net.impactdev.impactor.api.ui.layouts.Layout;
import net.impactdev.impactor.api.ui.pagination.Pagination;
import net.impactdev.impactor.common.api.ApiRegistrationUtil;
import net.impactdev.impactor.common.dependencies.DependencyContainer;
import net.impactdev.impactor.common.ui.LayoutImpl;
import net.impactdev.impactor.forge.api.ForgeImpactorAPIProvider;
import net.impactdev.impactor.forge.commands.CommandProvider;
import net.impactdev.impactor.forge.dependencies.ForgeClassPathAppender;
import net.impactdev.impactor.forge.logging.ForgeLogger;
import net.impactdev.impactor.forge.ui.container.ForgeUI;
import net.impactdev.impactor.forge.ui.container.icons.ForgeIcon;
import net.minecraftforge.common.MinecraftForge;

import java.util.Collections;
import java.util.List;

public class ForgeImpactorPlugin implements ImpactorPlugin, Depending {

    private static ForgeImpactorPlugin instance;

    private final ForgeBootstrap bootstrap;
    private final Logger logger;
    private final PluginMetadata metadata = PluginMetadata.builder()
            .id("impactor")
            .name("Impactor")
            .version("@version@")
            .description("Cross Platform API ")
            .build();

    public ForgeImpactorPlugin(ForgeBootstrap bootstrap, org.apache.logging.log4j.Logger delegate) {
        instance = this;
        this.bootstrap = bootstrap;
        this.logger = new ForgeLogger(delegate);
    }

    public static ForgeImpactorPlugin getInstance() {
        return instance;
    }

    public ForgeBootstrap getBootstrap() {
        return this.bootstrap;
    }

    @Override
    public PluginMetadata getMetadata() {
        return this.metadata;
    }

    @Override
    public Logger getPluginLogger() {
        return this.logger;
    }

    @Override
    public void construct() {
        ApiRegistrationUtil.register(new ForgeImpactorAPIProvider());
        Registry registry = Impactor.getInstance().getRegistry();
        registry.register(ImpactorPlugin.class, this);
        registry.register(ClassPathAppender.class, new ForgeClassPathAppender(this));
        registry.registerBuilderSupplier(Dependency.DependencyBuilder.class, DependencyContainer.DependencyContainerBuilder::new);

        DependencyManager manager = new DependencyManager(this);
        registry.register(DependencyManager.class, manager);
        manager.loadDependencies(ProvidedDependencies.JAR_RELOCATOR);
        manager.loadDependencies(this.getAllDependencies());

        registry.registerBuilderSupplier(Icon.IconBuilder.class, ForgeIcon.ForgeIconBuilder::new);
        registry.registerBuilderSupplier(Layout.LayoutBuilder.class, LayoutImpl.LayoutImplBuilder::new);
//        registry.registerBuilderSupplier(Pagination.PaginationBuilder.class, SpongePagination.SpongePaginationBuilder::new);
        registry.registerBuilderSupplier(ImpactorUI.UIBuilder.class, ForgeUI.ForgeUIBuilder::new);

        MinecraftForge.EVENT_BUS.register(new CommandProvider());
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }

    @Override
    public List<Dependency> getAllDependencies() {
        return Lists.newArrayList(
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
                ProvidedDependencies.SPONGE_MATH
        );
    }

    @Override
    public List<StorageType> getStorageRequirements() {
        return Collections.emptyList();
    }
}
