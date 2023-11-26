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

package net.impactdev.impactor.minecraft.plugin;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.scheduler.Ticks;
import net.impactdev.impactor.api.scheduler.v2.Scheduler;
import net.impactdev.impactor.api.scheduler.v2.Schedulers;
import net.impactdev.impactor.core.modules.ModuleInitializer;
import net.impactdev.impactor.core.plugin.BaseImpactorPlugin;
import net.impactdev.impactor.core.plugin.ImpactorBootstrapper;
import net.impactdev.impactor.minecraft.items.ItemsModule;
import net.impactdev.impactor.minecraft.platform.GamePlatform;
import net.impactdev.impactor.minecraft.scheduler.SyncScheduler;
import net.impactdev.impactor.minecraft.ui.UIModule;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public abstract class GameImpactorPlugin extends BaseImpactorPlugin {

    public GameImpactorPlugin(ImpactorBootstrapper bootstrapper) {
        super(bootstrapper);
    }

    @Override
    protected ModuleInitializer registerModules() {
        return super.registerModules()
                .with(ItemsModule.class)
                .with(UIModule.class);
    }

    @Override
    public void construct() {
        super.construct();
    }

    @Override
    public void starting() {
        ((SyncScheduler) Schedulers.require(Scheduler.SYNCHRONOUS)).initialize(this.platform().server());

        Scheduler async = Schedulers.require(Scheduler.ASYNCHRONOUS);
        async.delayedAndRepeating(() -> this.logger().info("Testing"), Ticks.of(20), Ticks.of(30 * 20));

        AtomicReference<Instant> marker = new AtomicReference<>();
        Scheduler sync = Schedulers.require(Scheduler.SYNCHRONOUS);
        sync.repeating(() -> {
            if(marker.get() == null) {
                marker.set(Instant.now());
                this.logger().info("Invoking first synchronized action!");
            } else {
                Duration since = Duration.between(marker.get(), marker.updateAndGet(current -> Instant.now()));
                this.logger().info("Invoking synchronized action, took " + since.getSeconds() + " seconds since last iteration...");
            }
        }, 5, TimeUnit.SECONDS);
    }

    @Override
    protected void setupSchedulers() {
        super.setupSchedulers();

        Schedulers.register(Scheduler.SYNCHRONOUS, new SyncScheduler(this.platform()));
    }

    private GamePlatform platform() {
        return ((GamePlatform) Impactor.instance().platform());
    }
}
