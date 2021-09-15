/*
 * This file is part of Impactor, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018-2021 NickImpact
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

package net.impactdev.impactor.sponge.scheduler;

import com.google.common.base.Suppliers;
import net.impactdev.impactor.api.scheduler.SchedulerAdapter;
import net.impactdev.impactor.api.scheduler.SchedulerTask;
import net.impactdev.impactor.sponge.SpongeImpactorPlugin;
import org.spongepowered.api.Game;
import org.spongepowered.api.scheduler.ScheduledTask;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.scheduler.TaskExecutorService;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SpongeSchedulerAdapter implements SchedulerAdapter {

    private final SpongeImpactorPlugin bootstrap;
    private final Game game;

    private final Scheduler asyncScheduler;
    private final Supplier<TaskExecutorService> sync;
    private final TaskExecutorService async;

    private final Set<ScheduledTask> tasks = Collections.newSetFromMap(new WeakHashMap<>());

    public SpongeSchedulerAdapter(SpongeImpactorPlugin plugin, Game game) {
        this.game = game;
        this.bootstrap = plugin;

        this.asyncScheduler = game.asyncScheduler();
        this.async = this.asyncScheduler.createExecutor(plugin.getPluginContainer());
        this.sync = Suppliers.memoize(() -> this.game.server().scheduler().createExecutor(plugin.getPluginContainer()));
    }

    @Override
    public Executor async() {
        return this.async;
    }

    @Override
    public Executor sync() {
        return this.sync.get();
    }

    public Scheduler getSyncScheduler() {
        return this.game.server().scheduler();
    }

    private SchedulerTask submitAsyncTask(Runnable runnable, Consumer<Task.Builder> config) {
        Task.Builder builder = Task.builder();
        config.accept(builder);

        Task task = builder
                .execute(runnable)
                .plugin(this.bootstrap.getPluginContainer())
                .build();
        ScheduledTask scheduledTask = this.asyncScheduler.submit(task);
        this.tasks.add(scheduledTask);
        return scheduledTask::cancel;
    }

    @Override
    public SchedulerTask asyncLater(Runnable task, long delay, TimeUnit unit) {
        return this.submitAsyncTask(task, builder -> builder.delay(delay, unit));
    }

    @Override
    public SchedulerTask asyncRepeating(Runnable task, long interval, TimeUnit unit) {
        return this.submitAsyncTask(task, builder -> builder.delay(interval, unit).interval(interval, unit));
    }

    @Override
    public SchedulerTask asyncDelayedAndRepeating(Runnable task, long delay, TimeUnit dUnit, long interval, TimeUnit iUnit) {
        return this.submitAsyncTask(task, builder -> builder.delay(delay, dUnit).interval(interval, iUnit));
    }

    @Override
    public void shutdownScheduler() {
        for(ScheduledTask task : this.tasks) {
            try {
                task.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void shutdownExecutor() {}

}
