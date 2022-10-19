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

package net.impactdev.impactor.commands.dev.ui;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.impactdev.impactor.api.commands.ImpactorCommand;
import net.impactdev.impactor.api.commands.annotations.Alias;
import net.impactdev.impactor.api.commands.annotations.CommandPath;
import net.impactdev.impactor.api.commands.executors.CommandContext;
import net.impactdev.impactor.api.commands.executors.CommandResult;
import net.impactdev.impactor.api.items.ImpactorItemStack;
import net.impactdev.impactor.api.items.types.ItemTypes;
import net.impactdev.impactor.api.ui.containers.Icon;
import net.impactdev.impactor.api.ui.containers.layouts.ChestLayout;
import net.impactdev.impactor.api.ui.containers.views.ChestView;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

@CommandPath("impactor ui")
@Alias("chest")
public class ChestViewTest implements ImpactorCommand {
    @Override
    public @NotNull CommandResult execute(CommandContext context) throws CommandSyntaxException {
        ChestView view = ChestView.builder()
                .provider(Key.key("impactor", "fabric-test"))
                .title(Component.text("1.18.2 Test Chest View").color(TextColor.color(0x00ffff)))
                .layout(ChestLayout.builder()
                        .size(5)
                        .border(Icon.builder()
                                .display(() -> ImpactorItemStack.basic()
                                        .type(ItemTypes.BLACK_STAINED_GLASS_PANE)
                                        .title(Component.empty())
                                        .build())
                                .build()
                        )
                        .center(Icon.builder()
                                .display(() -> ImpactorItemStack.basic()
                                        .type(ItemTypes.DIAMOND)
                                        .title(Component.text("This is a test item!").color(TextColor.color(0x0055ff)))
                                        .glow()
                                        .build()
                                )
                                .build()
                        )
                        .build())
                .build();

        view.open(context.source().requirePlayer());
        return CommandResult.successful();
    }
}
