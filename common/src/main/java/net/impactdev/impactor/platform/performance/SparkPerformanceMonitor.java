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

package net.impactdev.impactor.platform.performance;

import me.lucko.spark.api.Spark;
import me.lucko.spark.api.SparkProvider;
import me.lucko.spark.api.statistic.StatisticWindow;
import me.lucko.spark.api.statistic.misc.DoubleAverageInfo;
import net.impactdev.impactor.api.platform.performance.MemoryWatcher;
import net.impactdev.impactor.api.platform.performance.PerformanceMonitor;

import java.util.Optional;

public class SparkPerformanceMonitor implements PerformanceMonitor {

    private final Spark api = SparkProvider.get();

    @Override
    public double ticksPerSecond() {
        return Optional.ofNullable(this.api.tps())
                .map(stat -> stat.poll(StatisticWindow.TicksPerSecond.MINUTES_1))
                .orElse(PerformanceMonitor.INCOMPATIBLE_VALUE);
    }

    @Override
    public double averageTickDuration() {
        return Optional.ofNullable(this.api.mspt())
                .map(stat -> stat.poll(StatisticWindow.MillisPerTick.MINUTES_1))
                .map(DoubleAverageInfo::mean)
                .orElse(PerformanceMonitor.INCOMPATIBLE_VALUE);
    }

    @Override
    public MemoryWatcher memory() {
        return new MemoryWatcher();
    }

}
