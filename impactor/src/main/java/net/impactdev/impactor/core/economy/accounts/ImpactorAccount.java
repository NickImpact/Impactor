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
import net.impactdev.impactor.api.configuration.Config;
import net.impactdev.impactor.api.configuration.key.ConfigKey;
import net.impactdev.impactor.api.economy.EconomyService;
import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.economy.events.EconomyTransactionEvent;
import net.impactdev.impactor.api.economy.events.EconomyTransferTransactionEvent;
import net.impactdev.impactor.api.economy.transactions.composer.TransactionComposer;
import net.impactdev.impactor.api.economy.transactions.details.EconomyResultType;
import net.impactdev.impactor.api.economy.transactions.EconomyTransaction;
import net.impactdev.impactor.api.economy.transactions.details.EconomyTransactionType;
import net.impactdev.impactor.api.economy.transactions.EconomyTransferTransaction;
import net.impactdev.impactor.api.events.ImpactorEvent;
import net.impactdev.impactor.api.utility.printing.PrettyPrinter;
import net.impactdev.impactor.core.economy.EconomyConfig;
import net.impactdev.impactor.core.economy.ImpactorEconomyService;
import net.impactdev.impactor.core.economy.events.ImpactorEconomyTransactionEvent;
import net.impactdev.impactor.core.economy.events.ImpactorEconomyTransferTransactionEvent;
import net.impactdev.impactor.core.economy.transactions.ImpactorEconomyTransaction;
import net.impactdev.impactor.core.economy.transactions.ImpactorEconomyTransferTransaction;
import net.impactdev.impactor.core.economy.transactions.composers.BaseTransactionComposer;
import net.impactdev.impactor.core.economy.transactions.composers.TransferTransactionComposer;
import net.impactdev.impactor.core.plugin.BaseImpactorPlugin;
import net.impactdev.impactor.core.utility.future.Futures;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.event.PostResult;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public final class ImpactorAccount implements Account {

    private final EconomyService service = Impactor.instance()
            .services()
            .provide(EconomyService.class);

    private final UUID owner;
    private final Currency currency;
    private final boolean virtual;
    private BigDecimal balance;

    private ImpactorAccount(ImpactorAccountBuilder builder) {
        this(builder.currency, builder.owner, builder.virtual, Optional.ofNullable(builder.balance).orElse(builder.currency.defaultAccountBalance()));
    }

    private ImpactorAccount(Currency currency, UUID owner, boolean virtual, BigDecimal balance) {
        this.owner = owner;
        this.currency = currency;
        this.balance = balance;
        this.virtual = virtual;
    }

    public static ImpactorAccount load(Currency currency, UUID uuid, boolean virtual, BigDecimal balance) {
        return new ImpactorAccount(currency, uuid, virtual, balance);
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
    public boolean virtual() {
        return this.virtual;
    }

    @Override
    public @NotNull BigDecimal balance() {
        return this.balance;
    }

    @Override
    public @NotNull EconomyTransaction set(BigDecimal amount) {
        return EconomyTransaction.compose()
                .account(this)
                .type(EconomyTransactionType.SET)
                .amount(amount)
                .build();
    }

    public @NotNull EconomyTransaction set(BaseTransactionComposer composer) {
        final BigDecimal amount = composer.amount();
        return this.enact(amount, EconomyTransactionType.SET, () -> {
                    ImpactorEconomyTransaction.TransactionBuilder builder = ImpactorEconomyTransaction.builder()
                            .account(this)
                            .currency(this.currency)
                            .amount(amount)
                            .type(EconomyTransactionType.SET);

                    EconomyTransactionEvent.Pre pre = this.createAndFirePre(amount, EconomyTransactionType.SET);
                    if(pre.cancelled()) {
                        return this.complete(builder, EconomyResultType.CANCELLED, composer.messages());
                    }

                    boolean restrict = this.restriction(EconomyConfig.APPLY_RESTRICTIONS).orElse(false);
                    if(amount.signum() < 0) {
                        return this.createAndFirePost(this.complete(builder, EconomyResultType.INVALID, composer.messages()));
                    } else if(restrict && this.restriction(EconomyConfig.MAX_BALANCE).map(value -> amount.compareTo(value) > 0).orElse(false)) {
                        return this.createAndFirePost(this.complete(builder, EconomyResultType.INVALID, composer.messages()));
                    } else {
                        this.balance = amount;
                        this.save();
                        return this.createAndFirePost(this.complete(builder, EconomyResultType.SUCCESS, composer.messages()));
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
        return EconomyTransaction.compose()
                .account(this)
                .type(EconomyTransactionType.WITHDRAW)
                .amount(amount)
                .build();
    }

    public @NotNull EconomyTransaction withdraw(BaseTransactionComposer composer) {
        final BigDecimal amount = composer.amount();
        return this.enact(amount, EconomyTransactionType.WITHDRAW, () -> {
                    ImpactorEconomyTransaction.TransactionBuilder builder = ImpactorEconomyTransaction.builder()
                            .account(this)
                            .currency(this.currency)
                            .amount(amount)
                            .type(EconomyTransactionType.WITHDRAW);

                    EconomyTransactionEvent.Pre pre = this.createAndFirePre(amount, EconomyTransactionType.WITHDRAW);
                    if(pre.cancelled()) {
                        return this.complete(builder, EconomyResultType.CANCELLED, composer.messages());
                    }

                    BigDecimal result = this.balance.subtract(amount);
                    if(this.restriction(EconomyConfig.APPLY_RESTRICTIONS).orElse(false)) {
                        Optional<BigDecimal> minimum = this.restriction(EconomyConfig.MIN_BALANCE);
                        if(minimum.isPresent() && minimum.get().compareTo(result) > 0) {
                            return this.createAndFirePost(this.complete(builder, EconomyResultType.NOT_ENOUGH_FUNDS, composer.messages()));
                        }
                    }

                    if(result.signum() < 0) {
                        return this.createAndFirePost(this.complete(builder, EconomyResultType.NOT_ENOUGH_FUNDS, composer.messages()));
                    }

                    this.balance = result;
                    this.save();
                    return this.createAndFirePost(this.complete(builder, EconomyResultType.SUCCESS, composer.messages()));
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
        return EconomyTransaction.compose()
                .account(this)
                .type(EconomyTransactionType.DEPOSIT)
                .amount(amount)
                .build();
    }

    public @NotNull EconomyTransaction deposit(BaseTransactionComposer composer) {
        final BigDecimal amount = composer.amount();
        return this.enact(amount, EconomyTransactionType.DEPOSIT, () -> {
                    ImpactorEconomyTransaction.TransactionBuilder builder = ImpactorEconomyTransaction.builder()
                            .account(this)
                            .currency(this.currency)
                            .amount(amount)
                            .type(EconomyTransactionType.DEPOSIT);

                    EconomyTransactionEvent.Pre pre = this.createAndFirePre(amount, EconomyTransactionType.SET);
                    if(pre.cancelled()) {
                        return this.complete(builder, EconomyResultType.CANCELLED, composer.messages());
                    }

                    BigDecimal result = this.balance.add(amount);
                    if(this.restriction(EconomyConfig.APPLY_RESTRICTIONS).orElse(false)) {
                        Optional<BigDecimal> maximum = this.restriction(EconomyConfig.MAX_BALANCE);
                        if(maximum.isPresent() && maximum.get().compareTo(result) < 0) {
                            return this.createAndFirePost(this.complete(builder, EconomyResultType.NO_REMAINING_SPACE, composer.messages()));
                        }
                    }

                    this.balance = result;
                    this.save();
                    return this.createAndFirePost(this.complete(builder, EconomyResultType.SUCCESS, composer.messages()));
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
        return EconomyTransferTransaction.compose()
                .from(this)
                .to(to)
                .amount(amount)
                .build();
    }

    public @NotNull EconomyTransferTransaction transfer(TransferTransactionComposer composer) {
        final BigDecimal amount = composer.amount();
        final Account to = composer.target();

        return this.enact(amount, EconomyTransactionType.TRANSFER, () -> {
                ImpactorEconomyTransferTransaction.TransactionBuilder builder = ImpactorEconomyTransferTransaction.builder()
                        .currency(this.currency)
                        .from(this)
                        .to(to)
                        .amount(amount);

                if(!this.currency.key().equals(to.currency().key())) {
                    if(!this.restriction(EconomyConfig.ALLOW_TRANSFER_CROSS_CURRENCY).orElse(true)) {
                        return this.complete(builder, EconomyResultType.INVALID, composer.messages());
                    }
                }

                if(composer.amount().doubleValue() < 1) {
                    return this.complete(builder, EconomyResultType.FAILED, composer.messages());
                }

                EconomyTransferTransactionEvent.Pre event = new ImpactorEconomyTransferTransactionEvent.Pre(
                        this.currency,
                        this,
                        to,
                        amount
                );

                this.postAndVerify(event);
                if(event.cancelled()) {
                    return this.complete(builder, EconomyResultType.CANCELLED, composer.messages());
                }

                BigDecimal withdraw = this.balance.subtract(amount);
                BigDecimal deposit = to.balance().add(amount);
                if(this.restriction(EconomyConfig.APPLY_RESTRICTIONS).orElse(false)) {
                    Optional<BigDecimal> minimum = this.restriction(EconomyConfig.MIN_BALANCE);
                    Optional<BigDecimal> maximum = this.restriction(EconomyConfig.MAX_BALANCE);
                    if(maximum.isPresent() && maximum.get().compareTo(deposit) < 0) {
                        EconomyTransferTransactionEvent.Post post = new ImpactorEconomyTransferTransactionEvent.Post(
                                this.complete(builder, EconomyResultType.NO_REMAINING_SPACE, composer.messages())
                        );
                        this.postAndVerify(post);
                        return post.transaction();
                    }

                    if(minimum.isPresent() && minimum.get().compareTo(withdraw) > 0) {
                        EconomyTransferTransactionEvent.Post post = new ImpactorEconomyTransferTransactionEvent.Post(
                                this.complete(builder, EconomyResultType.NOT_ENOUGH_FUNDS, composer.messages())
                        );
                        this.postAndVerify(post);
                        return post.transaction();
                    }
                }

                this.balance = this.balance.subtract(amount);
                ((ImpactorAccount) to).quietSet(to.balance().add(amount));

                this.save();
                ((ImpactorAccount) to).save();

                EconomyTransferTransactionEvent.Post post = new ImpactorEconomyTransferTransactionEvent.Post(
                        this.complete(builder, EconomyResultType.SUCCESS, composer.messages())
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
        return EconomyTransaction.compose()
                .account(this)
                .type(EconomyTransactionType.RESET)
                .build();
    }

    public @NotNull EconomyTransaction reset(BaseTransactionComposer composer) {
        final BigDecimal amount = composer.amount();
        return this.enact(this.currency.defaultAccountBalance(), EconomyTransactionType.RESET, () -> {
                    ImpactorEconomyTransaction.TransactionBuilder builder = ImpactorEconomyTransaction.builder()
                            .account(this)
                            .currency(this.currency)
                            .amount(amount)
                            .type(EconomyTransactionType.DEPOSIT);

                    EconomyTransactionEvent.Pre pre = this.createAndFirePre(amount, EconomyTransactionType.RESET);
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
        this.service.save(this);
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

            Impactor.instance().scheduler().sync().execute(() -> printer.log(BaseImpactorPlugin.instance().logger(), PrettyPrinter.Level.ERROR));
            return fallback.get();
        }
    }

    private <T> Optional<T> restriction(ConfigKey<T> key) {
        EconomyService service = Impactor.instance().services().provide(EconomyService.class);
        if(service instanceof ImpactorEconomyService) {
            Config config = ((ImpactorEconomyService) service).config();
            return Optional.of(config.get(key));
        }

        return Optional.empty();
    }

    private EconomyTransaction complete(ImpactorEconomyTransaction.TransactionBuilder builder, EconomyResultType type, Map<EconomyResultType, Supplier<Component>> messages) {
        return builder.result(type).message(messages.getOrDefault(type, null)).build();
    }

    private EconomyTransferTransaction complete(ImpactorEconomyTransferTransaction.TransactionBuilder builder, EconomyResultType type, Map<EconomyResultType, Supplier<Component>> messages) {
        return builder.result(type).message(messages.getOrDefault(type, null)).build();
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
        private boolean virtual;

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
        public @NotNull AccountBuilder virtual() {
            this.virtual = true;
            return this;
        }

        public AccountBuilder overrideVirtuality(boolean value) {
            this.virtual = value;
            return this;
        }

        @Override
        public Account build() {
            return new ImpactorAccount(this);
        }
    }
}
