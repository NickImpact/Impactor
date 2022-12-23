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

package net.impactdev.impactor.core.economy.accounts;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.economy.EconomyService;
import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.economy.transactions.EconomyResultType;
import net.impactdev.impactor.api.economy.transactions.EconomyTransaction;
import net.impactdev.impactor.api.economy.transactions.EconomyTransactionType;
import net.impactdev.impactor.core.economy.ImpactorEconomyService;
import net.impactdev.impactor.core.economy.transactions.ImpactorEconomyTransaction;

import java.math.BigDecimal;
import java.util.UUID;

public final class ImpactorAccount implements Account {

    private final ImpactorEconomyService service = (ImpactorEconomyService) Impactor.instance()
            .services()
            .provide(EconomyService.class);

    private final UUID uuid;
    private final Currency currency;
    private BigDecimal balance;

    private ImpactorAccount(UUID uuid, Currency currency) {
        this(uuid, currency, currency.starting());
    }

    private ImpactorAccount(UUID uuid, Currency currency, BigDecimal balance) {
        this.uuid = uuid;
        this.currency = currency;
        this.balance = balance;
    }

    public static ImpactorAccount create(UUID uuid, Currency currency) {
        return new ImpactorAccount(uuid, currency);
    }

    public static ImpactorAccount load(UUID uuid, Currency currency, BigDecimal balance) {
        return new ImpactorAccount(uuid, currency, balance);
    }

    @Override
    public Currency currency() {
        return this.currency;
    }

    @Override
    public BigDecimal balance() {
        return this.balance;
    }

    @Override
    public EconomyTransaction set(BigDecimal amount) {
        ImpactorEconomyTransaction.TransactionBuilder builder = ImpactorEconomyTransaction.builder()
                .account(this)
                .currency(this.currency)
                .amount(amount)
                .type(EconomyTransactionType.SET);

        if(amount.signum() < 0) {
            return builder.result(EconomyResultType.FAILED).build();
        } else if(amount.doubleValue() >= 0) {
            return builder.result(EconomyResultType.NO_REMAINING_SPACE).build();
        } else {
            this.balance = amount;
            this.save();
            return builder.result(EconomyResultType.SUCCESS).build();
        }
    }

    @Override
    public EconomyTransaction withdraw(BigDecimal amount) {
        ImpactorEconomyTransaction.TransactionBuilder builder = ImpactorEconomyTransaction.builder()
                .account(this)
                .currency(this.currency)
                .amount(amount)
                .type(EconomyTransactionType.DEPOSIT);

        BigDecimal result = this.balance.subtract(amount);
        if(result.signum() < 0) {
            return builder.result(EconomyResultType.NOT_ENOUGH_FUNDS).build();
        }

        this.balance = result;
        this.save();
        return builder.result(EconomyResultType.SUCCESS).build();
    }

    @Override
    public EconomyTransaction deposit(BigDecimal amount) {
        ImpactorEconomyTransaction.TransactionBuilder builder = ImpactorEconomyTransaction.builder()
                .account(this)
                .currency(this.currency)
                .amount(amount)
                .type(EconomyTransactionType.DEPOSIT);

        this.balance = this.balance.add(amount);
        this.save();
        return builder.result(EconomyResultType.SUCCESS).build();
    }

    @Override
    public EconomyTransaction transfer(Account to, BigDecimal amount) {
        return null;
    }

    @Override
    public EconomyTransaction reset() {
        return null;
    }

    private void save() {
        this.service.storage().saveAccount(this.uuid, this);
    }

}
