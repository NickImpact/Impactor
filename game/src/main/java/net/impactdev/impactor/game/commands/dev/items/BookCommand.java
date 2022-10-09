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

package net.impactdev.impactor.game.commands.dev.items;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.impactdev.impactor.api.commands.ImpactorCommand;
import net.impactdev.impactor.api.commands.annotations.Alias;
import net.impactdev.impactor.api.commands.annotations.CommandPath;
import net.impactdev.impactor.api.commands.annotations.permissions.Permission;
import net.impactdev.impactor.api.commands.annotations.RestrictedExecutor;
import net.impactdev.impactor.api.commands.executors.CommandContext;
import net.impactdev.impactor.api.commands.executors.CommandResult;
import net.impactdev.impactor.api.items.ImpactorItemStack;
import net.impactdev.impactor.api.items.extensions.BookStack;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.platform.players.transactions.ItemTransaction;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

@CommandPath("impactor items")
@Alias("book")
@Permission("impactor.commands.dev.items")
@RestrictedExecutor(system = false)
public class BookCommand implements ImpactorCommand {

    @Override
    public @NotNull CommandResult execute(CommandContext context) throws CommandSyntaxException {
        PlatformPlayer platform = context.source().requirePlayer();

        ImpactorItemStack book = ImpactorItemStack.book()
                .type(BookStack.BookType.WRITTEN)
                .title(text("Impactor Book Test")
                        .style(Style.style().decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE))
                        .color(TextColor.color(0x42, 0x87, 0xf5))
                )
                .author("NickImpact")
                .unbreakable()
                .generation(BookStack.Generation.ORIGINAL)
                .pages(text("Hello World!"))
                .build();

        ItemTransaction transaction = platform.offer(book);
        return transaction.successful() ? CommandResult.successful() : CommandResult.builder().result(0).build();
    }
}
