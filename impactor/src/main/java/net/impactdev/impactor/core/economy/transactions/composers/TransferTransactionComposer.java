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

package net.impactdev.impactor.core.economy.transactions.composers;

import com.google.common.base.Preconditions;
import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.api.economy.transactions.EconomyTransferTransaction;
import net.impactdev.impactor.api.economy.transactions.composer.TransferComposer;
import net.impactdev.impactor.api.economy.transactions.details.EconomyResultType;
import net.impactdev.impactor.core.economy.accounts.ImpactorAccount;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public final class TransferTransactionComposer implements TransferComposer {

    private Account from;
    private Account to;

    private BigDecimal amount;
    private final Map<EconomyResultType, Component> messages = new HashMap<>();

    public Account target() {
        return this.to;
    }

    public BigDecimal amount() {
        return this.amount;
    }

    public Map<EconomyResultType, Component> messages() {
        return this.messages;
    }

    @Override
    public TransferComposer from(@NotNull Account account) {
        this.from = account;
        return this;
    }

    @Override
    public TransferComposer to(@NotNull Account account) {
        this.to = account;
        return this;
    }

    @Override
    public TransferComposer amount(@NotNull BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    @Override
    public TransferComposer message(@NotNull EconomyResultType type, @NotNull Component message) {
        this.messages.put(type, message);
        return this;
    }

    @Override
    public @NotNull EconomyTransferTransaction build() {
        Preconditions.checkNotNull(this.from, "from");
        Preconditions.checkNotNull(this.to, "to");
        Preconditions.checkNotNull(this.amount, "amount");

        ImpactorAccount account = (ImpactorAccount) this.from;
        return account.transfer(this);
    }
}
