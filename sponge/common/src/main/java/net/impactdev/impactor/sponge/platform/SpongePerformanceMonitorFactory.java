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

package net.impactdev.impactor.sponge.platform;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.platform.performance.MemoryWatcher;
import net.impactdev.impactor.api.platform.performance.PerformanceMonitor;
import net.impactdev.impactor.platform.performance.SparkPerformanceMonitor;
import org.spongepowered.api.Sponge;

public class SpongePerformanceMonitorFactory implements PerformanceMonitor.Factory {
    @Override
    public PerformanceMonitor create() {
        if(Impactor.instance().platform().info().plugin("spark").isPresent()) {
            return new SparkPerformanceMonitor();
        }

        return new PerformanceMonitor() {
            private final MemoryWatcher memory = new MemoryWatcher();

            @Override
            public double ticksPerSecond() {
                return Sponge.server().ticksPerSecond();
            }

            @Override
            public double averageTickDuration() {
                return Sponge.server().averageTickTime();
            }

            @Override
            public MemoryWatcher memory() {
                return this.memory;
            }
        };
    }
}