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

package net.impactdev.impactor.test;

import com.google.common.collect.Sets;
import net.impactdev.impactor.core.commands.ImpactorCommandRegistry;
import net.impactdev.impactor.core.modules.ImpactorModule;
import net.impactdev.impactor.core.plugin.BaseImpactorPlugin;
import net.impactdev.impactor.core.plugin.ImpactorBootstrapper;
import net.impactdev.impactor.test.dummies.TestCommandsModule;
import net.impactdev.impactor.test.dummies.TestPlatform;
import net.impactdev.impactor.test.dummies.TestScheduler;

import java.util.Set;

public class TestPlugin extends BaseImpactorPlugin {

    public TestPlugin(ImpactorBootstrapper bootstrapper) {
        super(bootstrapper);
    }

    @Override
    protected Set<Class<? extends ImpactorModule>> modules() {
        return Sets.newHashSet(
                TestPlatform.TestPlatformModule.class,
                TestScheduler.TestSchedulerModule.class,
                TestCommandsModule.class
        );
    }

}
