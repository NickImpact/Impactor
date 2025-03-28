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

package net.impactdev.impactor.api.events;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a ruleset which details how a subscriber should act on the invocation of an event. The general
 * idea behind this is to permit custom execution rules, such as not executing if the event has been cancelled.
 *
 * @param <E> The event type
 * @since 6.0.0
 */
public interface Accepts<E> {

    /**
     * Tests if a subscriber will accept an incoming event.
     *
     * @param event The actual event data posted
     * @return If the subscriber should be invoked
     * @since 6.0.0
     */
    boolean accepts(final @NotNull E event);

    /**
     * Specifies an Accepts rule which allows all incoming invocations to be allowed, regardless of event state.
     *
     * @return An allow all invocation ruleset
     * @param <E> The event type
     * @since 6.0.0
     */
    static <E> Accepts<E> allowAll() {
        return (event) -> true;
    }

}
