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

package net.impactdev.impactor.core.commands.economy;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.processing.CommandContainer;
import net.impactdev.impactor.api.commands.CommandSource;
import net.impactdev.impactor.api.economy.EconomyService;
import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.economy.transactions.EconomyTransaction;
import net.impactdev.impactor.api.economy.transactions.details.EconomyTransactionType;
import net.impactdev.impactor.api.items.ImpactorItemStack;
import net.impactdev.impactor.api.items.extensions.SkullStack;
import net.impactdev.impactor.api.platform.sources.PlatformSource;
import net.impactdev.impactor.api.utility.Context;
import net.impactdev.impactor.core.economy.context.TransactionContext;
import net.impactdev.impactor.core.translations.internal.ImpactorTranslations;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;

@CommandContainer
@CommandPermission("impactor.commands.economy.base")
public final class EconomyCommands {

    @CommandMethod("economy|eco balance [target] [currency]")
    @CommandPermission("impactor.commands.economy.balance")
    public void balance(final @NotNull CommandSource source, @Nullable @Argument("target") PlatformSource target, @Nullable @Argument("currency") Currency currency) {
        EconomyService service = EconomyService.instance();
        Currency c = currency != null ? currency : service.currencies().primary();
        PlatformSource focus = target != null ? target : source.source();

        Account account = service.account(c, focus.uuid()).join();

        Context context = Context.empty();
        context.append(Currency.class, c);
        context.append(Account.class, account);
        ImpactorTranslations.ECONOMY_BALANCE.send(source, context);
    }

    @CommandMethod("economy|eco withdraw <amount> [target] [currency]")
    @CommandPermission("impactor.commands.economy.withdraw")
    public void withdraw(
            final @NotNull CommandSource source,
            @Argument("amount") double amount,
            @Nullable @Argument("target") PlatformSource target,
            @Nullable @Argument("currency") Currency currency
    ) {
        EconomyService service = EconomyService.instance();
        Currency c = currency != null ? currency : service.currencies().primary();
        PlatformSource focus = target != null ? target : source.source();

        if(!service.hasAccount(c, focus.uuid()).join()) {
            throw new IllegalArgumentException(
                    (target != null ? "The target does " : "You do ") + "not have an account for the target currency..."
            );
        }

        Account account = service.account(c, focus.uuid()).join();
        BigDecimal before = account.balance();

        EconomyTransaction transaction = account.withdraw(new BigDecimal(amount));
        if(!transaction.successful()) {
            throw new RuntimeException("The transaction failed with reason: " + transaction.result().name());
        }

//        BigDecimal after = account.balance();
//        Component message = text("Transaction completed successfully!");
//        Component hover = text("Transaction Type: ")
//                .append(text(transaction.type().name()).color(NamedTextColor.YELLOW))
//                .append(newline())
//                .append(text("Before: ").color(NamedTextColor.GRAY))
//                .append(c.format(before).color(NamedTextColor.GREEN))
//                .append(newline())
//                .append(text("After: ").color(NamedTextColor.GRAY))
//                .append(c.format(after).color(NamedTextColor.GREEN));
//
//        message = message.hoverEvent(HoverEvent.showText(hover));
//
//        source.sendMessage(message);

        Context context = Context.empty();
        context.append(TransactionContext.class, new TransactionContext(EconomyTransactionType.WITHDRAW, before, account.balance()));
        context.append(Currency.class, c);
        context.append(Account.class, account);

        ImpactorTranslations.ECONOMY_WITHDRAW.send(source, context);
    }
}
