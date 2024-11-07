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

package net.impactdev.impactor.core.economy.networking.messages;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.impactdev.impactor.api.economy.EconomyService;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.economy.transactions.EconomyTransaction;
import net.impactdev.impactor.api.economy.transactions.details.EconomyTransactionType;
import net.impactdev.impactor.core.economy.transactions.context.TransactionContext;
import net.impactdev.json.JObject;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public final class TransactionMessage implements Message {

    public static final Key KEY = Key.key("impactor:transaction/normal");

    private final UUID id;
    private final TransactionContext transaction;

    public TransactionMessage(final UUID id, final EconomyTransaction transaction) {
        this.id = id;
        this.transaction = TransactionContext.from(transaction);
    }

    private TransactionMessage(final UUID id, final TransactionContext transaction) {
        this.id = id;
        this.transaction = transaction;
    }

    @Override
    public Key key() {
        return KEY;
    }

    @Override
    public @NotNull UUID id() {
        return this.id;
    }

    public TransactionContext context() {
        return this.transaction;
    }

    @Override
    public JsonElement serialized() {
        JObject transaction = new JObject()
                .add("currency", this.transaction.currency().key().asString())
                .add("account", this.transaction.account().toString())
                .add("amount", this.transaction.amount())
                .add("type", this.transaction.type().ordinal());

        JObject json = new JObject()
                .add("id", this.id.toString())
                .add("key", this.key().asString())
                .add("transaction", transaction);

        return json.toJson();
    }

    @SuppressWarnings("PatternValidation")
    public static TransactionMessage deserialize(final UUID id, final JsonElement json) {
        Objects.requireNonNull(json, "json");

        JsonObject content = json.getAsJsonObject();
        Currency currency = EconomyService.instance().currencies()
                .currency(Key.key(content.get("currency").getAsString()))
                .orElseThrow(() -> new IllegalStateException("Invalid currency: " + content.get("currency").getAsString()));

        UUID account = UUID.fromString(content.get("account").getAsString());
        BigDecimal amount = new BigDecimal(content.get("amount").getAsString());
        EconomyTransactionType type = EconomyTransactionType.valueOf(content.get("type").getAsString());

        return new TransactionMessage(id, new TransactionContext(
                currency,
                account,
                type,
                amount
        ));
    }
}
