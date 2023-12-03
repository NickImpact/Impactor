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

package net.impactdev.impactor.minecraft.scoreboard.display.resolvers.scheduled;

import net.impactdev.impactor.api.scheduler.SchedulerTask;
import net.impactdev.impactor.api.scheduler.v2.Scheduler;
import net.impactdev.impactor.api.scoreboards.display.Display;
import net.impactdev.impactor.api.scoreboards.display.resolvers.AbstractComponentResolver;
import net.impactdev.impactor.api.scoreboards.display.resolvers.scheduled.ScheduledResolver;
import net.impactdev.impactor.api.scoreboards.display.resolvers.scheduled.ScheduledResolverConfiguration;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

public class ScheduledResolverImpl extends AbstractComponentResolver implements ScheduledResolver {

    private final ScheduledResolverConfiguration configuration;

    private final Scheduler scheduler;
    private final ScheduledResolverConfigurationImpl.ScheduledTaskProvider provider;

    @MonotonicNonNull
    private SchedulerTask task;

    public ScheduledResolverImpl(ScheduledResolverConfigurationImpl config) {
        super(config.provider(), config.formatter());

        this.configuration = config;
        this.scheduler = config.scheduler();
        this.provider = config.task();
    }

    @Override
    public ScheduledResolverConfiguration configuration() {
        return this.configuration;
    }

    @Override
    public SchedulerTask task() {
        return this.task;
    }

    @Override
    public void start(Display displayable) {
        this.task = this.provider.schedule(this.scheduler, displayable::resolve);
    }

    @Override
    public void shutdown(Display displayable) {
        this.task.cancel();
    }


}
