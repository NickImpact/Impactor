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

package net.impactdev.impactor.core.text.placeholders.provided;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.economy.EconomyService;
import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.economy.transactions.details.EconomyTransactionType;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.platform.players.PlatformPlayerService;
import net.impactdev.impactor.api.platform.sources.PlatformSource;
import net.impactdev.impactor.api.text.TextProcessor;
import net.impactdev.impactor.api.text.placeholders.PlaceholderArguments;
import net.impactdev.impactor.api.translations.metadata.LanguageInfo;
import net.impactdev.impactor.core.economy.context.TransactionContext;
import net.impactdev.impactor.core.economy.context.TransferTransactionContext;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Optional;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;

@SuppressWarnings("unused")
public final class ImpactorPlaceholders {

    private static final DecimalFormat TWO_DECIMALS = new DecimalFormat("0.00");
    private static final DecimalFormat THREE_DECIMALS = new DecimalFormat("0.000");

    public static final ImpactorPlaceholder NAME = new ImpactorPlaceholder(
            impactor("name"),
            (viewer, ctx) -> ctx.request(PlatformSource.class)
                    .or(() -> ctx.request(PlatformPlayer.class))
                    .or(() -> ctx.request(PlatformSource.SOURCE))
                    .or(() -> ctx.request(PlatformPlayer.PLAYER))
                    .or(() -> Optional.ofNullable(viewer))
                    .map(PlatformSource::name)
                    .orElse(Component.empty())
    );

    public static final ImpactorPlaceholder UUID = new ImpactorPlaceholder(
            impactor("uuid"),
            (viewer, ctx) -> ctx.request(PlatformSource.class)
                    .or(() -> ctx.request(PlatformPlayer.class))
                    .or(() -> ctx.request(PlatformSource.SOURCE))
                    .or(() -> ctx.request(PlatformPlayer.PLAYER))
                    .or(() -> Optional.ofNullable(viewer))
                    .map(PlatformSource::uuid)
                    .map(uuid -> text(uuid.toString()))
                    .orElse(Component.empty())
    );

    public static final ImpactorPlaceholder TPS = new ImpactorPlaceholder(
            impactor("tps"),
            (viewer, ctx) -> text(TWO_DECIMALS.format(Impactor.instance().platform().performance().ticksPerSecond()))
    );
    public static final ImpactorPlaceholder MSPT = new ImpactorPlaceholder(
            impactor("mspt"),
            (viewer, ctx) -> text(THREE_DECIMALS.format(Impactor.instance().platform().performance().averageTickDuration()))
    );

    public static final ImpactorPlaceholder MEMORY_USAGE = new ImpactorPlaceholder(
            impactor("memory_used"),
            (viewer, ctx) -> text(Impactor.instance().platform().performance().memory().current())
    );
    public static final ImpactorPlaceholder MEMORY_ALLOCATED = new ImpactorPlaceholder(
            impactor("memory_allocated"),
            (viewer, ctx) -> text(Impactor.instance().platform().performance().memory().allocated())
    );
    public static final ImpactorPlaceholder MEMORY_TOTAL = new ImpactorPlaceholder(
            impactor("memory_total"),
            (viewer, ctx) -> text(Impactor.instance().platform().performance().memory().max())
    );
    public static final ImpactorPlaceholder ECONOMY_ACCOUNT = new ImpactorPlaceholder(
            impactor("account"),
            (viewer, ctx) -> {
                PlaceholderArguments arguments = ctx.require(PlaceholderArguments.class);
                if(!arguments.hasNext()) {
                    return empty();
                }

                Account account = ctx.require(Account.class);
                String option = arguments.pop();
                switch (option) {
                    case "balance":
                        // TODO - Argument support for short or long
                        return account.currency().format(account.balance());
                    case "name":
                        PlatformSource source = PlatformSource.factory().fromID(account.owner());
                        return source.name();
                    case "uuid":
                        return text(account.owner().toString());
                }

                return empty();
            }
    );
    public static final ImpactorPlaceholder ECONOMY_CURRENCY = new ImpactorPlaceholder(
            impactor("currency"),
            // TODO - Argument support for singular or plural
            (viewer, ctx) -> ctx.request(Currency.class).orElse(EconomyService.instance().currencies().primary()).plural()
    );
    public static final ImpactorPlaceholder ECONOMY_BALTOP = new ImpactorPlaceholder(
            impactor("baltop"),
            (viewer, ctx) -> {
                PlaceholderArguments arguments = ctx.require(PlaceholderArguments.class);
                if(!arguments.hasNext()) {
                    return empty();
                }

                switch (arguments.pop()) {
                    case "ranking":
                        return text(ctx.require(Integer.class));
                }

                return empty();
            }
    );
    public static final ImpactorPlaceholder ECONOMY_TRANSACTION = new ImpactorPlaceholder(
            impactor("economy_transaction"),
            (viewer, ctx) -> {
                PlaceholderArguments arguments = ctx.require(PlaceholderArguments.class);
                if(!arguments.hasNext()) {
                    return empty();
                }

                Currency currency = ctx.require(Currency.class);
                Optional<TransactionContext> transaction = ctx.request(TransactionContext.class);
                if(transaction.isPresent()) {
                    TransactionContext context = transaction.get();

                    String option = arguments.pop();
                    switch (option) {
                        case "type":
                            return text(context.type().name());
                        case "before":
                            return currency.format(context.before());
                        case "after":
                            return currency.format(context.after());
                        case "reason":
                            return text(context.result().name());
                    }
                } else {
                    TransferTransactionContext context = ctx.require(TransferTransactionContext.class);

                    String option = arguments.pop();
                    switch (option) {
                        case "type":
                            return text(EconomyTransactionType.TRANSFER.name());
                        case "result":
                            return text(context.result().name());
                        case "source":
                            String st = arguments.pop();
                            switch (st) {
                                case "before" -> {
                                    return currency.format(context.from().before());
                                }
                                case "after" -> {
                                    return currency.format(context.from().after());
                                }
                            }
                        case "recipient":
                            String rt = arguments.pop();
                            switch (rt) {
                                case "before" -> {
                                    return currency.format(context.to().before());
                                }
                                case "after" -> {
                                    return currency.format(context.to().after());
                                }
                            }
                    }
                }

                return empty();
            }
    );
    public static final ImpactorPlaceholder PAYMENT = new ImpactorPlaceholder(
            impactor("payment"),
            (viewer, ctx) -> {
                Currency currency = ctx.require(Currency.class);
                BigDecimal amount = ctx.require(BigDecimal.class);
                return currency.format(amount);
            }
    );
    public static final ImpactorPlaceholder LANGUAGE = new ImpactorPlaceholder(
            impactor("language"),
            (viewer, ctx) -> {
                PlaceholderArguments arguments = ctx.require(PlaceholderArguments.class);
                if(!arguments.hasNext()) {
                    return empty();
                }

                LanguageInfo info = ctx.require(LanguageInfo.class);
                switch (arguments.pop()) {
                    case "id":
                        return text(info.locale().toLanguageTag());
                    case "name":
                        return text(info.locale().getDisplayName());
                    case "progress":
                        int progress = info.progress();
                        return TextProcessor.mini().parse("<transition:red:yellow:green:" + progress / 100.0 + ">" + progress + "%");
                    case "contributor":
                        String contributor = ctx.require(String.class);
                        return text(contributor);
                }

                return empty();
            }
    );

    private static Key impactor(@Subst("dummy") @Pattern("[a-z0-9_\\-./]+") String key) {
        return Key.key("impactor", key);
    }

}
