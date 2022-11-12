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

package net.impactdev.impactor.commands.builtin.economy;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.commands.ImpactorCommand;
import net.impactdev.impactor.api.commands.annotations.Alias;
import net.impactdev.impactor.api.commands.annotations.CommandPath;
import net.impactdev.impactor.api.commands.executors.CommandContext;
import net.impactdev.impactor.api.commands.executors.CommandResult;
import net.impactdev.impactor.api.services.economy.EconomyService;
import net.impactdev.impactor.api.services.economy.accounts.Account;
import net.impactdev.impactor.api.services.economy.accounts.AccountAccessor;
import net.impactdev.impactor.api.services.economy.currency.Currency;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;

@CommandPath("baltop")
@Alias("currency")
public class EconomyBalTopCommand implements ImpactorCommand.Argument<String> {

    @Override
    public @NotNull CommandResult execute(CommandContext context) throws CommandSyntaxException {
        EconomyService service = Impactor.instance().services().provide(EconomyService.class);

        @SuppressWarnings({"PatternValidation", "OptionalGetWithoutIsPresent"})
        Currency target = service.currencies().currency(Key.key(
                "impactor", context.argument("currency", String.class).toLowerCase()
        )).get();

        context.source().sendMessage(text("Composing top balances...").color(NamedTextColor.GRAY));
        service.accessors().thenAccept(accessors -> {
            List<AccountAccessor> top5 = accessors.stream()
                    .sorted((a1, a2) -> {
                        Account a = a1.account(target).join();
                        Account b = a2.account(target).join();

                        return b.balance().compareTo(a.balance());
                    })
                    .limit(5)
                    .toList();

            context.source().sendMessage(text("+============ Top Balances ============+").color(NamedTextColor.YELLOW));
            for(int i = 0; i < top5.size(); i++) {
                Account account = top5.get(i).account(target).join();
                context.source().sendMessage(
                        text(i + 1).append(text(")")).color(NamedTextColor.GRAY)
                                .append(space())
                                .append(target.format(account.balance()).color(NamedTextColor.GREEN))
                );
            }
        });

        return CommandResult.successful();
    }

    @Override
    public ArgumentType<String> type() {
        return StringArgumentType.word();
    }

}
