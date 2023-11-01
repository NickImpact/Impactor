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

package net.impactdev.impactor.integrations.octo.fabric.mirrors;

import com.epherical.octoecon.api.Economy;
import com.epherical.octoecon.api.event.EconomyEvents;
import com.epherical.octoecon.api.user.UniqueUser;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import net.impactdev.impactor.api.economy.EconomyService;
import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.economy.currency.CurrencyProvider;
import net.impactdev.impactor.integrations.octo.fabric.AccountTranslator;
import net.impactdev.impactor.integrations.octo.fabric.CurrencyTranslator;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class ImpactorToOctoMirror implements EconomyService {

    private final OctoCurrencyProvider provider = new OctoCurrencyProvider();
    private Economy delegate;

    public ImpactorToOctoMirror() {
        EconomyEvents.ECONOMY_CHANGE_EVENT.register(in -> {
            this.delegate = in;
            this.provider.reload(in);
        });
    }

    @Override
    public String name() {
        return "Impactor Octo Economy API Mirror";
    }

    @Override
    public CurrencyProvider currencies() {
        return this.provider;
    }

    @Override
    public CompletableFuture<Boolean> hasAccount(Currency currency, UUID uuid) {
        return CompletableFuture.completedFuture(this.delegate.hasAccount(uuid));
    }

    @Override
    public CompletableFuture<Account> account(Currency currency, UUID uuid) {
        return CompletableFuture.supplyAsync(() -> AccountTranslator.impactor(
                this.delegate,
                CurrencyTranslator.octo(currency),
                Objects.requireNonNull(this.delegate.getOrCreatePlayerAccount(uuid))
        ));
    }

    @Override
    public CompletableFuture<Account> account(Currency currency, UUID uuid, Account.AccountModifier modifier) {
        return this.account(currency, uuid);
    }

    @Override
    public CompletableFuture<Multimap<Currency, Account>> accounts() {
        return CompletableFuture.supplyAsync(() -> {
            final Map<Key, Currency> translations = Maps.newHashMap();

            final Multimap<Currency, Account> accounts = ArrayListMultimap.create();
            this.delegate.getAllUsers().forEach(user -> {
                if(user instanceof UniqueUser unique) {
                    this.delegate.getCurrencies().forEach(octo -> {
                        final Key key = CurrencyTranslator.keyForOcto(octo);
                        Currency currency = translations.computeIfAbsent(key, ignore -> CurrencyTranslator.impactor(this.delegate, octo));
                        accounts.put(currency, AccountTranslator.impactor(this.delegate, octo, unique));
                    });
                }
            });

            return accounts;
        });
    }

    @Override
    public CompletableFuture<Void> deleteAccount(Currency currency, UUID uuid) {
        this.delegate.deleteAccount(uuid);
        return CompletableFuture.completedFuture(null);
    }

    private static final class OctoCurrencyProvider implements CurrencyProvider {

        private final Map<Key, Currency> currencies = Maps.newHashMap();
        private Currency primary;

        @SuppressWarnings("PatternValidation")
        public void reload(Economy economy) {
            this.currencies.clear();
            economy.getCurrencies().forEach(currency -> {
                Currency impactor = CurrencyTranslator.impactor(economy, currency);
                if(impactor.primary()) {
                    this.primary = impactor;
                }

                this.currencies.put(
                        CurrencyTranslator.keyForOcto(currency),
                        impactor
                );
            });
        }

        @Override
        public @NotNull Currency primary() {
            return Objects.requireNonNull(this.primary);
        }

        @Override
        public Optional<Currency> currency(Key key) {
            return Optional.ofNullable(this.currencies.get(key));
        }

        @Override
        public Set<Currency> registered() {
            return ImmutableSet.copyOf(this.currencies.values());
        }

        @Override
        public CompletableFuture<Boolean> register(Currency currency) {
            return CompletableFuture.completedFuture(false);
        }
    }

}
