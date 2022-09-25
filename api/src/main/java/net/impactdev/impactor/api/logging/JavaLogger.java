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

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaLogger implements PluginLogger {

    private final ImpactorPlugin plugin;
    private final Logger delegate;
    private final Function<String, String> colorizer = in -> in.replaceAll("&", "\u00a7");
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
    public void warn(String line) {
        this.delegate.warning(this.colorizer.apply(line));
    }

    @Override
    public void warn(String line, Throwable throwable) {
        this.delegate.log(Level.WARNING, line, throwable);
    }

    @Override
    public void severe(String line) {
        this.delegate.severe(this.colorizer.apply(line));
    }

    @Override
    public void severe(String line, Throwable throwable) {
        this.delegate.log(Level.SEVERE, line, throwable);
    }

    public void debug(String line) {
        this.delegate.info(this.colorizer.apply("Debug - " + line));
    }

}
