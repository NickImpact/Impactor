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

package net.impactdev.impactor.minecraft.scheduler;

import com.google.common.collect.Maps;
import net.impactdev.impactor.api.scheduler.v2.Scheduler;
import net.impactdev.impactor.api.scheduler.SchedulerTask;
import net.impactdev.impactor.api.scheduler.Ticks;
import net.impactdev.impactor.minecraft.platform.GamePlatform;
import net.kyori.adventure.key.Key;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class SyncScheduler implements Scheduler {

    private final Map<UUID, SynchronousTask> tasks = Maps.newConcurrentMap();
    private final Executor sync;

    public SyncScheduler(GamePlatform platform) {
        this.sync = r -> platform.server().executeBlocking(r);
    }

    public void initialize(MinecraftServer server) {
        server.addTickable(this::tick);
    }

    @Override
    public Key key() {
        return Scheduler.SYNCHRONOUS;
    }

    @Override
    public Executor executor() {
        return this.sync;
    }

    @Override
    public void publish(@NotNull Runnable runnable) {
        this.sync.execute(runnable);
    }

    @Override
    public SchedulerTask delayed(@NotNull Runnable action, @NotNull Ticks ticks) {
        SynchronousTask task = new SynchronousTask(action, ticks, Ticks.zero());
        this.tasks.put(task.uuid(), task);
        return task::cancel;
    }

    @Override
    public SchedulerTask delayed(@NotNull Runnable action, long delay, @NotNull TimeUnit unit) {
        return this.delayed(action, Ticks.ofWallClockTime(delay, unit.toChronoUnit()));
    }

    @Override
    public SchedulerTask repeating(@NotNull Runnable action, @NotNull Ticks ticks) {
        SynchronousTask task = new SynchronousTask(action, Ticks.zero(), ticks);
        this.tasks.put(task.uuid(), task);
        return task::cancel;
    }

    @Override
    public SchedulerTask repeating(@NotNull Runnable action, long interval, @NotNull TimeUnit unit) {
        return this.repeating(action, Ticks.ofWallClockTime(interval, unit.toChronoUnit()));
    }

    @Override
    public SchedulerTask delayedAndRepeating(@NotNull Runnable action, @NotNull Ticks delay, @NotNull Ticks interval) {
        SynchronousTask task = new SynchronousTask(action, delay, interval);
        this.tasks.put(task.uuid(), task);
        return task::cancel;
    }

    @Override
    public SchedulerTask delayedAndRepeating(@NotNull Runnable action, long delay, long interval, @NotNull TimeUnit unit) {
        return this.delayedAndRepeating(
                action,
                Ticks.ofWallClockTime(delay, unit.toChronoUnit()),
                Ticks.ofWallClockTime(interval, unit.toChronoUnit())
        );
    }

    @Override
    public void shutdown() {
        this.tasks.forEach((key, task) -> task.cancel());
    }

    private void tick() {
        this.tasks.forEach((key, task) -> {
            task.tick();
            if(task.cancelled()) {
                this.tasks.remove(key);
            }
        });
    }

}
