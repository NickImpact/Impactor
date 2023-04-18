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

package net.impactdev.impactor.core.utility.future;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.impactdev.impactor.api.utility.ExceptionPrinter;
import net.impactdev.impactor.core.plugin.BaseImpactorPlugin;
import net.impactdev.impactor.core.utility.future.ThrowingRunnable;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;

public final class Futures {

    public static final ExecutorService ASYNC_EXECUTOR = Executors.newFixedThreadPool(
            8,
            new ThreadFactoryBuilder()
                    .setNameFormat("Impactor Async Executor - #%d")
                    .setDaemon(true)
                    .build()
    );

    public static <T> CompletableFuture<T> execute(Callable<T> method) {
        return execute(ASYNC_EXECUTOR, method);
    }

    public static <T> CompletableFuture<T> execute(Executor executor, Callable<T> method) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return method.call();
            } catch (Exception e) {
                ExceptionPrinter.print(BaseImpactorPlugin.instance().logger(), e);
                throw new CompletionException(e);
            }
        }, executor);
    }

    public static CompletableFuture<Void> execute(ThrowingRunnable runnable) {
        return execute(ASYNC_EXECUTOR, runnable);
    }

    public static CompletableFuture<Void> execute(Executor executor, ThrowingRunnable runnable) {
        return CompletableFuture.runAsync(() -> {
            try {
                runnable.run();
            } catch (Exception e) {
                ExceptionPrinter.print(BaseImpactorPlugin.instance().logger(), e);
                throw new CompletionException(e);
            }
        }, executor);
    }

    public static <T> CompletableFuture<T> makeFutureDelayed(Callable<T> callable, long delay, TimeUnit unit) {
        ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);

        return CompletableFuture.supplyAsync(() -> {
            try {
                return callable.call();
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new CompletionException(e);
            }
        }, r -> scheduler.schedule(() -> ASYNC_EXECUTOR.execute(r), delay, unit));
    }

    public static <T> CompletableFuture<T> makeFutureDelayed(Callable<T> callable, Executor executor, long delay, TimeUnit unit) {
        ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);

        return CompletableFuture.supplyAsync(() -> {
            try {
                return callable.call();
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new CompletionException(e);
            }
        }, r -> scheduler.schedule(() -> executor.execute(r), delay, unit));
    }

    public static CompletableFuture<Void> timed(ThrowingRunnable runnable, long duration, TimeUnit unit) {
        return execute(runnable).acceptEither(timeoutAfter(duration, unit), ignore -> {});
    }

    public static CompletableFuture<Void> timed(ThrowingRunnable runnable, Executor executor, long duration, TimeUnit unit) {
        return execute(executor, runnable).acceptEither(timeoutAfter(duration, unit), ignore -> {});
    }

    public static <T> CompletableFuture<T> timed(Callable<T> callable, long duration, TimeUnit unit) {
        return execute(callable).applyToEither(timeoutAfter(duration, unit), value -> value);
    }

    /**
     * Forces a completable future to timeout its actions after the specified amount of time. This is best used
     * with {@link CompletableFuture#acceptEither(CompletionStage, Consumer) acceptEither},
     * {@link CompletableFuture#applyToEither(CompletionStage, Function) applyToEither}, or any of their respective
     * async companions.
     *
     * @param timeout The amount of time that it should take before we forcibly raise a timeout exception
     * @param unit The time unit to measure our timeout value by
     * @param <W> The intended return type of the completable future (for compatibility with both run and supply)
     * @return A completable future who's sole purpose is to timeout after X amount of time
     */
    public static <W> CompletableFuture<W> timeoutAfter(long timeout, TimeUnit unit) {
        return makeFutureDelayed(() -> {
            throw new TimeoutException();
        }, timeout, unit);
    }

    /**
     * Forces a completable future to timeout its actions after the specified amount of time. This is best used
     * with {@link CompletableFuture#acceptEither(CompletionStage, Consumer) acceptEither},
     * {@link CompletableFuture#applyToEither(CompletionStage, Function) applyToEither}, or any of their respective
     * async companions.
     *
     * @param timeout The amount of time that it should take before we forcibly raise a timeout exception
     * @param unit The time unit to measure our timeout value by
     * @param <W> The intended return type of the completable future (for compatibility with both run and supply)
     * @return A completable future who's sole purpose is to timeout after X amount of time
     */
    public static <W> CompletableFuture<W> timeoutAfter(Executor executor, long timeout, TimeUnit unit) {
        return makeFutureDelayed(() -> {
            throw new TimeoutException();
        }, executor, timeout, unit);
    }

    /**
     * Forces a completable future to timeout its actions after the specified amount of time. This is best used
     * with {@link CompletableFuture#acceptEither(CompletionStage, Consumer) acceptEither},
     * {@link CompletableFuture#applyToEither(CompletionStage, Function) applyToEither}, or any of their respective
     * async companions.
     *
     * @param timeout The amount of time that it should take before we forcibly raise a timeout exception
     * @param unit The time unit to measure our timeout value by
     * @param <W> The intended return type of the completable future (for compatibility with both run and supply)
     * @return A completable future who's sole purpose is to timeout after X amount of time
     */
    public static <W> CompletableFuture<W> timeoutAfter(Runnable runnable, Executor executor, long timeout, TimeUnit unit) {
        return makeFutureDelayed(() -> {
            runnable.run();
            throw new TimeoutException();
        }, executor, timeout, unit);
    }

}
