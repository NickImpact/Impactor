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

package net.impactdev.impactor.api.text.events;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import net.impactdev.impactor.api.text.placeholders.PlaceholderParser;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Represents the event which is responsible for registering placeholders to Impactor.
 */
public interface PlaceholderRegistrationEvent {

    /**
     * Registers a new placeholder parser to Impactor using the given key.
     *
     * @param key The key to represent the parser
     * @param parser The parser to bind to the given key
     * @return The event to allow for registration chaining
     */
    @NotNull
    @CanIgnoreReturnValue
    PlaceholderRegistrationEvent register(Key key, PlaceholderParser parser);

    /**
     * Registers a map of placeholder parsers to Impactor with their binding keys.
     *
     * @param parsers A map of keys to placeholder parsers
     * @return The event to allow for registration chaining
     */
    @NotNull
    @CanIgnoreReturnValue
    PlaceholderRegistrationEvent register(Map<Key, PlaceholderParser> parsers);

}
