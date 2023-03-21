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

import com.google.common.collect.Multimap;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.economy.EconomyService;
import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.economy.events.EconomyTransactionEvent;
import net.impactdev.impactor.api.economy.transactions.EconomyTransferTransaction;
import net.impactdev.impactor.api.economy.transactions.composer.TransactionComposer;
import net.impactdev.impactor.api.economy.transactions.details.EconomyResultType;
import net.impactdev.impactor.api.economy.transactions.EconomyTransaction;
import net.impactdev.impactor.api.economy.transactions.details.EconomyTransactionType;
import net.impactdev.impactor.core.economy.ImpactorEconomyService;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BasicEconomyTest {

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

    private static final UUID target = UUID.randomUUID();
    private static BigDecimal balance = BigDecimal.ZERO;

    @Test
    @Order(1)
    public void createAndVerifyAccount() {
        EconomyService service = Impactor.instance().services().provide(EconomyService.class);
        Currency currency = service.currencies().primary();
        Account account = service.account(currency, target).join();

        assertEquals(currency.defaultAccountBalance(), balance = account.balance());
        assertTrue(Files.exists(Paths.get("impactor")
                .resolve("economy")
                .resolve("accounts")
                .resolve("users")
                .resolve(account.owner().toString().substring(0, 2))
                .resolve(account.owner() + ".conf")
        ));
    }

    @Test
    @Order(2)
    public void fetchAndUpdate() {
        assertTrue(Files.exists(Paths.get("impactor")
                .resolve("economy")
                .resolve("accounts")
                .resolve("users")
                .resolve(target.toString().substring(0, 2))
                .resolve(target + ".conf")
        ));

        EconomyService service = Impactor.instance().services().provide(EconomyService.class);
        Currency currency = service.currencies().primary();
        Account account = service.account(currency, target).join();

        assertEquals(balance, account.balance());
        BigDecimal adjustment = BigDecimal.valueOf(250);
        EconomyTransaction transaction = account.deposit(adjustment);

        assertEquals(EconomyResultType.SUCCESS, transaction.result());
        assertEquals(EconomyTransactionType.DEPOSIT, transaction.type());
        assertEquals(balance.add(adjustment), balance = account.balance());

        account = service.account(currency, target).join();
        assertEquals(balance, account.balance());
    }

    @Test
    @Order(3)
    public void validateStorageAndAccessMultipleAccounts() {
        EconomyService service = Impactor.instance().services().provide(EconomyService.class);
        Currency currency = service.currencies().primary();
        Multimap<Currency, Account> current = service.accounts().join();

        assertEquals(1, current.size());

        service.account(currency, UUID.randomUUID()).join();
        service.account(currency, UUID.randomUUID()).join();
        current = service.accounts().join();
        assertEquals(3, current.size());
    }

    @Test
    @Order(4)
    public void calculateTopAccounts() {
        EconomyService service = Impactor.instance().services().provide(EconomyService.class);
        Currency currency = service.currencies().primary();

        BigDecimal adjustment = BigDecimal.ZERO;
        Collection<Account> accounts = service.accounts(currency).join();
        for(Account account : accounts) {
            account.reset();

            account.deposit(adjustment);
            adjustment = adjustment.add(BigDecimal.valueOf(250));
        }

        List<Account> sorted = accounts.stream()
                .sorted(Comparator.comparing(Account::balance).reversed())
                .collect(Collectors.toList());

        assertEquals(BigDecimal.valueOf(1000.0), sorted.get(0).balance());
    }

    @Test
    @Order(5)
    public void subscribeAndListenForTransactions() {
        AtomicBoolean flag = new AtomicBoolean(false);
        Impactor.instance().events().subscribe(EconomyTransactionEvent.Post.class, event -> {
            EconomyTransaction transaction = event.transaction();
            assertEquals(EconomyResultType.SUCCESS, transaction.result());
            flag.set(true);
        });

        EconomyService service = Impactor.instance().services().provide(EconomyService.class);
        Currency currency = service.currencies().primary();
        Account account = service.account(currency, target).join();

        account.withdraw(new BigDecimal(300));
        assertTrue(flag.get());
    }

    @Test
    public void verifyFormatting() {
        EconomyService service = Impactor.instance().services().provide(EconomyService.class);
        Currency currency = service.currencies().primary();

        BigDecimal amount = new BigDecimal("17.38");
        Component condensed = currency.format(amount);
        Component standard = currency.format(amount, false);

        assertEquals("$17.38", PlainTextComponentSerializer.plainText().serialize(condensed));
        assertEquals("17.38 Dollars", PlainTextComponentSerializer.plainText().serialize(standard));

        condensed = currency.format(BigDecimal.ONE);
        standard = currency.format(BigDecimal.ONE, false);
        assertEquals("$1.00", PlainTextComponentSerializer.plainText().serialize(condensed));
        assertEquals("1.00 Dollar", PlainTextComponentSerializer.plainText().serialize(standard));

        condensed = currency.format(BigDecimal.ZERO);
        standard = currency.format(BigDecimal.ZERO, false);
        assertEquals("$0.00", PlainTextComponentSerializer.plainText().serialize(condensed));
        assertEquals("0.00 Dollars", PlainTextComponentSerializer.plainText().serialize(standard));

        condensed = currency.format(amount, true, Locale.CANADA_FRENCH);
        assertEquals("$17,38", PlainTextComponentSerializer.plainText().serialize(condensed));
    }

    @Test
    @Order(6)
    public void transactionManipulation() {
        EconomyService service = Impactor.instance().services().provide(EconomyService.class);
        Currency currency = service.currencies().primary();

        Account account = service.account(currency, target).join();
        EconomyTransaction set = account.set(BigDecimal.ONE);
        assertTrue(set.successful());
        BigDecimal amount = new BigDecimal("1.00");

        EconomyTransaction transaction = EconomyTransaction.compose()
                .account(account)
                .type(EconomyTransactionType.WITHDRAW)
                .amount(amount)
                .message(EconomyResultType.SUCCESS, Component.text("Transaction succeeded!"))
                .message(EconomyResultType.NOT_ENOUGH_FUNDS, Component.text("Account did not have enough funds..."))
                .message(EconomyResultType.FAILED, Component.text("A failure occurred while processing this transaction"))
                .build();

        assertTrue(transaction.successful());
        assertEquals(0, account.balance().intValue());

        AtomicReference<Component> message = new AtomicReference<>(Component.empty());
        Audience audience = new Audience() {
            @Override
            public void sendMessage(@NotNull Identity source, @NotNull Component msg, @NotNull MessageType type) {
                message.set(msg);
            }
        };

        transaction.inform(audience);
        assertEquals("Transaction succeeded!", PlainTextComponentSerializer.plainText().serialize(message.get()));

        Account a = service.account(currency, UUID.randomUUID()).join();
        EconomyTransferTransaction transfer = EconomyTransferTransaction.compose()
                .from(a)
                .to(account)
                .amount(new BigDecimal(250))
                .message(EconomyResultType.SUCCESS, Component.text("Transaction completed!"))
                .build();

        transfer.inform(audience);
        assertEquals("Transaction completed!", PlainTextComponentSerializer.plainText().serialize(message.get()));

        Currency secondary = Currency.builder()
                .key(Key.key("impactor:test"))
                .name(Component.text("Test"))
                .plural(Component.text("Test"))
                .decimals(3)
                .formatting(Currency.SymbolFormatting.BEFORE)
                .symbol(Component.text('$'))
                .starting(BigDecimal.TEN)
                .build();
        Account b = service.account(secondary, UUID.randomUUID()).join();
        EconomyTransferTransaction failure = EconomyTransferTransaction.compose()
                .from(a)
                .to(b)
                .amount(BigDecimal.TEN)
                .message(EconomyResultType.INVALID, Component.text("Mismatched currencies..."))
                .build();

        failure.inform(audience);
        assertEquals("Mismatched currencies...", PlainTextComponentSerializer.plainText().serialize(message.get()));
    }

    @Test
    @Order(7)
    public void delete() {
        EconomyService service = Impactor.instance().services().provide(EconomyService.class);
        Currency primary = service.currencies().primary();
        Currency secondary = Currency.builder()
                .key(Key.key("impactor:test"))
                .name(Component.text("Test"))
                .plural(Component.text("Test"))
                .decimals(3)
                .formatting(Currency.SymbolFormatting.BEFORE)
                .symbol(Component.text('$'))
                .starting(BigDecimal.TEN)
                .build();

        service.account(primary, target).join();
        service.account(secondary, target).join();

        assertTrue(service.hasAccount(primary, target).join());
        assertTrue(service.hasAccount(secondary, target).join());

        service.deleteAccount(secondary, target).join();
        assertTrue(service.hasAccount(primary, target).join());
        assertFalse(service.hasAccount(secondary, target).join());
    }

    @Test
    public void virtualAccounts() {
        EconomyService service = Impactor.instance().services().provide(EconomyService.class);

        Account account = service.account(UUID.randomUUID(), Account.AccountBuilder::virtual).join();
        assertTrue(account.virtual());

        Account existing = service.account(target, Account.AccountBuilder::virtual).join();
        assertFalse(existing.virtual());
    }
}
