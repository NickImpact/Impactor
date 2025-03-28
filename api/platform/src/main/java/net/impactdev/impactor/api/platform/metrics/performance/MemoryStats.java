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

package net.impactdev.impactor.api.platform.metrics.performance;

public final class MemoryStats {

    public long current(ByteType type) {
        Runtime runtime = Runtime.getRuntime();
        return type.translate(runtime.totalMemory() - type.translate(runtime.freeMemory()));
    }

    public long allocated(ByteType type) {
        Runtime runtime = Runtime.getRuntime();
        return type.translate(runtime.totalMemory());
    }

    public long max(ByteType type) {
        return type.translate(Runtime.getRuntime().maxMemory());
    }

    public enum ByteType {
        BYTE,
        KILOBYTE,
        MEGABYTE,
        GIGABYTE;

        private static final short BYTE_DIVISOR = 1024;
        public long translate(long bytes) {
            return bytes / (long) Math.pow(BYTE_DIVISOR, this.ordinal());
        }
    }

}
