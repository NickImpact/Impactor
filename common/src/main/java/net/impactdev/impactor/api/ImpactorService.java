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

package net.impactdev.impactor.api;

import net.impactdev.impactor.api.event.EventBus;
import net.impactdev.impactor.api.platform.Platform;
import net.impactdev.impactor.api.providers.BuilderProvider;
import net.impactdev.impactor.api.providers.FactoryProvider;
import net.impactdev.impactor.api.providers.ServiceProvider;
import net.impactdev.impactor.api.scheduler.SchedulerAdapter;
import net.impactdev.impactor.providers.BuilderProviderImplementation;
import net.impactdev.impactor.providers.FactoryProviderImplementation;

public class ImpactorService implements Impactor {

    private final FactoryProvider factories = new FactoryProviderImplementation();
    private final BuilderProvider builders = new BuilderProviderImplementation();

    @Override
    public Platform platform() {
        return null;
    }

    @Override
    public FactoryProvider factories() {
        return this.factories;
    }

    @Override
    public BuilderProvider builders() {
        return this.builders;
    }

    @Override
    public ServiceProvider services() {
        return null;
    }

    @Override
    public EventBus events() {
        return null;
    }

    @Override
    public SchedulerAdapter scheduler() {
        return null;
    }
}
