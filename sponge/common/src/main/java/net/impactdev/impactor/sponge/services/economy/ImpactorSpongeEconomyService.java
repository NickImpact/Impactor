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

package net.impactdev.impactor.sponge.services.economy;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import net.impactdev.impactor.economy.ImpactorEconomyService;
import net.impactdev.impactor.economy.currency.ImpactorCurrency;
import net.impactdev.impactor.sponge.services.economy.accounts.SpongeUniqueAccount;
import net.impactdev.impactor.sponge.services.economy.currency.SpongeCurrencyDelegator;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.AccountDeletionResultType;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.account.VirtualAccount;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class ImpactorSpongeEconomyService implements EconomyService {

    private final net.impactdev.impactor.api.services.economy.EconomyService impactor;

    private final LoadingCache<UUID, UniqueAccount> unique = Caffeine.newBuilder().build(SpongeUniqueAccount::new);
    private final LoadingCache<String, VirtualAccount> virtual = Caffeine.newBuilder()
            .build(key -> {
                return null;
            });

    public ImpactorSpongeEconomyService() {
        this.impactor = new ImpactorEconomyService();
        this.impactor.currencies().registered().forEach(currency -> SpongeCurrencyDelegator.register((ImpactorCurrency) currency));
    }

    @Override
    public Currency defaultCurrency() {
        return SpongeCurrencyDelegator.sponge((ImpactorCurrency) this.impactor.currencies().primary());
    }

    @Override
    public boolean hasAccount(UUID uuid) {
        return this.unique.get(uuid) != null;
    }

    @Override
    public boolean hasAccount(String identifier) {
        return this.virtual.get(identifier) != null;
    }

    @Override
    public Optional<UniqueAccount> findOrCreateAccount(UUID uuid) {
        return Optional.ofNullable(this.unique.get(uuid));
    }

    @Override
    public Optional<Account> findOrCreateAccount(String identifier) {
        return Optional.ofNullable(this.virtual.get(identifier));
    }

    @Override
    public Stream<UniqueAccount> streamUniqueAccounts() {
        return this.uniqueAccounts().stream();
    }

    @Override
    public Collection<UniqueAccount> uniqueAccounts() {
        return this.unique.asMap().values();
    }

    @Override
    public Stream<VirtualAccount> streamVirtualAccounts() {
        return this.virtualAccounts().stream();
    }

    @Override
    public Collection<VirtualAccount> virtualAccounts() {
        return this.virtual.asMap().values();
    }

    @Override
    public AccountDeletionResultType deleteAccount(UUID uuid) {
        return null;
    }

    @Override
    public AccountDeletionResultType deleteAccount(String identifier) {
        return null;
    }
}
