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

package net.impactdev.impactor.test.economy;

import net.impactdev.impactor.api.core.ServiceProvider;
import net.impactdev.impactor.api.economy.EconomyService;
import net.impactdev.impactor.api.economy.accounts.banks.Interest;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.economy.transactions.EconomyTransaction;
import net.impactdev.impactor.api.economy.transactions.details.TransactionResult;
import net.impactdev.impactor.api.economy.transactions.details.TransactionType;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EconomyTest {

    @Test
    public void transact() {
        EconomyService service = ServiceProvider.instance().provide(EconomyService.class);
        Currency primary = service.currencies().primary();

        service.accounts().getOrCreate(UUID.randomUUID(), primary).thenAccept(account -> {
            assertEquals(500.0, account.balance().doubleValue());

            EconomyTransaction transaction = account.set(new BigDecimal("750.0")).join();
            assertEquals(TransactionType.SET, transaction.type());
            assertEquals(TransactionResult.SUCCESS, transaction.result());
            assertTrue(transaction.successful());

            assertEquals(750.0, transaction.amount().doubleValue());
        }).join();
    }

    @Test
    public void banking() {
        final BigDecimal balance = new BigDecimal("500.0");
        final BigDecimal interest = new BigDecimal("0.025");

        EconomyService service = ServiceProvider.instance().provide(EconomyService.class);
        Currency primary = Currency.builder()
                .key(Key.key("impactor", "test"))
                .decimals(2)
                .starting(balance)
                .symbol(Component.text("$"))
                .displayName(Component.text("Test"), Component.text("Testing"))
                .formatting("<symbol><balance>")
                .banking(config -> {
                    config.interest(i -> i.enabled(true).applier(b -> b.add(b.multiply(interest))));
                })
                .build();

        service.accounts().getOrCreate(UUID.randomUUID(), primary)
                .thenApply(account -> {
                    assertTrue(account.bank().isPresent());
                    return account.bank().orElseThrow();
                })
                .thenAccept(bank -> {
                    assertTrue(bank.interest().isPresent());
                    assertTrue(bank.credit().isPresent());

                    Interest i = bank.interest().get();
                    BigDecimal amount = i.apply(new BigDecimal(50));
                    assertEquals(balance.add(balance.multiply(interest)), amount);
                })
                .join();
    }

}
