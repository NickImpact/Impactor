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

import com.epherical.octoecon.api.Currency;
import com.epherical.octoecon.api.Economy;
import com.epherical.octoecon.api.user.FakeUser;
import com.epherical.octoecon.api.user.UniqueUser;
import com.epherical.octoecon.api.user.User;
import net.impactdev.impactor.api.economy.EconomyService;
import net.impactdev.impactor.api.economy.events.EstablishEconomyServiceEvent;
import net.impactdev.impactor.api.events.ImpactorEventBus;
import net.impactdev.impactor.integrations.octo.fabric.CurrencyTranslator;
import net.kyori.adventure.key.Key;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

public final class OctoToImpactorMirror implements Economy {

    private EconomyService delegate;

    public OctoToImpactorMirror() {
        ImpactorEventBus.bus().subscribe(EstablishEconomyServiceEvent.class, event -> this.delegate = event.service());
    }

    @Override
    public boolean enabled() {
        return true;
    }

    @Override
    public Collection<Currency> getCurrencies() {
        return this.delegate.currencies()
                .registered()
                .stream()
                .map(CurrencyTranslator::octo)
                .collect(Collectors.toList());
    }

    @Override
    public Currency getDefaultCurrency() {
        return CurrencyTranslator.octo(this.delegate.currencies().primary());
    }

    @Override
    @SuppressWarnings("PatternValidation")
    public @Nullable Currency getCurrency(ResourceLocation identifier) {
        Key key = Key.key(identifier.toString());
        return this.delegate.currencies().currency(key).map(CurrencyTranslator::octo).orElse(null);
    }

    @Override
    public @Nullable FakeUser getOrCreateAccount(ResourceLocation identifier) {
        return null;
    }

    @Override
    public @Nullable UniqueUser getOrCreatePlayerAccount(UUID identifier) {
        return null;
    }

    @Override
    public Collection<UniqueUser> getUniqueUsers() {
        return null;
    }

    @Override
    public Collection<User> getAllUsers() {
        return null;
    }

    @Override
    public Collection<FakeUser> getFakeUsers() {
        return null;
    }

    @Override
    public boolean hasAccount(UUID identifier) {
        return this.delegate.hasAccount(identifier).join();
    }

    @Override
    public boolean hasAccount(ResourceLocation identifier) {
        return false;
    }

    @Override
    public boolean deleteAccount(UUID identifier) {
        this.delegate.deleteAccount(identifier).join();
        return true;
    }

    @Override
    public boolean deleteAccount(ResourceLocation identifier) {
        return false;
    }
}
