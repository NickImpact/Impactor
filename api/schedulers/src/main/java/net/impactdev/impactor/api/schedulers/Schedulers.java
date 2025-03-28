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

package net.impactdev.impactor.api.schedulers;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public final class Schedulers {

    private static final Schedulers INSTANCE = new Schedulers();

    public static final Key SYNCHRONOUS = Key.key("impactor", "schedulers/synchronous");
    public static final Key ASYNCHRONOUS = Key.key("impactor", "schedulers/asynchronous");

    private final Map<Key, Scheduler> schedulers = new HashMap<>();

    private Schedulers() {}

    public static Schedulers instance() {
        return INSTANCE;
    }

    /**
     * Receives the synchronous scheduler that is hooked to the main server thread.
     *
     * <p>With minecraft, this typically means a scheduler which is hooked to the actual ticking of the server.
     * As such, this sort of scheduler is capable of being affected by the speed of the server, and can possibly
     * feature skipped ticks based on the platform.</p>
     *
     * @return A scheduler executing via the main server thread
     */
    public Scheduler synchronous() {
        return this.get(SYNCHRONOUS);
    }

    /**
     * Receives a scheduler running asynchronously from the main server thread. These threads are localized
     * to Impactor, and are unlinked from the main server thread.
     *
     * <p>Note that most game interactions must be run from the main server thread. Due to Minecraft being
     * a single-threaded game by nature, main thread enforcement is very prevalent in handling game
     * interactions properly. Take great care using this scheduler if you need to perform world actions. And if
     * so, delegate your tasks to the {@link #synchronous() synchronous} scheduler and execute via
     * {@link Scheduler#publish(Runnable)}.</p>
     *
     * @return A scheduler executing multiple threads not linked to the main thread
     */
    public Scheduler asynchronous() {
        return this.get(ASYNCHRONOUS);
    }

    /**
     * Locates a scheduler bound to the registry by the given key.
     *
     * @param key The key binding the scheduler
     * @return A scheduler bound by the given key
     * @throws IllegalArgumentException If the given key does not contain any mapping
     */
    public Scheduler get(final @NotNull Key key) throws IllegalArgumentException {
        Scheduler scheduler = schedulers.get(key);
        if(scheduler == null) {
            throw new IllegalArgumentException("Scheduler not found: " + key);
        }

        return scheduler;
    }

}
