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

import net.impactdev.impactor.api.scheduler.Ticks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class SynchronousTask {

    private final UUID uuid = UUID.randomUUID();
    private final Runnable action;

    private final Ticks delay;
    private final Ticks interval;

    private boolean delayed;
    private long tick;

    private boolean cancelled;

    public SynchronousTask(@NotNull Runnable action, @Nullable Ticks delay, @Nullable Ticks interval) {
        this.action = action;
        this.delay = delay;
        this.interval = interval;

        this.delayed = this.delay != null && this.delay.ticks() > 0;
    }

    public void tick() {
        this.tick++;

        if(this.delayed) {
            if(this.tick >= this.delay.ticks()) {
                this.delayed = false;

                this.tick = 0;
                this.action.run();
            }
        } else {
            if(this.interval != null && this.interval.ticks() > 0) {
                if(this.tick >= this.interval.ticks()) {
                    this.tick = 0;
                    this.action.run();
                }
            }
        }
    }

    public UUID uuid() {
        return this.uuid;
    }

    public boolean cancelled() {
        return this.cancelled;
    }

    public void cancel() {
        this.cancelled = true;
    }
}
