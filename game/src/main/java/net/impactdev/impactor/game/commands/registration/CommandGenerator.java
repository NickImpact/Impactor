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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.commands.ImpactorCommand;
import net.impactdev.impactor.api.commands.PermissionsService;
import net.impactdev.impactor.api.commands.annotations.Alias;
import net.impactdev.impactor.api.commands.annotations.CommandPath;
import net.impactdev.impactor.api.commands.annotations.permissions.Permission;
import net.impactdev.impactor.api.commands.annotations.permissions.Phase;
import net.impactdev.impactor.game.commands.executors.ExecutorFactory;
import net.impactdev.impactor.game.commands.executors.ImpactorExecutor;
import net.impactdev.impactor.game.commands.specs.CommandRoot;
import net.impactdev.impactor.game.commands.specs.CommandSpec;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Predicate;

import static net.impactdev.impactor.game.commands.AnnotationReader.optional;
import static net.impactdev.impactor.game.commands.AnnotationReader.require;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class CommandGenerator {

    final List<CommandRoot> roots = Lists.newArrayList();
    final Multimap<String, LiteralArgumentBuilder<CommandSourceStack>> redirects = ArrayListMultimap.create();

    public void generate(ImpactorCommand command, @Nullable CommandPath path) {
        Alias alias = require(command, Alias.class);
        ArgumentBuilder<CommandSourceStack, ?> builder = command instanceof ImpactorCommand.Argument ?
                this.createArgument((ImpactorCommand.Argument<?>) command, alias.value()) :
                this.createLiteral(alias.value());

        Predicate<CommandSourceStack> requirement = null;
        Optional<Permission> permission = optional(command, Permission.class);
        if(permission.isPresent() && permission.get().phase().equals(Phase.LOOKUP)) {
            requirement = source -> Impactor.instance().services().provide(PermissionsService.class).hasPermission(source, permission.get().value());
        }

        Optional.ofNullable(Optional.ofNullable(requirement)
                    .map(r -> Optional.ofNullable(command.requirement()).map(r::and).orElse(r))
                    .orElse(command.requirement()))
                .ifPresent(builder::requires);

        builder.executes(new ImpactorExecutor(ExecutorFactory.create(command), permission.orElse(null)));
        this.register(path, builder);

        if(!(command instanceof ImpactorCommand.Argument)) {
            for(String key : alias.redirects()) {
                StringJoiner joiner = new StringJoiner(" ");
                if(path != null) {
                    joiner.add(path.value());
                }

                joiner.add(alias.value());
                this.redirects.put(joiner.toString(), this.createLiteral(key));
            }
        }
    }

    private LiteralArgumentBuilder<CommandSourceStack> createLiteral(final String key) {
        return literal(key);
    }

    private RequiredArgumentBuilder<CommandSourceStack, ?> createArgument(final ImpactorCommand.Argument<?> parent, final String key) {
        return argument(key, parent.type());
    }

    private void register(@Nullable CommandPath path, ArgumentBuilder<CommandSourceStack, ?> builder) {
        String[] keys = path != null ? path.value().split(" ") : new String[]{};
        if(keys.length < 1) {
            if(builder instanceof LiteralArgumentBuilder) {
                this.roots.add(new CommandRoot((LiteralArgumentBuilder<CommandSourceStack>) builder));
            } else {
                throw new IllegalArgumentException("Tried to create a root command via an argument");
            }
        } else {
            CommandSpec<?> parent = this.roots.stream()
                    .map(root -> root.findNode(keys))
                    .filter(Optional::isPresent)
                    .map(optional -> optional.orElse(null))
                    .findFirst()
                    .orElse(null);

            if(parent == null) {
                LiteralArgumentBuilder<CommandSourceStack> root = literal(keys[0]);
                CommandRoot generated = new CommandRoot(root);

                CommandSpec<?> leaf = generated;
                for(int i = 1; i < keys.length; i++) {
                    leaf = leaf.append(literal(keys[i]));
                }

                leaf.append(builder);
                this.roots.add(generated);
            } else {
                parent.append(builder);
            }
        }
    }

}
