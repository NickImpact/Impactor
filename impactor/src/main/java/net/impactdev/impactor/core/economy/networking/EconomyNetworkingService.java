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

package net.impactdev.impactor.core.economy.networking;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rockbb.jedis.toolkit.JedisLock;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.economy.events.EconomyTransactionEvent;
import net.impactdev.impactor.api.economy.events.EconomyTransferTransactionEvent;
import net.impactdev.impactor.api.economy.transactions.EconomyTransaction;
import net.impactdev.impactor.api.economy.transactions.EconomyTransferTransaction;
import net.impactdev.impactor.api.logging.PluginLogger;
import net.impactdev.impactor.core.economy.accounts.AccountManager;
import net.impactdev.impactor.core.economy.accounts.ImpactorAccount;
import net.impactdev.impactor.core.economy.networking.consumption.MessageConsumer;
import net.impactdev.impactor.core.economy.networking.messages.Message;
import net.impactdev.impactor.core.economy.networking.messages.TransactionMessage;
import net.impactdev.impactor.core.economy.networking.messages.TransferTransactionMessage;
import net.impactdev.impactor.core.economy.networking.messenger.Messenger;
import net.impactdev.impactor.core.economy.transactions.context.TransactionContext;
import net.impactdev.impactor.core.economy.transactions.context.TransferTransactionContext;
import net.impactdev.impactor.core.plugin.BaseImpactorPlugin;
import net.impactdev.impactor.core.utility.collections.ExpiringSet;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public final class EconomyNetworkingService implements MessageConsumer {

    private final AccountManager manager;
    private final PluginLogger logger;
    private final Messenger messenger;

    private final ExpiringSet<UUID> received = new ExpiringSet<>(5, TimeUnit.MINUTES);

    // Holds Jedis Locks for transactions in order to release them as soon as possible
    private final Cache<UUID, JedisLock> accountLocks;

    public EconomyNetworkingService(final BaseImpactorPlugin plugin, final AccountManager manager, final Messenger.Provider provider) {
        this.logger = plugin.logger();
        this.manager = manager;
        this.messenger = provider.obtain(this);

        this.accountLocks = Caffeine.newBuilder()
                .expireAfterAccess(10, TimeUnit.SECONDS)
                .removalListener((UUID key, JedisLock lock, RemovalCause cause) -> {
                    if (lock != null) lock.release();
                })
                .build();

        // Lock Requests
        Impactor.instance().events().subscribe(EconomyTransactionEvent.Pre.class, event -> {
            if (!acquireAccountLock(event.account().owner())) event.cancelled(true);
        });

        Impactor.instance().events().subscribe(EconomyTransferTransactionEvent.Pre.class, event -> {
            if (!acquireAccountLock(event.from().owner())) event.cancelled(true);
            if (!acquireAccountLock(event.to().owner())) event.cancelled(true);
        });

        // Transaction Messaging
        Impactor.instance().events().subscribe(EconomyTransactionEvent.Post.class, event -> {
            releaseAccountLock(event.account().owner());

            this.publishTransaction(event.transaction());
        });

        Impactor.instance().events().subscribe(EconomyTransferTransactionEvent.Post.class, event -> {
            releaseAccountLock(event.from().owner());
            releaseAccountLock(event.to().owner());

            this.publishTransaction(event.transaction());
        });
    }

    public void shutdown() {
        this.messenger.shutdown();
    }

    public CompletableFuture<Void> publishTransaction(final EconomyTransaction transaction) {
        return CompletableFuture.runAsync(() -> {
            UUID id = this.generateID();
            this.logger.debug("Publishing transaction with ID: " + id);

            this.messenger.publish(new TransactionMessage(id, transaction));
        });
    }

    public CompletableFuture<Void> publishTransaction(final EconomyTransferTransaction transaction) {
        return CompletableFuture.runAsync(() -> {
            UUID id = this.generateID();
            this.logger.debug("Publishing transaction with ID: " + id);

            this.messenger.publish(new TransferTransactionMessage(id, transaction));
        });
    }

    private UUID generateID() {
        UUID uuid = UUID.randomUUID();
        this.received.add(uuid);

        return uuid;
    }

    @Override
    public void consume(@NotNull Message message) {
        Objects.requireNonNull(message, "message cannot be null");

        if(!this.received.add(message.id())) {
            return;
        }

        this.processIncomingMessage(message);
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public void consume(@NotNull JsonElement json) {
        Objects.requireNonNull(json, "json cannot be null");
        JsonObject root = json.getAsJsonObject();

        JsonElement id = root.get("id");
        if(id == null) {
            throw new IllegalStateException("Incoming message has no ID argument: " + root);
        }

        UUID uuid = UUID.fromString(id.getAsString());
        if(!this.received.add(uuid)) {
            return;
        }

        JsonElement type = root.get("key");
        if(type == null) {
            throw new IllegalStateException("Incoming message has no key argument: " + root);
        }

        Key key = Key.key(type.getAsString());
        JsonElement content = root.get("transaction");

        Message message = this.deserialize(uuid, key, content);
        this.processIncomingMessage(message);
    }

    private Message deserialize(UUID id, Key key, JsonElement content) {
        if(key.equals(TransactionMessage.KEY)) {
            return TransactionMessage.deserialize(id, content);
        } else {
            return TransferTransactionMessage.deserialize(id, content);
        }
    }

    private void processIncomingMessage(final @NotNull Message message) {
        if(message instanceof TransactionMessage transaction) {
            TransactionContext context = transaction.context();
            this.manager.accountIfPresent(context.account(), context.currency())
                    .map(x -> (ImpactorAccount) x)
                    .ifPresent(x -> this.manager.invalidate(x.owner(), x.currency()));
        } else if(message instanceof TransferTransactionMessage transaction) {
            TransferTransactionContext context = transaction.context();
            this.manager.accountIfPresent(context.from(), context.currency())
                    .map(x -> (ImpactorAccount) x)
                    .ifPresent(x -> this.manager.invalidate(x.owner(), x.currency()));
            this.manager.accountIfPresent(context.to(), context.currency())
                    .map(x -> (ImpactorAccount) x)
                    .ifPresent(x -> this.manager.invalidate(x.owner(), x.currency()));
        }
    }

    public boolean acquireAccountLock(UUID uuid) {
        JedisLock lock = messenger.obtainLock(uuid);

        if (lock.acquire()) {
            accountLocks.put(uuid, lock);
            return true;
        } else {
            logger.severe("Failed to acquire lock for account: " + uuid);
            return false;
        }
    }

    public void releaseAccountLock(UUID uuid) {
        accountLocks.invalidate(uuid);
    }
}
