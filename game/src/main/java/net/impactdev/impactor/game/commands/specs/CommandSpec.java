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

package net.impactdev.impactor.game.commands.specs;

import com.google.common.collect.Lists;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;

import java.util.List;

public abstract class CommandSpec<B extends ArgumentBuilder<CommandSourceStack, B>> {

    protected final B root;
    private final List<CommandSpec<?>> children = Lists.newArrayList();

    protected CommandSpec(B root) {
        this.root = root;
    }

    public abstract String key();

    public List<CommandSpec<?>> children() {
        return this.children;
    }

    public CommandSpec<?> append(ArgumentBuilder<CommandSourceStack, ?> builder) {
        if(builder instanceof LiteralArgumentBuilder) {
            LiteralCommandSpec spec = new LiteralCommandSpec((LiteralArgumentBuilder<CommandSourceStack>) builder);
            this.children.add(spec);

            return spec;
        } else if(builder instanceof RequiredArgumentBuilder) {
            ArgumentCommandSpec<?> spec = new ArgumentCommandSpec<>((RequiredArgumentBuilder<CommandSourceStack, ?>) builder);
            this.children.add(spec);

            return spec;
        }

        throw new UnsupportedOperationException("Unknown argument builder type: " + builder.getClass().getSimpleName());
    }

    public B build() {
        this.children.forEach(spec -> {
            ArgumentBuilder<CommandSourceStack, ?> child = spec.build();
            this.root.then(child);
        });

        return this.root;
    }

    public static class LiteralCommandSpec extends CommandSpec<LiteralArgumentBuilder<CommandSourceStack>> {

        public LiteralCommandSpec(LiteralArgumentBuilder<CommandSourceStack> root) {
            super(root);
        }

        @Override
        public String key() {
            return this.root.getLiteral();
        }

    }

    public static class ArgumentCommandSpec<T> extends CommandSpec<RequiredArgumentBuilder<CommandSourceStack, T>> {

        public ArgumentCommandSpec(RequiredArgumentBuilder<CommandSourceStack, T> root) {
            super(root);
        }

        @Override
        public String key() {
            return this.root.getName();
        }

    }
}
