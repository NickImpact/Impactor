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

package net.impactdev.impactor.api.economy.currency;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Represents the service responsible for providing currency information to the economy service.
 *
 * @since 5.0.0
 */
public interface CurrencyProvider {

    /**
     * Specifies the primary (or default) currency that is represented by the economy service.
     * This should always be present, and if not, indicates a problem with the implementation.
     *
     * @return The primary currency supported by the economy service
     * @throws IllegalStateException If no currency is capable of being accessed
     * @since 5.0.0
     */
    @NotNull Currency primary();

    /**
     * Attempts to find a currency registered with the given identifier. If none can be
     * identified, this will return an empty optional.
     *
     * @param key The key for the currency
     * @return An optionally populated currency, if it exists
     * @since 5.0.0
     */
    Optional<Currency> currency(Key key);


}
