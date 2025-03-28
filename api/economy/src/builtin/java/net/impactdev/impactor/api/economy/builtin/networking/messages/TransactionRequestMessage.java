package net.impactdev.impactor.api.economy.builtin.networking.messages;

import com.google.auto.service.AutoService;
import net.impactdev.impactor.api.economy.builtin.transactions.TransactionRequest;
import net.impactdev.impactor.api.networking.messages.Message;
import net.impactdev.impactor.api.networking.messages.MessageCodec;
import net.kyori.adventure.key.Key;

import java.util.UUID;

@AutoService(Message.class)
public record TransactionRequestMessage(UUID id, TransactionRequest transaction) implements Message.Request {

    public static final Key KEY = Key.key("economy", "transaction/request");
    public static final MessageCodec<TransactionRequestMessage> CODEC = MessageCodec.create(message -> message.transaction.serialize(), (id, content) -> new TransactionRequestMessage(id, TransactionRequest.deserialize(content.getAsJsonObject())));

    @Override
    public Key key() {
        return KEY;
    }

    @Override
    public Class<? extends Response> awaits() {
        return TransactionResponseMessage.class;
    }

}
