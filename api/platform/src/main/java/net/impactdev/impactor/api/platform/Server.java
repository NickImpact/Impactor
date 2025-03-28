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

package net.impactdev.impactor.api.platform;

import net.impactdev.impactor.api.platform.metrics.PerformanceService;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.pointer.Pointered;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;

import java.util.UUID;

/**
 * Represents the actual game server. When running on a proxy, this element represents a particular server
 * connected to the overall network, versus the overall proxy.
 *
 * <p>As a forwarding audience, sending messages to the server is capable of alerting all other applicable
 * audience members on the server. By standard definition, this would be for players primarily.</p>
 */
public interface Server extends Identifiable, Pointered, ForwardingAudience {

    /** Represents the UUID which identifies the server. This is an uuid made up of strictly 0s */
    UUID UUID = new UUID(0, 0);

    /**
     * Provides the services responsible for monitoring performance of the platform. This will
     * include details such as memory usage, server tick times, and much more.
     *
     * @return The service responsible for monitoring performance of the platform
     * @since 6.0.0
     */
    PerformanceService performance();

    /**
     * Schedules the given runnable to the server's main tick loop. This will schedule the task in a blocking
     * manner, and must be completed before the server can tick further.
     *
     * @param runnable The blocking task to execute on the server
     * @since 6.0.0
     */
    void schedule(Runnable runnable);

}
