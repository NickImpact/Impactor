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
import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.platform.sources.PlatformSource;
import net.impactdev.impactor.api.text.TextProcessor;
import net.impactdev.impactor.api.text.placeholders.PlaceholderArguments;
import net.impactdev.impactor.api.translations.metadata.LanguageInfo;
import net.impactdev.impactor.core.economy.context.TransactionContext;
import net.kyori.adventure.key.Key;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;

import java.text.DecimalFormat;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;

@SuppressWarnings("unused")
public final class ImpactorPlaceholders {

    private static final DecimalFormat TWO_DECIMALS = new DecimalFormat("0.00");
    private static final DecimalFormat THREE_DECIMALS = new DecimalFormat("0.000");

    public static final ImpactorPlaceholder NAME = new ImpactorPlaceholder(
            impactor("name"),
            (viewer, ctx) -> ctx.request(PlatformSource.class)
                    .map(PlatformSource::name)
                    .orElse(empty())
    );

    public static final ImpactorPlaceholder UUID = new ImpactorPlaceholder(
            impactor("uuid"),
            (viewer, ctx) -> ctx.request(PlatformSource.class)
                    .map(PlatformSource::uuid)
                    .map(id -> text(id.toString()))
                    .orElse(empty())
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
                        // TODO
                        return empty();
                    case "uuid":
                        return text(account.owner().toString());
                }

                return empty();
            }
    );
    public static final ImpactorPlaceholder ECONOMY_CURRENCY = new ImpactorPlaceholder(
            impactor("currency"),
            // TODO - Argument support for singular or plural
            (viewer, ctx) -> ctx.require(Currency.class).plural()
    );
    public static final ImpactorPlaceholder ECONOMY_TRANSACTION = new ImpactorPlaceholder(
            impactor("economy_transaction"),
            (viewer, ctx) -> {
                PlaceholderArguments arguments = ctx.require(PlaceholderArguments.class);
                if(!arguments.hasNext()) {
                    return empty();
                }

                Account account = ctx.require(Account.class);
                TransactionContext transaction = ctx.require(TransactionContext.class);

                String option = arguments.pop();
                switch (option) {
                    case "type":
                        return text(transaction.type().name());
                    case "before":
                        return account.currency().format(transaction.before());
                }

                return empty();
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