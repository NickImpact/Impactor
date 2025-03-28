package net.impactdev.impactor.api.economy.builtin.networking;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.impactdev.impactor.api.core.collections.ExpiringSet;
import net.impactdev.impactor.api.economy.builtin.networking.messages.TransactionResponseMessage;
import net.impactdev.impactor.api.networking.messages.Message;
import net.impactdev.impactor.api.networking.messages.MessageCodec;
import net.impactdev.impactor.api.networking.messages.MessageConsumer;
import net.impactdev.impactor.api.networking.NetworkingService;
import net.kyori.adventure.key.Key;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

final class EconomyMessageConsumer implements MessageConsumer {

    private final Logger logger;
    private final NetworkingService service;

    private final ExpiringSet<UUID> received = new ExpiringSet<>(5, TimeUnit.MINUTES);

    EconomyMessageConsumer(final @NotNull Logger logger, final @NotNull NetworkingService service) {
        this.logger = logger;
        this.service = Objects.requireNonNull(service);
    }

    @Override
    public void consume(@NotNull Message message) {
        Objects.requireNonNull(message, "Message cannot be null");
        if (this.received.add(message.id())) {
            this.service.processor().process(message);
        }
    }

    @Override
    @SuppressWarnings("PatternValidation")
    public void consume(@NotNull JsonElement json) {
        Objects.requireNonNull(json, "JSON data cannot be null");
        JsonObject root = json.getAsJsonObject();

        UUID uuid = this.request(root, "uuid", element -> UUID.fromString(element.getAsString()));
        if (!this.received.add(uuid)) {
            return;
        }

        Key key = this.request(root, "key", element -> Key.key(element.getAsString()));
        JsonObject content = root.getAsJsonObject("content");

        MessageCodec<?> codec = this.service.registry().codec(key);
        this.service.processor().process(codec.decode(uuid, content));
    }

    private void process(@NotNull Message message) {
        this.logger.debug("Processing message: {} ({})", message.id(), message.key());
        if (message instanceof TransactionResponseMessage response) {
            UUID request = response.request();
            Optional.ofNullable(this.requests.remove(request)).ifPresent(action -> action.accept(response));
        }
    }

    UUID generateForOutgoing() {
        UUID uuid = UUID.randomUUID();
        this.received.add(uuid);

        return uuid;
    }

    private <T> T request(JsonObject json, String key, Function<JsonElement, T> transformer) {
        JsonElement element = json.get(key);
        if (element == null) {
            throw new IllegalArgumentException("JSON lacking requested field: " + key);
        }

        return transformer.apply(element);
    }

}
