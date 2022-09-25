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

package net.impactdev.impactor.game.commands.dev.items.skulls;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.impactdev.impactor.api.commands.ImpactorCommand;
import net.impactdev.impactor.api.commands.annotations.Alias;
import net.impactdev.impactor.api.commands.annotations.CommandPath;
import net.impactdev.impactor.api.commands.annotations.Permission;
import net.impactdev.impactor.api.commands.annotations.RestrictedExecutor;
import net.impactdev.impactor.api.commands.executors.CommandExecutor;
import net.impactdev.impactor.api.commands.executors.CommandResult;
import net.impactdev.impactor.api.items.ImpactorItemStack;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import static net.kyori.adventure.text.Component.text;

@CommandPath("impactor items skull name")
@Alias("value")
@Permission("impactor.commands.dev.items")
@RestrictedExecutor(system = false)
public class SkullSkinArgument implements ImpactorCommand.Argument<String> {

    @Override
    public ArgumentType<String> type() {
        return StringArgumentType.word();
    }

    @Override
    public CommandExecutor executor() {
        return ctx -> {
            CommandContext<CommandSourceStack> context = ctx.require(CommandExecutor.COMMAND_CONTEXT);
            ServerPlayer source = ctx.require(ServerPlayer.class);

            ImpactorItemStack skull = ImpactorItemStack.skull()
                    .title(text("Impactor Skull Test").color(TextColor.color(0x42, 0x87, 0xf5)))
                    .glow()
                    .unbreakable()
                    .player()
                    .of(context.getArgument("value", String.class))
                    .complete()
                    .build();

            ItemStack minecraft = skull.asMinecraftNative();
            source.inventory.add(minecraft);
            source.inventoryMenu.broadcastChanges();

            return CommandResult.successful();
        };
    }
}
