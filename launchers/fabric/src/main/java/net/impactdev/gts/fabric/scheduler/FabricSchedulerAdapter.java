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

package net.impactdev.gts.fabric.scheduler;

import net.impactdev.gts.fabric.FabricImpactorBootstrap;
import net.impactdev.impactor.api.logging.PluginLogger;
import net.impactdev.impactor.api.scheduler.AbstractJavaScheduler;

import java.util.concurrent.Executor;

public final class FabricSchedulerAdapter extends AbstractJavaScheduler {

    private final Executor sync;

    public FabricSchedulerAdapter(FabricImpactorBootstrap bootstrap) {
        super(bootstrap.logger());
        this.sync = r -> bootstrap.server().orElseThrow(() -> new IllegalStateException("Server not yet available")).submit(r).join();
    }

    @Override
    public Executor sync() {
        return this.sync;
    }
}
