package net.impactdev.impactor.api.economy.builtin.transactions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.impactdev.impactor.api.core.json.JObject;
import net.impactdev.impactor.api.economy.EconomyService;
import net.impactdev.impactor.api.economy.accounts.Account;
import net.impactdev.impactor.api.economy.accounts.AccountManager;
import net.impactdev.impactor.api.economy.currency.Currency;
import net.impactdev.impactor.api.economy.currency.CurrencyProvider;
import net.impactdev.impactor.api.economy.transactions.details.TransactionType;
import net.kyori.adventure.key.Key;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionRequest(Currency currency, Account account, BigDecimal amount, TransactionType type, Instant timestamp) {

    public JsonElement serialize() {
        JObject transaction = new JObject();
        transaction.add("currency", this.currency().key().toString());
        transaction.add("account", this.account().owner().toString());
        transaction.add("type", this.type().ordinal());
        transaction.add("amount", this.amount());
        transaction.add("timestamp", this.timestamp().toString());

        return transaction.toJson();
    }

    @SuppressWarnings("PatternValidation")
    public static TransactionRequest deserialize(JsonElement json) {
        final JsonObject data = json.getAsJsonObject();

        final CurrencyProvider provider = EconomyService.service().currencies();
        final Currency currency = provider.currency(Key.key(data.get("currency").getAsString())).orElseThrow();

        final AccountManager accounts = EconomyService.service().accounts();

        return new TransactionRequest(
                currency,
                accounts.getOrCreate(UUID.fromString(data.get("account").getAsString()), currency),
                BigDecimal.valueOf(data.get("amount").getAsDouble()),
                TransactionType.values()[data.get("type").getAsInt()],
                Instant.parse(data.get("timestamp").getAsString())
        );
    }

}
