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

package net.impactdev.impactor.fabric.commands;

import cloud.commandframework.CommandManager;
import cloud.commandframework.fabric.FabricServerCommandManager;
import net.impactdev.impactor.api.commands.CommandSource;
import net.impactdev.impactor.core.commands.ImpactorCommandManager;
import net.impactdev.impactor.minecraft.commands.CommandSourceStackTranslator;
import net.minecraft.commands.CommandSourceStack;

public final class FabricCommandManager extends ImpactorCommandManager<CommandSourceStack> {

    public static void activate() {
        FabricCommandManager manager = new FabricCommandManager();
        manager.initialize();
    }

    @Override
    protected CommandManager<CommandSource> create(Coordinator coordinator) {
        return new FabricServerCommandManager<>(
                coordinator,
                this.impactor(),
                this.platform()
        );
    }

    @Override
    protected ToImpactor<CommandSourceStack> impactor() {
        return CommandSourceStackTranslator::impactor;
    }

    @Override
    protected ToNative<CommandSourceStack> platform() {
        return CommandSourceStackTranslator::minecraft;
    }
}
