package net.impactdev.impactor.api.economy.builtin.networking.messages;

import com.google.auto.service.AutoService;
import com.google.gson.JsonObject;
import net.impactdev.impactor.api.economy.transactions.EconomyTransaction;
import net.impactdev.impactor.api.networking.messages.Message;
import net.impactdev.impactor.api.networking.messages.MessageCodec;
import net.impactdev.impactor.api.networking.response.ErrorCode;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@AutoService(Message.class)
public record TransactionResponseMessage(UUID uuid, UUID request, Duration duration,
                                         EconomyTransaction transaction) implements Message.Response {

    public static final Key KEY = Key.key("economy", "transaction/response");
    public static final MessageCodec<TransactionResponseMessage> CODEC = MessageCodec.create(
            message -> new JsonObject(), (id, content) -> {
                throw new UnsupportedOperationException("Awaiting implemenetation");
            }
    );

    @Override
    public Key key() {
        return KEY;
    }

    @Override
    public @NotNull UUID id() {
        return this.uuid;
    }

    public EconomyTransaction transaction() {
        return this.transaction;
    }

    @Override
    public UUID request() {
        return this.request;
    }

    @Override
    public Duration duration() {
        return this.duration;
    }

    @Override
    public boolean successful() {
        return false;
    }

    @Override
    public Optional<ErrorCode> error() {
        return Optional.empty();
    }

}
