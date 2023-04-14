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
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.processing.CommandContainer;
import com.google.common.base.Preconditions;
import net.impactdev.impactor.api.commands.CommandSource;
import net.impactdev.impactor.api.economy.EconomyService;
import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.economy.transactions.EconomyTransaction;
import net.impactdev.impactor.api.economy.transactions.EconomyTransferTransaction;
import net.impactdev.impactor.api.economy.transactions.details.EconomyTransactionType;
import net.impactdev.impactor.api.platform.sources.PlatformSource;
import net.impactdev.impactor.api.utility.Context;
import net.impactdev.impactor.core.economy.context.TransactionContext;
import net.impactdev.impactor.core.economy.context.TransferTransactionContext;
import net.impactdev.impactor.core.translations.internal.ImpactorTranslations;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

@SuppressWarnings({"DuplicatedCode", "unused"})
@CommandContainer
@CommandPermission("impactor.commands.economy.base")
public final class EconomyCommands {

    @CommandMethod("economy|eco balance [currency] [target]")
    @CommandPermission("impactor.commands.economy.balance")
    @CommandDescription("Fetches the balance of the source or identified target")
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

    @CommandMethod("economy|eco withdraw <amount> [currency] [target]")
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

        Account account = service.account(c, focus.uuid()).join();
        BigDecimal before = account.balance();

        EconomyTransaction transaction = account.withdraw(new BigDecimal(amount));
        if(!transaction.successful()) {
            throw new RuntimeException("The transaction failed with reason: " + transaction.result().name());
        }

        Context context = Context.empty();
        context.append(TransactionContext.class, new TransactionContext(EconomyTransactionType.WITHDRAW, before, account.balance()));
        context.append(Currency.class, c);
        context.append(Account.class, account);

        ImpactorTranslations.ECONOMY_TRANSACTION.send(source, context);
    }

    @CommandMethod("economy|eco deposit <amount> [currency] [target]")
    @CommandPermission("impactor.commands.economy.deposit")
    public void deposit(
            final @NotNull CommandSource source,
            @Argument("amount") double amount,
            @Nullable @Argument("target") PlatformSource target,
            @Nullable @Argument("currency") Currency currency
    ) {
        EconomyService service = EconomyService.instance();
        Currency c = currency != null ? currency : service.currencies().primary();
        PlatformSource focus = target != null ? target : source.source();

        Account account = service.account(c, focus.uuid()).join();
        BigDecimal before = account.balance();

        EconomyTransaction transaction = account.deposit(new BigDecimal(amount));
        if(!transaction.successful()) {
            throw new RuntimeException("The transaction failed with reason: " + transaction.result().name());
        }

        Context context = Context.empty();
        context.append(TransactionContext.class, new TransactionContext(EconomyTransactionType.DEPOSIT, before, account.balance()));
        context.append(Currency.class, c);
        context.append(Account.class, account);
        ImpactorTranslations.ECONOMY_TRANSACTION.send(source, context);
    }

    @CommandMethod("economy|eco set <amount> [currency] [target]")
    @CommandPermission("impactor.commands.economy.set")
    public void set(
            final @NotNull CommandSource source,
            @Argument("amount") double amount,
            @Nullable @Argument("target") PlatformSource target,
            @Nullable @Argument("currency") Currency currency
    ) {
        EconomyService service = EconomyService.instance();
        Currency c = currency != null ? currency : service.currencies().primary();
        PlatformSource focus = target != null ? target : source.source();

        Account account = service.account(c, focus.uuid()).join();
        BigDecimal before = account.balance();

        EconomyTransaction transaction = account.deposit(new BigDecimal(amount));
        if(!transaction.successful()) {
            throw new RuntimeException("The transaction failed with reason: " + transaction.result().name());
        }

        Context context = Context.empty();
        context.append(TransactionContext.class, new TransactionContext(EconomyTransactionType.SET, before, account.balance()));
        context.append(Currency.class, c);
        context.append(Account.class, account);
        ImpactorTranslations.ECONOMY_TRANSACTION.send(source, context);
    }

    @CommandMethod("economy|eco reset [currency] [target]")
    @CommandPermission("impactor.commands.economy.reset")
    public void reset(
            final @NotNull CommandSource source,
            @Nullable @Argument("target") PlatformSource target,
            @Nullable @Argument("currency") Currency currency
    ) {
        EconomyService service = EconomyService.instance();
        Currency c = currency != null ? currency : service.currencies().primary();
        PlatformSource focus = target != null ? target : source.source();

        Account account = service.account(c, focus.uuid()).join();
        BigDecimal before = account.balance();

        EconomyTransaction transaction = account.reset();
        if(!transaction.successful()) {
            throw new RuntimeException("The transaction failed with reason: " + transaction.result().name());
        }

        Context context = Context.empty();
        context.append(TransactionContext.class, new TransactionContext(EconomyTransactionType.RESET, before, account.balance()));
        context.append(Currency.class, c);
        context.append(Account.class, account);
        ImpactorTranslations.ECONOMY_TRANSACTION.send(source, context);
    }

    @CommandMethod("economy|eco pay <amount> <target> [currency] [source]")
    @CommandPermission("impactor.commands.economy.pay")
    public void transfer(
            final @NotNull CommandSource source,
            @Argument("amount") double amount,
            @Argument("target") PlatformSource target,
            @Nullable @Argument("currency") Currency currency,
            @Nullable @Argument("source") PlatformSource from
    ) {
        EconomyService service = EconomyService.instance();
        Currency c = currency != null ? currency : service.currencies().primary();
        PlatformSource focus = from != null ? from : source.source();

        Preconditions.checkArgument(focus != target, "Cannot pay yourself!");

        Account sa = service.account(c, focus.uuid()).join();
        Account ta = service.account(c, target.uuid()).join();

        BigDecimal sb = sa.balance();
        BigDecimal tb = ta.balance();

        EconomyTransferTransaction transaction = sa.transfer(ta, new BigDecimal(amount));
        if(!transaction.successful()) {
            throw new RuntimeException("The transaction failed with reason: " + transaction.result().name());
        }

        Context context = Context.empty();
        context.append(TransferTransactionContext.class, new TransferTransactionContext(
                new TransactionContext(EconomyTransactionType.WITHDRAW, sb, sa.balance()),
                new TransactionContext(EconomyTransactionType.DEPOSIT, tb, ta.balance())
        ));
        context.append(Currency.class, c);

        ImpactorTranslations.ECONOMY_TRANSFER.send(source, context);
    }
}
