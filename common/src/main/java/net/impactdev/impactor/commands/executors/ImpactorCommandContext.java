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

package net.impactdev.impactor.commands.executors;

import com.mojang.brigadier.context.StringRange;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.commands.executors.CommandContext;
import net.impactdev.impactor.api.commands.executors.CommandSource;
import net.impactdev.impactor.commands.sources.SourceTranslator;

public final class ImpactorCommandContext<S> implements CommandContext {

    private final com.mojang.brigadier.context.CommandContext<S> delegate;
    private final CommandSource source;

    public ImpactorCommandContext(com.mojang.brigadier.context.CommandContext<S> delegate) {
        this.delegate = delegate;
        this.source = Impactor.instance().services().provide(SourceTranslator.class).translate(delegate.getSource());
    }

    @Override
    public CommandSource source() {
        return this.source;
    }

    @Override
    public <V> V argument(String name, Class<V> type) {
        return this.delegate.getArgument(name, type);
    }

    @Override
    public StringRange range() {
        return this.delegate.getRange();
    }

    @Override
    public String input() {
        return this.delegate.getInput();
    }

}