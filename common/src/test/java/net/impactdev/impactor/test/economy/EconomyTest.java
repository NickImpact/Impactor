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

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.services.economy.EconomyService;
import net.impactdev.impactor.api.services.economy.accounts.Account;
import net.impactdev.impactor.api.services.economy.accounts.AccountAccessor;
import net.impactdev.impactor.api.services.economy.currency.Currency;
import net.impactdev.impactor.api.services.economy.currency.CurrencyProvider;
import net.impactdev.impactor.api.services.economy.transactions.EconomyResultType;
import net.impactdev.impactor.api.services.economy.transactions.EconomyTransaction;
import net.impactdev.impactor.api.services.economy.transactions.EconomyTransactionType;
import net.impactdev.impactor.economy.ImpactorEconomyService;
import net.impactdev.impactor.economy.accounts.accessors.UniqueAccountAccessor;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EconomyTest {

    @AfterAll
    public static void clean() {
        EconomyService service = Impactor.instance().services().provide(EconomyService.class);
        ((ImpactorEconomyService) service).storage().purge().join();

        try {
            Path config = Paths.get("config");
            Path logs = Paths.get("logs");
            Path impactor = Paths.get("impactor");

            if(Files.exists(config)) {
                FileUtils.cleanDirectory(config.toFile());
                Files.delete(config);
            }

            if(Files.exists(logs)) {
                FileUtils.cleanDirectory(logs.toFile());
                Files.delete(logs);
            }

            if(Files.exists(impactor)) {
                FileUtils.cleanDirectory(impactor.toFile());
                Files.delete(impactor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void base() {
        EconomyService service = Impactor.instance().services().provide(EconomyService.class);
        CurrencyProvider currencies = service.currencies();
        Account account = service.account(PlatformPlayer.getOrCreate(UUID.randomUUID()).accountAccessor(), currencies.primary()).join();
        assertNotNull(account);
        assertEquals(currencies.primary().starting(), account.balance());
    }

    @Test
    public void references() {
        EconomyService service = Impactor.instance().services().provide(EconomyService.class);
        CurrencyProvider currencies = service.currencies();
        PlatformPlayer a = PlatformPlayer.getOrCreate(UUID.randomUUID());
        PlatformPlayer b = PlatformPlayer.getOrCreate(UUID.randomUUID());
        Account aa = service.account(a.accountAccessor(), currencies.primary()).join();
        Account ab = service.account(b.accountAccessor(), currencies.primary()).join();
        aa.deposit(BigDecimal.valueOf(250.0));
        ab.deposit(BigDecimal.valueOf(500.0));

        List<AccountAccessor> holders = service.accessors().join();
        assertNotNull(holders);
        assertFalse(holders.isEmpty());

        assertDoesNotThrow(() -> {
            AccountAccessor aaAccessor = holders.stream()
                    .filter(accessor -> accessor instanceof UniqueAccountAccessor)
                    .filter(accessor -> ((UniqueAccountAccessor) accessor).uuid().equals(a.uuid()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Missing Account Accessor"));

            AccountAccessor bbAccessor = holders.stream()
                    .filter(accessor -> accessor instanceof UniqueAccountAccessor)
                    .filter(accessor -> ((UniqueAccountAccessor) accessor).uuid().equals(b.uuid()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Missing Account Accessor"));

            assertEquals(aa.balance(), aaAccessor.account(currencies.primary()).join().balance());
            assertEquals(ab.balance(), bbAccessor.account(currencies.primary()).join().balance());
        });

        List<AccountAccessor> sorted = holders.stream()
                .sorted((r1, r2) -> {
                    Account a1 = r1.account(currencies.primary()).join();
                    Account a2 = r2.account(currencies.primary()).join();

                    return a2.balance().compareTo(a1.balance());
                }).toList();
        assertEquals(BigDecimal.valueOf(1000.0), sorted.get(0).account(currencies.primary()).join().balance());
        assertEquals(BigDecimal.valueOf(750.0), sorted.get(1).account(currencies.primary()).join().balance());
    }

    @Test
    public void transactions() {
        EconomyService service = Impactor.instance().services().provide(EconomyService.class);
        CurrencyProvider currencies = service.currencies();
        Currency focus = currencies.primary();

        PlatformPlayer a = PlatformPlayer.getOrCreate(UUID.randomUUID());
        Account aa = service.account(a.accountAccessor(), focus).join();
        EconomyTransaction transaction = aa.deposit(BigDecimal.valueOf(500));

        assertEquals(aa, transaction.account());
        assertEquals(BigDecimal.valueOf(500), transaction.amount());
        assertEquals(focus, transaction.currency());
        assertEquals(EconomyTransactionType.DEPOSIT, transaction.type());
        assertEquals(EconomyResultType.SUCCESS, transaction.result());
    }
}
