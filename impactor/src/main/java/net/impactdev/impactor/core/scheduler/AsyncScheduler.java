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

package net.impactdev.impactor.core.scheduler;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.scheduler.SchedulerTask;
import net.impactdev.impactor.api.scheduler.Ticks;
import net.impactdev.impactor.api.scheduler.v2.Scheduler;
import net.impactdev.impactor.api.scheduler.v2.Task;
import net.impactdev.impactor.api.utility.ExceptionPrinter;
import net.impactdev.impactor.core.plugin.BaseImpactorPlugin;
import net.kyori.adventure.key.Key;
import org.checkerframework.checker.index.qual.NonNegative;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AsyncScheduler implements Scheduler {

    private static final String THREAD_NAME = "Impactor Scheduler";
    private static final String WORKER_PREFIX = "Impactor Worker - ";
    private static final int PARALLELISM = 16;

    private final ScheduledThreadPoolExecutor scheduler;
    private final ForkJoinPool worker;

    public AsyncScheduler() {
        this.scheduler = new ScheduledThreadPoolExecutor(1, r -> {
            Thread thread = Executors.defaultThreadFactory().newThread(r);
            thread.setName(THREAD_NAME);
            thread.setDaemon(true);
            return thread;
        });
        this.scheduler.setRemoveOnCancelPolicy(true);
        this.scheduler.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        this.worker = new ForkJoinPool(PARALLELISM, new WorkerThreadFactory(), new ExceptionHandler(), false);
    }

    @Override
    public Key key() {
        return Scheduler.ASYNCHRONOUS;
    }

    @Override
    public Executor executor() {
        return this.worker;
    }

    @Override
    public void publish(@NotNull Runnable action) {
        this.worker.execute(action);
    }

    @Override
    public SchedulerTask delayed(@NotNull Consumer<Task> action, @NotNull Ticks ticks) {
        return this.delayed(() -> action.accept(null), ticks.ticks() / 20, TimeUnit.SECONDS);
    }

    @Override
    public SchedulerTask delayed(@NotNull Runnable action, @NonNegative long delay, @NotNull TimeUnit unit) {
        ScheduledFuture<?> future = this.scheduler.schedule(() -> this.worker.execute(action), delay, unit);
        return () -> future.cancel(false);
    }

    @Override
    public SchedulerTask repeating(@NotNull Runnable action, @NotNull Ticks ticks) {
        return this.repeating(action, ticks.ticks() / 20, TimeUnit.SECONDS);
    }

    @Override
    public SchedulerTask repeating(@NotNull Runnable action, @NonNegative long interval, @NotNull TimeUnit unit) {
        ScheduledFuture<?> future = this.scheduler.scheduleAtFixedRate(() -> this.worker.execute(action), 0, interval, unit);
        return () -> future.cancel(false);
    }

    @Override
    public SchedulerTask delayedAndRepeating(@NotNull Runnable action, @NotNull Ticks delay, @NotNull Ticks interval) {
        return this.delayedAndRepeating(action, delay.ticks() / 20, interval.ticks() / 20, TimeUnit.SECONDS);
    }

    @Override
    public SchedulerTask delayedAndRepeating(@NotNull Runnable action, @NonNegative long delay, @NonNegative long interval, @NotNull TimeUnit unit) {
        ScheduledFuture<?> future = this.scheduler.scheduleAtFixedRate(() -> this.worker.execute(action), delay, interval, unit);
        return () -> future.cancel(false);
    }

    @Override
    public void shutdown() {
        this.shutdownExecutor();
        this.shutdownScheduler();
    }

    private void shutdownScheduler() {
        this.scheduler.shutdown();
        try {
            if (!this.scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                BaseImpactorPlugin.instance().logger().severe("Timed out waiting for the Impactor scheduler to terminate");
                reportRunningTasks(thread -> thread.getName().equals(THREAD_NAME));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void shutdownExecutor() {
        this.worker.shutdown();
        try {
            if (!this.worker.awaitTermination(10, TimeUnit.SECONDS)) {
                BaseImpactorPlugin.instance().logger().severe("Timed out waiting for the Impactor worker thread pool to terminate");
                reportRunningTasks(thread -> thread.getName().startsWith(WORKER_PREFIX));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void reportRunningTasks(Predicate<Thread> predicate) {
        Thread.getAllStackTraces().forEach((thread, stack) -> {
            if (predicate.test(thread)) {
                BaseImpactorPlugin.instance().logger().warn("Thread " + thread.getName() + " is blocked, and may be the reason for the slow shutdown!\n" +
                        Arrays.stream(stack).map(el -> "  " + el).collect(Collectors.joining("\n"))
                );
            }
        });
    }

    private static final class WorkerThreadFactory implements ForkJoinPool.ForkJoinWorkerThreadFactory {
        private static final AtomicInteger COUNT = new AtomicInteger(0);

        @Override
        public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
            ForkJoinWorkerThread thread = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
            thread.setDaemon(true);
            thread.setName(WORKER_PREFIX + COUNT.getAndIncrement());
            thread.setContextClassLoader(Impactor.class.getClassLoader());
            return thread;
        }
    }

    private static final class ExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            ExceptionPrinter.print(BaseImpactorPlugin.instance().logger(), e);
        }
    }
}
