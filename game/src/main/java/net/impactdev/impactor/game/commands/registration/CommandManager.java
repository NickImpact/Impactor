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

package net.impactdev.impactor.game.commands.registration;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import net.impactdev.impactor.api.commands.ImpactorCommand;
import net.impactdev.impactor.api.commands.annotations.CommandPath;
import net.impactdev.impactor.game.commands.specs.CommandRoot;
import net.minecraft.commands.CommandSourceStack;

import java.util.Map;
import java.util.Optional;

import static net.impactdev.impactor.game.commands.AnnotationReader.optional;

public class CommandManager {

    private final CommandGenerator generator = new CommandGenerator();

    public void register(ImpactorCommand command) {
        Optional<CommandPath> path = optional(command, CommandPath.class);

        if(!path.isPresent() && command instanceof ImpactorCommand.Argument) {
            throw new IllegalArgumentException("Only literal arguments can be the root of a command");
        }

        this.generator.generate(command, path.orElse(null));
    }

    public void registerWithBrigadier(CommandDispatcher<CommandSourceStack> dispatcher) {
        for(CommandRoot details : this.generator.roots) {
            dispatcher.register(details.build());
        }

        for(Map.Entry<String, LiteralArgumentBuilder<CommandSourceStack>> entry : this.generator.redirects.entries()) {
            String[] path = entry.getKey().split(" ");

            CommandNode<CommandSourceStack> parent = dispatcher.getRoot();
            CommandNode<CommandSourceStack> target = dispatcher.getRoot().getChild(path[0]);
            for(int i = 1; i < path.length; i++) {
                parent = target;
                target = target.getChild(path[i]);
            }

            LiteralArgumentBuilder<CommandSourceStack> redirect = entry.getValue();
            redirect.requires(target.getRequirement());
            redirect.forward(target.getRedirect(), target.getRedirectModifier(), target.isFork());
            redirect.executes(target.getCommand());
            for(CommandNode<CommandSourceStack> child : target.getChildren()) {
                redirect.then(child);
            }

            parent.addChild(redirect.build());
        }

        this.generator.roots.clear();
        this.generator.redirects.clear();
    }

}
