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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.dependencies.Dependency;
import net.impactdev.impactor.api.dependencies.ProvidedDependencies;
import net.impactdev.impactor.api.logging.PluginLogger;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import net.impactdev.impactor.api.plugin.registry.PluginRegistry;
import net.impactdev.impactor.api.registry.Registry;
import net.impactdev.impactor.common.api.ApiRegistrationUtil;
import net.impactdev.impactor.common.plugin.InternalImpactorPlugin;
import net.impactdev.impactor.velocity.api.VelocityImpactorAPIProvider;

import java.util.Set;

public class VelocityImpactorPlugin extends InternalImpactorPlugin {

    private static VelocityImpactorPlugin instance;
    private final VelocityImpactorBootstrap bootstrap;

    public VelocityImpactorPlugin(VelocityImpactorBootstrap bootstrap) {
        this.bootstrap = bootstrap;
        this.register();
        ApiRegistrationUtil.register(new VelocityImpactorAPIProvider(this.bootstrap.scheduler()));
    }

    public static VelocityImpactorPlugin instance() {
        return instance;
    }

    @Override
    public Set<Dependency> dependencies() {
        return ImmutableSet.copyOf(Lists.newArrayList(
                ProvidedDependencies.BYTEBUDDY,
                ProvidedDependencies.REFLECTIONS
        ));
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
    public VelocityImpactorBootstrap bootstrapper() {
        return this.bootstrap;
    }

    @Override
    protected void listeners() {}

    @Override
    protected void commands() {}

    @Override
    protected void placeholders() {}
}
