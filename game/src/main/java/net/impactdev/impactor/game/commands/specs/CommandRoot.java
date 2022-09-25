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

import com.google.common.base.Preconditions;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CommandRoot extends CommandSpec.LiteralCommandSpec {

    public CommandRoot(LiteralArgumentBuilder<CommandSourceStack> root) {
        super(root);
    }

    public Optional<CommandSpec<?>> findNode(final String[] keys) {
        Preconditions.checkArgument(keys.length > 0);

        return Optional.ofNullable(this.findNodeRecursively(keys, 0, this));
    }

    @Nullable
    private CommandSpec<?> findNodeRecursively(final String[] path, final int index, final CommandSpec<?> current) {
        if(!path[index].equals(current.key())) {
            return null;
        }

        if(path.length - 1 == index) {
            return current;
        }

        if(!current.children().isEmpty()) {
            for(CommandSpec<?> child : current.children()) {
                CommandSpec<?> spec = this.findNodeRecursively(path, index + 1, child);
                if(spec != null) {
                    return spec;
                }
            }
        }

        return null;
    }

}
