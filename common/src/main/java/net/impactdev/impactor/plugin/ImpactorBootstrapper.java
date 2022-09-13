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

package net.impactdev.impactor.plugin;

import net.impactdev.impactor.api.logging.PluginLogger;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import net.impactdev.impactor.util.ExceptionPrinter;

public abstract class ImpactorBootstrapper {

    private final ImpactorPlugin plugin;
    private final PluginLogger logger;

    public ImpactorBootstrapper(PluginLogger logger) {
        this.logger = logger;
        this.plugin = this.createPlugin();
    }

    public PluginLogger logger() {
        return this.logger;
    }

    protected abstract ImpactorPlugin createPlugin();

    public void construct() {
        try {
            this.plugin.construct();
        } catch (Exception e) {
            ExceptionPrinter.print(this.plugin, e);
        }
    }

    public void shutdown() {
        try {
            this.plugin.shutdown();
        } catch (Exception e) {
            ExceptionPrinter.print(this.plugin, e);
        }
    }
}
