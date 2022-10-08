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

import net.impactdev.impactor.api.events.ImpactorEvent;
import net.impactdev.impactor.api.platform.Platform;
import net.impactdev.impactor.api.providers.BuilderProvider;
import net.impactdev.impactor.api.providers.FactoryProvider;
import net.impactdev.impactor.api.providers.ServiceProvider;
import net.impactdev.impactor.api.scheduler.SchedulerAdapter;
import net.kyori.event.EventBus;

public interface Impactor {

    static Impactor instance() {
        return ImpactorServiceProvider.get();
    }

    Platform platform();

    /**
     * Provides access to a set of factories responsible for creating a particular object.
     * Outside of builders, these methods are meant to effectively act as constructors
     * while not maintaining the style of a builder.
     *
     * @return A provider of factories, allowing registration and location of particular
     * factories
     */
    FactoryProvider factories();

    /**
     * Provides access to a set of builders responsible for creating a particular object.
     * These builders are expected to extend from the {@link net.impactdev.impactor.api.builders.Builder}
     * interface, and can both be registered and located from this particular provider.
     *
     * @return A provider of builders, allowing registration and location of particular
     * builders
     */
    BuilderProvider builders();

    ServiceProvider services();

    SchedulerAdapter scheduler();

    EventBus<ImpactorEvent> events();

}
