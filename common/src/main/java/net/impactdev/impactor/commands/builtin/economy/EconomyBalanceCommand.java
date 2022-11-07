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

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.commands.ImpactorCommand;
import net.impactdev.impactor.api.commands.annotations.Alias;
import net.impactdev.impactor.api.commands.annotations.permissions.Permission;
import net.impactdev.impactor.api.commands.executors.CommandContext;
import net.impactdev.impactor.api.commands.executors.CommandResult;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.platform.players.PlatformSource;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;
import net.impactdev.impactor.api.services.economy.EconomyService;
import net.impactdev.impactor.api.services.economy.accounts.Account;
import net.impactdev.impactor.api.services.economy.accounts.AccountAccessor;
import net.impactdev.impactor.api.services.economy.currency.Currency;
import net.impactdev.impactor.api.utilities.ExceptionPrinter;
import net.impactdev.impactor.commands.exceptions.RequirePlayerSyntaxExceptionType;
import net.impactdev.impactor.plugin.BaseImpactorPlugin;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static net.kyori.adventure.text.Component.text;

@Alias("bal")
@Permission("impactor.commands.economy.balance")
public class EconomyBalanceCommand implements ImpactorCommand {

    private static final RequirePlayerSyntaxExceptionType EXCEPTION = new RequirePlayerSyntaxExceptionType();

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public @NotNull CommandResult execute(CommandContext context) throws CommandSyntaxException {
        CompletableFuture<Map<Currency, Account>> accounts = context.source().asPlayer()
                .map(PlatformPlayer::accountAccessor)
                .map(AccountAccessor::accounts)
                .orElseThrow(EXCEPTION::create);

        context.source().sendMessage(text("Fetching your balances...").color(NamedTextColor.GRAY));
        accounts.thenAccept(in -> {
            if(in.isEmpty()) {
                EconomyService service = Impactor.instance().services().provide(EconomyService.class);
                AccountAccessor accessor = context.source().asPlayer().get().accountAccessor();

                BaseImpactorPlugin.instance().logger().info("Number of currencies registered: " + service.currencies().registered().size());
                for(Currency currency : service.currencies().registered()) {
                    in.put(currency, service.account(accessor, currency).join());
                }
            }

            context.source().sendMessage(text("+============ Balances ============+").color(NamedTextColor.YELLOW));
            for(Map.Entry<Currency, Account> entry : in.entrySet()) {
                Currency currency = entry.getKey();
                Account account = entry.getValue();

                context.source().sendMessage(currency.plural().append(text(": ").append(account.currency().format(account.balance()))));
            }
        }).exceptionally(e -> {
            ExceptionPrinter.print(BaseImpactorPlugin.instance(), e);
            context.source().sendMessage(text("Exception: ").append(text(e.getMessage())).color(NamedTextColor.RED));

            return null;
        });

        return CommandResult.successful();
    }

}
