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

package net.impactdev.impactor.integrations.vault;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.impactdev.impactor.api.configuration.Config;
import net.impactdev.impactor.api.economy.EconomyService;
import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.economy.currency.CurrencyProvider;
import net.impactdev.impactor.api.events.ImpactorEvent;
import net.kyori.adventure.key.Key;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static net.kyori.adventure.text.Component.text;

public final class VaultService implements EconomyService {

    @MonotonicNonNull
    private Economy delegate;
    private CurrencyProvider currencies;

    public void vault(Economy delegate, Config config) {
        this.delegate = delegate;
        this.currencies = new VaultCurrencyProvider(delegate, config);
    }

    @Override
    public String name() {
        return "Impactor Vault Service Mirror";
    }

    @Override
    public CurrencyProvider currencies() {
        return this.currencies;
    }

    @Override
    public CompletableFuture<Boolean> hasAccount(Currency currency, UUID uuid) {
        return CompletableFuture.supplyAsync(() -> this.delegate.hasAccount(Bukkit.getOfflinePlayer(uuid)))
                .orTimeout(5, TimeUnit.SECONDS);
    }

    @Override
    public CompletableFuture<Account> account(Currency currency, UUID uuid) {
        OfflinePlayer target = Bukkit.getOfflinePlayer(uuid);
        return CompletableFuture.supplyAsync(() -> this.delegate.hasAccount(target))
                .thenApply(ignore -> Account.builder()
                        .currency(this.currencies.primary())
                        .owner(uuid)
                        .balance(BigDecimal.valueOf(this.delegate.getBalance(target)))
                        .build()
                )
                .orTimeout(5, TimeUnit.SECONDS);
    }

    @Override
    public CompletableFuture<Account> account(Currency currency, UUID uuid, Account.AccountModifier modifier) {
        return this.account(currency, uuid);
    }

    @Override
    public CompletableFuture<Multimap<Currency, Account>> accounts() {
        return CompletableFuture.completedFuture(ArrayListMultimap.create());
    }

    @Override
    public CompletableFuture<Void> deleteAccount(Currency currency, UUID uuid) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> save(Account account) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(account.owner());
        double balance = this.delegate.getBalance(player);
        double difference = account.balance().doubleValue() - balance;
        return CompletableFuture.runAsync(() -> this.delegate.depositPlayer(player, difference));
    }

    public static final class VaultReadyEvent implements ImpactorEvent {

        private final Economy service;

        public VaultReadyEvent(Economy service) {
            this.service = service;
        }

        public Economy economy() {
            return this.service;
        }

    }

    public static final class VaultCurrencyProvider implements CurrencyProvider {

        private final Currency currency;

        @SuppressWarnings("PatternValidation")
        public VaultCurrencyProvider(Economy delegate, Config config) {
            String name = Optional.ofNullable(delegate.currencyNamePlural())
                    .filter(in -> !in.isBlank())
                    .map(in -> in.replace(" ", "_"))
                    .orElse("provided");

            this.currency = Currency.builder()
                    .key(Key.key("vault", name))
                    .name(text(delegate.currencyNameSingular()))
                    .plural(text(delegate.currencyNamePlural()))
                    .symbol(text(config.get(VaultConfig.VAULT_SYMBOL)))
                    .formatting(config.get(VaultConfig.VAULT_SYMBOL_PREFIX) ? Currency.SymbolFormatting.BEFORE : Currency.SymbolFormatting.AFTER)
                    .decimals(delegate.fractionalDigits())
                    .transferable(true)
                    .starting(BigDecimal.ZERO)
                    .primary()
                    .build();
        }

        @Override
        public @NotNull Currency primary() {
            return this.currency;
        }

        @Override
        public Optional<Currency> currency(Key key) {
            if(this.currency.key().equals(key)) {
                return Optional.of(this.currency);
            }

            return Optional.empty();
        }

        @Override
        public Set<Currency> registered() {
            return Set.of(this.currency);
        }

        @Override
        public CompletableFuture<Boolean> register(Currency currency) {
            return CompletableFuture.completedFuture(false);
        }
    }
}
