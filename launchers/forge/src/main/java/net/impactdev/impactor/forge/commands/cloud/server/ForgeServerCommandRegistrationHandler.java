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

package net.impactdev.impactor.forge.commands.cloud.server;

import cloud.commandframework.Command;
import cloud.commandframework.arguments.StaticArgument;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import net.impactdev.impactor.core.plugin.BaseImpactorPlugin;
import net.impactdev.impactor.forge.commands.cloud.ForgeCloudCommandManager;
import net.impactdev.impactor.forge.commands.cloud.ForgeCommandRegistrationHandler;
import net.impactdev.impactor.forge.commands.cloud.ForgeExecutor;
import net.impactdev.impactor.forge.mixins.cloud.CommandSelectionAccessor_Cloud;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ForgeServerCommandRegistrationHandler<C> extends ForgeCommandRegistrationHandler<C, CommandSourceStack> {

    private final Set<Command<C>> registered = ConcurrentHashMap.newKeySet();

    public void initialize(final ForgeCloudCommandManager<C, CommandSourceStack> manager) {
        super.initialize(manager);
    }

    @Override
    public boolean registerCommand(@NonNull Command<?> command) {
        return this.registered.add((Command<C>) command);
    }

    @SuppressWarnings("ConstantConditions")
    public void registerAllCommands(final @NonNull RegisterCommandsEvent event) {
        this.manager().registrationCalled();

        final CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        final CommandSelectionAccessor_Cloud side = (CommandSelectionAccessor_Cloud) (Object) event.getEnvironment();

        for(final Command<C> command : this.registered) {
            final Commands.CommandSelection environment = command.getCommandMeta().getOrDefault(
                    ForgeServerCommandManager.META_REGISTRATION_ENVIRONMENT,
                    Commands.CommandSelection.ALL
            );

            if((environment == Commands.CommandSelection.INTEGRATED && !side.integrated()) ||
                    (environment == Commands.CommandSelection.DEDICATED && !side.dedicated()))
            {
                continue;
            }

            this.registerCommand(dispatcher.getRoot(), command);
        }
    }

    private void registerCommand(final RootCommandNode<CommandSourceStack> dispatcher, final Command<C> command) {
        final StaticArgument<C> first = ((StaticArgument<C>) command.getArguments().get(0));
        BaseImpactorPlugin.instance().logger().info("Registering command: " + first.getName());

        final CommandNode<CommandSourceStack> baseNode = this.manager().brigadierManager().createLiteralCommandNode(
                first.getName(),
                command,
                (src, perm) -> this.manager().hasPermission(
                        this.manager().sourceMapper().apply(src),
                        perm
                ),
                true,
                new ForgeExecutor<>(this.manager(), CommandSourceStack::getTextName, CommandSourceStack::sendFailure)
        );

        dispatcher.addChild(baseNode);

        for (final String alias : first.getAlternativeAliases()) {
            dispatcher.addChild(buildRedirect(alias, baseNode));
        }
    }

}
