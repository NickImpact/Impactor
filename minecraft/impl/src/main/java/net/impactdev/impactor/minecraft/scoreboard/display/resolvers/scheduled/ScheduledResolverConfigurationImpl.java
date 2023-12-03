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
import net.impactdev.impactor.api.scheduler.Ticks;
import net.impactdev.impactor.api.scheduler.v2.Scheduler;
import net.impactdev.impactor.api.scoreboards.display.formatters.DisplayFormatter;
import net.impactdev.impactor.api.scoreboards.display.resolvers.ComponentProvider;
import net.impactdev.impactor.api.scoreboards.display.resolvers.ComponentResolver;
import net.impactdev.impactor.api.scoreboards.display.resolvers.scheduled.ScheduledResolverConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

public final class ScheduledResolverConfigurationImpl implements ScheduledResolverConfiguration {

    private final ComponentProvider provider;
    private final Scheduler scheduler;
    private final DisplayFormatter formatter;
    private final ScheduledTaskProvider task;

    public ScheduledResolverConfigurationImpl(TaskBuilder builder) {
        this.provider = builder.provider;
        this.scheduler = builder.scheduler;
        this.formatter = builder.formatter;
        this.task = builder.task;
    }

    @Override
    public ComponentProvider provider() {
        return this.provider;
    }

    @Override
    public @Nullable DisplayFormatter formatter() {
        return this.formatter;
    }

    @Override
    public ComponentResolver create() {
        return new ScheduledResolverImpl(this);
    }

    @Override
    public Scheduler scheduler() {
        return this.scheduler;
    }

    public ScheduledTaskProvider task() {
        return this.task;
    }

    public static final class TaskBuilder implements Configuration, TaskProperties {

        private ComponentProvider provider;
        private DisplayFormatter formatter;
        private Scheduler scheduler;
        private ScheduledTaskProvider task;

        @Override
        public Configuration provider(ComponentProvider provider) {
            this.provider = provider;
            return this;
        }

        @Override
        public Configuration formatter(DisplayFormatter formatter) {
            this.formatter = formatter;
            return this;
        }

        @Override
        public TaskProperties scheduler(Scheduler scheduler) {
            this.scheduler = scheduler;
            return this;
        }

        @Override
        public Configuration repeating(long delay, long interval, TimeUnit unit) {
            this.task = (scheduler, action) -> scheduler.delayedAndRepeating(action, delay, interval, unit);
            return this;
        }

        @Override
        public Configuration repeating(Ticks delay, Ticks interval) {
            this.task = (scheduler, action) -> scheduler.delayedAndRepeating(action, delay, interval);
            return this;
        }

        @Override
        public Configuration delayed(long delay, TimeUnit unit) {
            this.task = (scheduler, action) -> scheduler.delayed(action, delay, unit);
            return this;
        }

        @Override
        public Configuration delayed(Ticks delay) {
            this.task = (scheduler, action) -> scheduler.delayed(action, delay);
            return this;
        }

        @Override
        public ScheduledResolverConfiguration build() {
            return new ScheduledResolverConfigurationImpl(this);
        }
    }

    @FunctionalInterface
    public interface ScheduledTaskProvider {

        SchedulerTask schedule(Scheduler scheduler, Runnable action);

    }
}
