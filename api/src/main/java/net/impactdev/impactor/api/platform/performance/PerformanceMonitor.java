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

package net.impactdev.impactor.api.platform.performance;

import net.impactdev.impactor.api.Impactor;

/**
 * Provides performance statistics involving the server. This includes things such as ticks
 * per second, memory usage, and more.
 *
 * <p>If the platform additionally has spark present, the implementation will default to accessing its
 * API for this information for more specific and fine-tuned results. This system is not meant to
 * replace Spark, but rather simply work alongside it. If you need further performance details regarding
 * the platform, consider hooking into Spark and accessing its API directly.
 */
public interface PerformanceMonitor {

    double INCOMPATIBLE_VALUE = 0.0;

    static PerformanceMonitor create() {
        return Impactor.instance().factories().provide(Factory.class).create();
    }

    /**
     * Gets the current TPS statistic from the platform. If the platform does not support ticks
     * (proxies), then this can be expected to return the value specified by {@link #INCOMPATIBLE_VALUE}.
     *
     * @return The current ticks per second rate of the platform
     */
    double ticksPerSecond();

    /**
     * Gets the average tick duration (or milliseconds per tick) of the platform based on the
     * current time. By default, this only polls the last minute of ticks performed by the server.
     *
     * @return The average tick duration for the last minute
     */
    double averageTickDuration();

    /**
     * Provides memory usage statistics for the platform runtime environment. This includes the currently
     * allocated, in use, and max allocation possible.
     *
     * @return Memory statistics regarding the platform runtime environment
     */
    MemoryWatcher memory();

    interface Factory {

        PerformanceMonitor create();

    }

}
