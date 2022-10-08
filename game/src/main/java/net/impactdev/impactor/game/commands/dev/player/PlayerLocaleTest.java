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

package net.impactdev.impactor.game.commands.dev.player;

import net.impactdev.impactor.api.commands.ImpactorCommand;
import net.impactdev.impactor.api.commands.annotations.Alias;
import net.impactdev.impactor.api.commands.annotations.CommandPath;
import net.impactdev.impactor.api.commands.annotations.RestrictedExecutor;
import net.impactdev.impactor.api.commands.executors.CommandResult;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.utilities.context.Context;
import net.impactdev.impactor.plugin.BaseImpactorPlugin;
import net.kyori.adventure.text.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

@CommandPath("impactor players")
@Alias("locale")
@RestrictedExecutor(system = false)
public final class PlayerLocaleTest implements ImpactorCommand {

    @Override
    public @NotNull CommandResult execute(Context context) {
        ServerPlayer source = context.require(ServerPlayer.class);
        PlatformPlayer player = PlatformPlayer.getOrCreate(source.getUUID());
        player.sendMessage(Component.text("Using locale: " + player.locale().getDisplayName()));
        BaseImpactorPlugin.instance().logger().info("Using locale: " + player.locale().toLanguageTag());

        return CommandResult.successful();
    }

}
