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

package net.impactdev.impactor.forge.commands;

import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.ButtonAction;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.ui.ImpactorUI;
import net.impactdev.impactor.api.ui.icons.Icon;
import net.impactdev.impactor.api.ui.layouts.Layout;
import net.impactdev.impactor.forge.ForgeImpactorPlugin;
import net.impactdev.impactor.forge.adventure.RelocationTranslator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CommandProvider {

    @SubscribeEvent
    public void onCommandsRegistry(final RegisterCommandsEvent event) {
        event.getDispatcher().register(LiteralArgumentBuilder.<CommandSource>literal("uitest")
                .executes(context -> {
                    try {
                        ServerPlayerEntity source = context.getSource().getPlayerOrException();
                        Icon<ItemStack> center = Icon.builder(ItemStack.class)
                                .display(GooeyButton.builder()
                                        .display(new ItemStack(Blocks.CHEST))
                                        .title(RelocationTranslator.relocated(MiniMessage.miniMessage()
                                                .deserialize("<gradient:red:green:blue>Cool Test Icon</gradient>")))
                                        .build()
                                        .getDisplay()
                                )
                                .listener(c -> {
                                    ForgeImpactorPlugin.getInstance().getPluginLogger().info("Click detected on icon at slot: " + c.require(ButtonAction.class).getSlot());
                                    return false;
                                })
                                .build();

                        ImpactorUI ui = ImpactorUI.builder()
                                .layout(Layout.builder()
                                        .size(5)
                                        .slot(center, 22)
                                        .build()
                                )
                                .title(Component.text("Impactor/Gooey Test").color(NamedTextColor.DARK_AQUA))
                                .build();
                        ui.open(PlatformPlayer.from(source));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return 0;
                })
        );
    }

}
