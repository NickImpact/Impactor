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

package net.impactdev.impactor.api.placeholders;

import net.impactdev.impactor.api.adventure.TextProcessor;
import net.impactdev.impactor.api.services.Service;
import net.kyori.adventure.key.Key;

import java.util.Map;

/**
 * Represents a service responsible for managing placeholders registered by different plugins. These placeholders
 * are represented by a parser which accepts a {@link net.impactdev.impactor.api.utilities.context.Context} for
 * additional information regarding placeholders.
 *
 * <p>The placeholder service goes hand in hand with a {@link TextProcessor}
 * which parses a raw string into a valid Adventure {@link net.kyori.adventure.text.Component}, complete
 * with styling and filled in placeholders.
 *
 * <h2>Platform Placeholder Services</h2>
 * With some platforms, such as Sponge, placeholders exist with their own service. Impactor will do its best
 * to translate these placeholders, but if the source for a target placeholder does not support the provided
 * context resolution, these could display unexpectedly.
 */
public interface PlaceholderService extends Service {

    void register(Key key, PlaceholderParser parser);

    Map<Key, PlaceholderParser> parsers();

}
