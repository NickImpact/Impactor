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

package net.impactdev.impactor.minecraft.scoreboard.updaters.scheduled;

import net.impactdev.impactor.api.scheduler.Ticks;
import net.impactdev.impactor.api.scheduler.v2.Scheduler;
import net.impactdev.impactor.api.scoreboards.updaters.scheduled.ScheduledConfiguration;
import net.impactdev.impactor.api.scoreboards.updaters.scheduled.ScheduledUpdater;

import java.util.concurrent.TimeUnit;

public record ScheduledConfigurationImpl(Scheduler scheduler, ScheduledTaskProvider provider) implements ScheduledConfiguration {
    @Override
    public ScheduledUpdater generate() {
        return new ScheduledUpdaterImpl(this);
    }

    public static final class Configuration implements ScheduledConfiguration.ProvideScheduler, ScheduledConfiguration.ConfigureTask {

        private Scheduler scheduler;

        @Override
        public ConfigureTask scheduler(Scheduler scheduler) {
            this.scheduler = scheduler;
            return this;
        }

        @Override
        public ScheduledConfiguration repeating(long delay, long interval, TimeUnit unit) {
            return new ScheduledConfigurationImpl(this.scheduler, (scheduler, action) -> scheduler.delayedAndRepeating(action, delay, interval, unit));
        }

        @Override
        public ScheduledConfiguration repeating(Ticks delay, Ticks interval) {
            return new ScheduledConfigurationImpl(this.scheduler, (scheduler, action) -> scheduler.delayedAndRepeating(action, delay, interval));
        }

        @Override
        public ScheduledConfiguration delayed(long delay, TimeUnit unit) {
            return new ScheduledConfigurationImpl(this.scheduler, (scheduler, action) -> scheduler.delayed(action, delay, unit));
        }

        @Override
        public ScheduledConfiguration delayed(Ticks delay) {
            return new ScheduledConfigurationImpl(this.scheduler, (scheduler, action) -> scheduler.delayed(action, delay));
        }
    }
}
