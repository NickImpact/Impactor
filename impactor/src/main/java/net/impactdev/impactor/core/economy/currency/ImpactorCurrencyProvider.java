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

package net.impactdev.impactor.core.economy.currency;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.economy.currency.CurrencyProvider;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;

public class ImpactorCurrencyProvider implements CurrencyProvider {

    private final Currency primary;
    private final Cache<Key, Currency> currencies = Caffeine.newBuilder().build();
    private final ReentrantLock lock = new ReentrantLock();

    public ImpactorCurrencyProvider(List<Currency> currencies) {
        this.primary = currencies.stream()
                .filter(Currency::primary)
                .findFirst()
                .orElse(currencies.get(0));

        currencies.forEach(currency -> this.currencies.put(currency.key(), currency));
    }

    @Override
    public @NotNull Currency primary() {
        return this.primary;
    }

    @Override
    public Optional<Currency> currency(Key key) {
        return Optional.ofNullable(this.currencies.getIfPresent(key));
    }

    @Override
    public Set<Currency> registered() {
        return new HashSet<>(this.currencies.asMap().values());
    }

    @Override
    public CompletableFuture<Boolean> register(Currency currency) {
        return CompletableFuture.supplyAsync(() -> {
            this.lock.lock();
            try {
                if (this.currencies.getIfPresent(currency.key()) != null) {
                    return false;
                }

                this.currencies.put(currency.key(), currency);
                return true;
            } finally {
                this.lock.unlock();
            }
        });
    }
}
