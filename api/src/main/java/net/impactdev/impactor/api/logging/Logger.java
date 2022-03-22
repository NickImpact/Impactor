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

package net.impactdev.impactor.api.logging;

import java.util.Collection;
import java.util.function.Supplier;

public interface Logger {

    void info(String line);
    void info(Collection<String> lines);
    void info(Supplier<String> supplier);
    void info(String marker, String line);
    void info(String marker, Collection<String> lines);
    void info(String marker, Supplier<String> supplier);

    void warn(String line);
    void warn(Collection<String> lines);
    void warn(Supplier<String> supplier);
    void warn(String marker, String line);
    void warn(String marker, Collection<String> lines);
    void warn(String marker, Supplier<String> supplier);

    void error(String line);
    void error(Collection<String> lines);
    void error(Supplier<String> supplier);
    void error(String marker, String line);
    void error(String marker, Collection<String> lines);
    void error(String marker, Supplier<String> supplier);

    void debug(String line);
    void debug(Collection<String> lines);
    void debug(Supplier<String> supplier);
    void debug(String marker, String line);
    void debug(String marker, Collection<String> lines);
    void debug(String marker, Supplier<String> supplier);
}
