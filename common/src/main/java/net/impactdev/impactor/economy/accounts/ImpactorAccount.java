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

package net.impactdev.impactor.economy.accounts;

import net.impactdev.impactor.api.services.economy.accounts.Account;
import net.impactdev.impactor.api.services.economy.accounts.AccountLinker;
import net.impactdev.impactor.api.services.economy.currency.Currency;
import net.impactdev.impactor.api.services.economy.transactions.EconomyResultType;
import net.impactdev.impactor.api.services.economy.transactions.EconomyTransaction;
import net.impactdev.impactor.api.services.economy.transactions.EconomyTransactionType;
import net.impactdev.impactor.economy.transactions.ImpactorEconomyTransaction;

import java.math.BigDecimal;

public final class ImpactorAccount implements Account {

    private final Currency currency;
    private final AccountLinker linker;
    private BigDecimal balance;

    private ImpactorAccount(Currency currency, AccountLinker linker) {
        this.currency = currency;
        this.linker = linker;
        this.balance = currency.starting();
    }

    private ImpactorAccount(Currency currency, BigDecimal balance, AccountLinker linker) {
        this.currency = currency;
        this.linker = linker;
        this.balance = balance;
    }

    public static ImpactorAccount create(Currency currency, AccountLinker linker) {
        return new ImpactorAccount(currency, linker);
    }

    public static ImpactorAccount load(Currency currency, BigDecimal balance, AccountLinker linker) {
        return new ImpactorAccount(currency, balance, linker);
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
        this.linker.save(this);
    }

}
