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

package net.impactdev.impactor.forge.logging;

import net.impactdev.impactor.api.logging.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ForgeLogger implements Logger {

    private final org.apache.logging.log4j.Logger delegate;
    private final Map<String, Marker> markers = new HashMap<>();

    public ForgeLogger(org.apache.logging.log4j.Logger delegate) {
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
    public void info(Collection<String> lines) {
        lines.forEach(this::info);
    }

    @Override
    public void info(Supplier<String> supplier) {
        this.info(supplier.get());
    }

    @Override
    public void info(String marker, String line) {
        Marker m = this.get(marker);
        this.delegate.info(m, line);
    }

    @Override
    public void info(String marker, Collection<String> lines) {
        lines.forEach(line -> this.info(marker, line));
    }

    @Override
    public void info(String marker, Supplier<String> supplier) {
        Marker m = this.get(marker);
        this.delegate.info(m, supplier);
    }

    @Override
    public void warn(String line) {
        this.delegate.warn(line);
    }

    @Override
    public void warn(Collection<String> lines) {
        lines.forEach(this::warn);
    }

    @Override
    public void warn(Supplier<String> supplier) {
        this.warn(supplier.get());
    }

    @Override
    public void warn(String marker, String line) {
        Marker m = this.get(marker);
        this.delegate.warn(m, line);
    }

    @Override
    public void warn(String marker, Collection<String> lines) {
        lines.forEach(line -> this.warn(marker, line));
    }

    @Override
    public void warn(String marker, Supplier<String> supplier) {
        Marker m = this.get(marker);
        this.delegate.warn(m, supplier);
    }

    @Override
    public void error(String line) {
        this.delegate.error(line);
    }

    @Override
    public void error(Collection<String> lines) {
        lines.forEach(this::error);
    }

    @Override
    public void error(Supplier<String> supplier) {
        this.error(supplier.get());
    }

    @Override
    public void error(String marker, String line) {
        Marker m = this.get(marker);
        this.delegate.error(m, line);
    }

    @Override
    public void error(String marker, Collection<String> lines) {
        lines.forEach(line -> this.error(marker, line));
    }

    @Override
    public void error(String marker, Supplier<String> supplier) {
        Marker m = this.get(marker);
        this.delegate.error(m, supplier);
    }

    @Override
    public void debug(String line) {
        this.delegate.debug(line);
    }

    @Override
    public void debug(Collection<String> lines) {
        lines.forEach(this::debug);
    }

    @Override
    public void debug(Supplier<String> supplier) {
        this.debug(supplier.get());
    }

    @Override
    public void debug(String marker, String line) {
        Marker m = this.get(marker);
        this.delegate.debug(m, line);
    }

    @Override
    public void debug(String marker, Collection<String> lines) {
        lines.forEach(line -> this.debug(marker, line));
    }

    @Override
    public void debug(String marker, Supplier<String> supplier) {
        Marker m = this.get(marker);
        this.delegate.debug(m, supplier);
    }
}
