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

package net.impactdev.impactor.minecraft.scoreboard.resolvers;

import net.impactdev.impactor.api.scheduler.v2.Scheduler;
import net.impactdev.impactor.api.scheduler.v2.Schedulers;
import net.impactdev.impactor.api.scheduler.SchedulerTask;
import net.impactdev.impactor.api.text.transforming.transformers.TextTransformer;
import net.impactdev.impactor.scoreboards.updaters.scheduled.ScheduledResolver;
import net.kyori.adventure.text.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public final class ImpactorScheduledResolver implements ScheduledResolver {

    private final Supplier<Component> provider;
    private final TextTransformer transformer;

    private final AtomicReference<Component> component;
    private final SchedulerTask task;

    private final Duration duration;
    private final boolean async;

    private ImpactorScheduledResolver(ImpactorScheduledResolverBuilder builder) {
        this.provider = builder.provider;
        this.transformer = builder.transformer;
        this.duration = Duration.of(builder.time, builder.unit.toChronoUnit());
        this.async = builder.async;

        this.component = new AtomicReference<>(Component.empty());
        this.task = this.configure(builder);
    }

    @Override
    public Component resolve() {
        return this.component.updateAndGet(ignore -> this.transformer.transform(this.provider.get()));
    }

    @Override
    public void shutdown() {
        this.task.cancel();
    }

    @Override
    public Duration interval() {
        return this.duration;
    }

    @Override
    public boolean async() {
        return this.async;
    }

    private SchedulerTask configure(ImpactorScheduledResolverBuilder builder) {
        Runnable action = () -> {

        };

        if(builder.async) {
            Scheduler scheduler = Schedulers.require(Scheduler.ASYNCHRONOUS);
            return scheduler.repeating(action, builder.time, builder.unit);
        }

        Scheduler scheduler = Schedulers.require(Scheduler.SYNCHRONOUS);
        return scheduler.repeating(action, builder.time, builder.unit);
    }

    public static final class ImpactorScheduledResolverBuilder implements ScheduledResolverBuilder {

        private Supplier<Component> provider;
        private long time;
        private TimeUnit unit;
        private boolean async;
        private TextTransformer transformer;

        @Override
        public ScheduledResolverBuilder text(Supplier<Component> provider) {
            this.provider = provider;
            return this;
        }

        @Override
        public ScheduledResolverBuilder interval(long time, TimeUnit unit) {
            this.time = time;
            this.unit = unit;
            return this;
        }

        @Override
        public ScheduledResolverBuilder async() {
            this.async = true;
            return this;
        }

        @Override
        public ScheduledResolverBuilder transformer(TextTransformer transformer) {
            this.transformer = transformer;
            return this;
        }

        @Override
        public ScheduledResolver build() {
            return new ImpactorScheduledResolver(this);
        }

    }

}
