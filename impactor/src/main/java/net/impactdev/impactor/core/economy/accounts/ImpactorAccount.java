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

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.economy.EconomyService;
import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.economy.events.EconomyTransactionEvent;
import net.impactdev.impactor.api.economy.events.EconomyTransferTransactionEvent;
import net.impactdev.impactor.api.economy.transactions.EconomyResultType;
import net.impactdev.impactor.api.economy.transactions.EconomyTransaction;
import net.impactdev.impactor.api.economy.transactions.EconomyTransactionType;
import net.impactdev.impactor.api.economy.transactions.EconomyTransferTransaction;
import net.impactdev.impactor.api.events.ImpactorEvent;
import net.impactdev.impactor.api.utility.printing.PrettyPrinter;
import net.impactdev.impactor.core.economy.ImpactorEconomyService;
import net.impactdev.impactor.core.economy.events.ImpactorEconomyTransactionEvent;
import net.impactdev.impactor.core.economy.events.ImpactorEconomyTransferTransactionEvent;
import net.impactdev.impactor.core.economy.transactions.ImpactorEconomyTransaction;
import net.impactdev.impactor.core.economy.transactions.ImpactorEconomyTransferTransaction;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.event.PostResult;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public final class ImpactorAccount implements Account {

    private final ImpactorEconomyService service = (ImpactorEconomyService) Impactor.instance()
            .services()
            .provide(EconomyService.class);

    private final UUID owner;
    private final Currency currency;
    private BigDecimal balance;

    private ImpactorAccount(ImpactorAccountBuilder builder) {
        this(builder.currency, builder.owner, Optional.ofNullable(builder.balance).orElse(builder.currency.defaultAccountBalance()));
    }

    private ImpactorAccount(Currency currency, UUID owner, BigDecimal balance) {
        this.owner = owner;
        this.currency = currency;
        this.balance = balance;
    }

    public static ImpactorAccount load(Currency currency, UUID uuid, BigDecimal balance) {
        return new ImpactorAccount(currency, uuid, balance);
    }

    @Override
    public @NotNull Currency currency() {
        return this.currency;
    }

    @Override
    public @NotNull UUID owner() {
        return this.owner;
    }

    @Override
    public @NotNull BigDecimal balance() {
        return this.balance;
    }

    @Override
    public @NotNull EconomyTransaction set(BigDecimal amount) {
        return this.enact(amount, EconomyTransactionType.SET, () -> {
            ImpactorEconomyTransaction.TransactionBuilder builder = ImpactorEconomyTransaction.builder()
                    .account(this)
                    .currency(this.currency)
                    .amount(amount)
                    .type(EconomyTransactionType.SET);

            EconomyTransactionEvent.Pre pre = this.createAndFirePre(amount, EconomyTransactionType.SET);
            if(pre.cancelled()) {
                return builder.result(EconomyResultType.CANCELLED).build();
            }

            if(amount.signum() < 0) {
                return this.createAndFirePost(builder.result(EconomyResultType.FAILED).build());
            } else if(amount.doubleValue() >= 0) {
                return this.createAndFirePost(builder.result(EconomyResultType.NO_REMAINING_SPACE).build());
            } else {
                this.balance = amount;
                this.save();
                return this.createAndFirePost(builder.result(EconomyResultType.SUCCESS).build());
            }
        }, () -> ImpactorEconomyTransaction.builder()
                .currency(this.currency)
                .account(this)
                .amount(amount)
                .type(EconomyTransactionType.SET)
                .result(EconomyResultType.FAILED)
                .build()
        );

    }

    @Override
    public @NotNull EconomyTransaction withdraw(BigDecimal amount) {
        return this.enact(amount, EconomyTransactionType.WITHDRAW, () -> {
            ImpactorEconomyTransaction.TransactionBuilder builder = ImpactorEconomyTransaction.builder()
                    .account(this)
                    .currency(this.currency)
                    .amount(amount)
                    .type(EconomyTransactionType.WITHDRAW);

            EconomyTransactionEvent.Pre pre = this.createAndFirePre(amount, EconomyTransactionType.WITHDRAW);
            if(pre.cancelled()) {
                return builder.result(EconomyResultType.CANCELLED).build();
            }

            BigDecimal result = this.balance.subtract(amount);
            if(result.signum() < 0) {
                return this.createAndFirePost(builder.result(EconomyResultType.NOT_ENOUGH_FUNDS).build());
            }

            this.balance = result;
            this.save();
            return this.createAndFirePost(builder.result(EconomyResultType.SUCCESS).build());
        }, () -> ImpactorEconomyTransaction.builder()
                .currency(this.currency)
                .account(this)
                .amount(amount)
                .type(EconomyTransactionType.WITHDRAW)
                .result(EconomyResultType.FAILED)
                .build()
        );
    }

    @Override
    public @NotNull EconomyTransaction deposit(BigDecimal amount) {
        return this.enact(amount, EconomyTransactionType.DEPOSIT, () -> {
            ImpactorEconomyTransaction.TransactionBuilder builder = ImpactorEconomyTransaction.builder()
                    .account(this)
                    .currency(this.currency)
                    .amount(amount)
                    .type(EconomyTransactionType.DEPOSIT);

            EconomyTransactionEvent.Pre pre = this.createAndFirePre(amount, EconomyTransactionType.SET);
            if(pre.cancelled()) {
                return builder.result(EconomyResultType.CANCELLED).build();
            }

            this.balance = this.balance.add(amount);
            this.save();
            return this.createAndFirePost(builder.result(EconomyResultType.SUCCESS).build());
        }, () -> ImpactorEconomyTransaction.builder()
                .currency(this.currency)
                .account(this)
                .amount(amount)
                .type(EconomyTransactionType.DEPOSIT)
                .result(EconomyResultType.FAILED)
                .build()
        );
    }

    @Override
    public @NotNull EconomyTransferTransaction transfer(Account to, BigDecimal amount) {
        return this.enact(amount, EconomyTransactionType.TRANSFER, () -> {
            ImpactorEconomyTransferTransaction.TransactionBuilder builder = ImpactorEconomyTransferTransaction.builder()
                    .currency(this.currency)
                    .from(this)
                    .to(to)
                    .amount(amount);

            EconomyTransferTransactionEvent.Pre event = new ImpactorEconomyTransferTransactionEvent.Pre(
                    this.currency,
                    this,
                    to,
                    amount
            );

            this.postAndVerify(event);
            if(event.cancelled()) {
                return builder.result(EconomyResultType.CANCELLED).build();
            }

            // TODO - Validate
            this.balance = this.balance.subtract(amount);
            ((ImpactorAccount) to).quietSet(to.balance().add(amount));

            this.save();
            ((ImpactorAccount) to).save();

            EconomyTransferTransactionEvent.Post post = new ImpactorEconomyTransferTransactionEvent.Post(
                    builder.result(EconomyResultType.SUCCESS).build()
            );
            this.postAndVerify(post);
            return post.transaction();
        }, () -> ImpactorEconomyTransferTransaction.builder()
                .currency(this.currency)
                .from(this)
                .to(to)
                .amount(amount)
                .result(EconomyResultType.FAILED)
                .build()
        );
    }

    @Override
    public @NotNull EconomyTransaction reset() {
        return this.enact(this.currency.defaultAccountBalance(), EconomyTransactionType.RESET, () -> {
            ImpactorEconomyTransaction.TransactionBuilder builder = ImpactorEconomyTransaction.builder()
                    .account(this)
                    .currency(this.currency)
                    .amount(this.currency.defaultAccountBalance())
                    .type(EconomyTransactionType.DEPOSIT);

            EconomyTransactionEvent.Pre pre = this.createAndFirePre(this.currency.defaultAccountBalance(), EconomyTransactionType.RESET);
            if(pre.cancelled()) {
                return builder.result(EconomyResultType.CANCELLED).build();
            }

            this.balance = this.currency.defaultAccountBalance();
            this.save();
            return this.createAndFirePost(builder.result(EconomyResultType.SUCCESS).build());
        }, () -> ImpactorEconomyTransaction.builder()
                .currency(this.currency)
                .account(this)
                .amount(this.currency.defaultAccountBalance())
                .type(EconomyTransactionType.DEPOSIT)
                .result(EconomyResultType.FAILED)
                .build()
        );
    }

    private void quietSet(BigDecimal amount) {
        this.balance = amount;
    }

    private void save() {
        this.service.storage().save(this);
    }

    private void postAndVerify(@NotNull ImpactorEvent event) throws PostResult.CompositeException {
        PostResult result = Impactor.instance().events().post(event);
        result.raise();
    }

    private <T> T enact(BigDecimal amount, EconomyTransactionType type, TransactionProcessor<T> processor, Supplier<T> fallback) {
        try {
            return processor.process();
        } catch (PostResult.CompositeException exception) {
            PrettyPrinter printer = new PrettyPrinter(80);
            printer.title("Economy Transaction - Subscriber Exceptions")
                    .consume(p -> {
                        int tracked = exception.result().exceptions().size();
                        if(tracked > 1) {
                            p.add("A set of exceptions were encountered while trying to process a");
                            p.add("transaction. Details regarding the problem will now be displayed below...");
                        } else {
                            p.add("An exception encountered while trying to process a transaction. Details");
                            p.add("regarding the problem will now be displayed below...");
                        }
                    })
                    .hr('-')
                    .add("Transaction Type: " + type.name())
                    .add("Currency: " + PlainTextComponentSerializer.plainText().serialize(this.currency.plural()))
                    .add("Account: " + this.owner.toString())
                    .add("Amount: " + PlainTextComponentSerializer.plainText().serialize(this.currency.format(amount)))
                    .consume(p -> {
                        p.newline();
                        AtomicInteger index = new AtomicInteger(1);
                        exception.result().exceptions()
                                .forEach((subscriber, error) -> {
                                    p.add("%d: %s", index.getAndIncrement(), subscriber);
                                    p.add(error, 2);
                                });
                        p.newline();
                    });
            return fallback.get();
        }
    }

    @FunctionalInterface
    private interface TransactionProcessor<T> {

        T process() throws PostResult.CompositeException;

    }

    private EconomyTransactionEvent.Pre createAndFirePre(BigDecimal amount, EconomyTransactionType type) throws PostResult.CompositeException{
        EconomyTransactionEvent.Pre event = new ImpactorEconomyTransactionEvent.Pre(
                this.currency,
                this,
                amount,
                type
        );

        this.postAndVerify(event);
        return event;
    }

    @CanIgnoreReturnValue
    private EconomyTransaction createAndFirePost(EconomyTransaction transaction) throws PostResult.CompositeException {
        EconomyTransactionEvent.Post event = new ImpactorEconomyTransactionEvent.Post(transaction);
        this.postAndVerify(event);
        return transaction;
    }

    public static final class ImpactorAccountBuilder implements AccountBuilder {

        private Currency currency;
        private UUID owner;
        private BigDecimal balance;

        @Override
        public @NotNull AccountBuilder currency(@NotNull Currency currency) {
            this.currency = currency;
            return this;
        }

        @Override
        public @NotNull AccountBuilder owner(@NotNull UUID uuid) {
            this.owner = uuid;
            return this;
        }

        @Override
        public @NotNull AccountBuilder balance(@NotNull BigDecimal balance) {
            this.balance = balance;
            return this;
        }

        @Override
        public Account build() {
            return new ImpactorAccount(this);
        }
    }
}
