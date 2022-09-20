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

package net.impactdev.impactor.sponge;

import com.google.common.collect.Sets;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.utilities.printing.PrettyPrinter;
import net.impactdev.impactor.modules.ImpactorModule;
import net.impactdev.impactor.plugin.BaseImpactorPlugin;
import net.impactdev.impactor.plugin.ImpactorBootstrapper;
import net.impactdev.impactor.sponge.platform.SpongePlatformModule;

import java.util.Set;

public class SpongeImpactorPlugin extends BaseImpactorPlugin {

    public SpongeImpactorPlugin(ImpactorBootstrapper bootstrapper) {
        super(bootstrapper);
    }

    @Override
    public void construct() throws Exception {
        super.construct();

        PrettyPrinter printer = new PrettyPrinter(80);
        printer.title("Platform Information");
        Impactor.instance().platform().info().print(printer);
        printer.log(this.logger(), PrettyPrinter.Level.INFO);
    }

    @Override
    protected Set<Class<? extends ImpactorModule>> modules() {
        return Sets.newHashSet(SpongePlatformModule.class);
    }

}
