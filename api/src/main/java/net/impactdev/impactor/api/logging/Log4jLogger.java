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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.HashMap;
import java.util.Map;

public class Log4jLogger implements PluginLogger {

    private final Logger delegate;
    private final Map<String, Marker> markers = new HashMap<>();

    public Log4jLogger(Logger delegate) {
        this.delegate = delegate;
    }

    private Marker get(String marker) {
        return this.markers.computeIfAbsent(marker, MarkerManager::getMarker);
    }

    @Override
    public void info(String line) {
        this.delegate.info(line);
    }

    @Override
    public void warn(String line) {
        this.delegate.warn(line);
    }

    @Override
    public void warn(String line, Throwable throwable) {
        this.delegate.warn(line, throwable);
    }

    @Override
    public void severe(String line) {
        this.delegate.error(line);
    }

    @Override
    public void severe(String line, Throwable throwable) {
        this.delegate.error(line, throwable);
    }

    @Override
    public void debug(String line) {
        this.delegate.debug(line);
    }

}
