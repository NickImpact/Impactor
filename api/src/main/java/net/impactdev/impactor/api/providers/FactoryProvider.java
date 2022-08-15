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

package net.impactdev.impactor.api.providers;

import java.util.NoSuchElementException;

public interface FactoryProvider {

    /**
     * Attempts to provide a factory instance responsible for creating new versions
     * of a target typing.
     *
     * @param type The type of factory this manager should provide
     * @return The instance representing the target factory
     * @param <T> The type of factory
     * @throws NoSuchElementException If the target factory does not exist
     */
    <T> T provide(Class<T> type) throws NoSuchElementException;

    /**
     * Registers a factory to this manager under the specified typing. This typing should
     * match that of the target instance.
     *
     * @param type The class type of the factory
     * @param instance An actual instance of the factory
     * @return <code>true</code> if the factory instance was accepted, <code>false</code> otherwise
     * @param <T> The typing of the factory
     */
    <T> boolean register(Class<T> type, T instance);

}
