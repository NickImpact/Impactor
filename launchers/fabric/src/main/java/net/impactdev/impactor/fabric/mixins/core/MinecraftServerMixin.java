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

package net.impactdev.impactor.fabric.mixins.core;

import net.impactdev.impactor.api.platform.performance.MemoryWatcher;
import net.impactdev.impactor.api.platform.performance.PerformanceMonitor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MinecraftServer.class)
@Implements(@Interface(iface = PerformanceMonitor.class, prefix = "impactor$"))
public class MinecraftServerMixin {

    @Shadow
    private float averageTickTime;
    @Shadow @Final
    public long[] tickTimes;

    public double impactor$ticksPerSecond() {
        return 1000 / Math.max(50, this.averageTickTime);
    }

    public double impactor$averageTickDuration() {
        int length = this.tickTimes.length;
        long sum = 0;

        for(long tick : this.tickTimes) {
            sum += tick;
        }

        return (sum / (double) length) / 1000000;
    }

    public MemoryWatcher impactor$memory() {
        return new MemoryWatcher();
    }

}
