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

import net.impactdev.impactor.api.plugin.ImpactorPlugin;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class JavaLogger implements PluginLogger {

    private final ImpactorPlugin plugin;
    private final Logger delegate;
    private final Function<String, String> colorizer = in -> in.replaceAll("[&]", "\u00a7");
    private final BiFunction<String, String, String> preprocessor = (marker, input) -> "[" + marker + "] " + input;

    public JavaLogger(ImpactorPlugin plugin, Logger delegate) {
        this.plugin = plugin;
        this.delegate = delegate;
    }

    @Override
    public void info(String line) {
        this.delegate.info(this.colorizer.apply(line));
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
        this.delegate.info(this.colorizer.apply(this.preprocessor.apply(marker, line)));
    }

    @Override
    public void info(String marker, Collection<String> lines) {
        lines.forEach(l -> this.info(marker, l));
    }

    @Override
    public void info(String marker, Supplier<String> supplier) {
        this.info(marker, supplier.get());
    }

    @Override
    public void warn(String line) {
        this.delegate.warning(this.colorizer.apply(line));
    }

    @Override
    public void warn(Collection<String> lines) {
        lines.forEach(this::info);
    }

    @Override
    public void warn(Supplier<String> supplier) {
        this.warn(supplier.get());
    }

    @Override
    public void warn(String marker, String line) {
        this.delegate.warning(this.colorizer.apply(this.preprocessor.apply(marker, line)));
    }

    @Override
    public void warn(String marker, Collection<String> lines) {
        lines.forEach(l -> this.warn(marker, l));
    }

    @Override
    public void warn(String marker, Supplier<String> supplier) {
        this.warn(marker, supplier.get());
    }

    @Override
    public void error(String line) {
        this.delegate.severe(this.colorizer.apply(line));
    }

    @Override
    public void error(Collection<String> lines) {
        lines.forEach(this::info);
    }

    @Override
    public void error(Supplier<String> supplier) {
        this.warn(supplier.get());
    }

    @Override
    public void error(String marker, String line) {
        this.delegate.severe(this.colorizer.apply(this.preprocessor.apply(marker, line)));
    }

    @Override
    public void error(String marker, Collection<String> lines) {
        lines.forEach(l -> this.error(marker, l));
    }

    @Override
    public void error(String marker, Supplier<String> supplier) {
        this.error(marker, supplier.get());
    }

    public void debug(String line) {
        this.delegate.info(this.colorizer.apply("Debug - " + line));
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
        this.delegate.info(this.colorizer.apply(this.preprocessor.apply("Debug - " + marker, line)));
    }

    @Override
    public void debug(String marker, Collection<String> lines) {
        lines.forEach(l -> this.debug(marker, l));
    }

    @Override
    public void debug(String marker, Supplier<String> supplier) {
        this.debug(marker, supplier.get());
    }

}