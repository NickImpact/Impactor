package net.impactdev.impactor.api.networking.messages;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents the method of serialization and deserialization for a particular message.
 *
 * @since 6.0.0
 */
public record MessageCodec<M extends Message>(MessageEncoder<M> encoder, MessageDecoder<M> decoder) {

    public static <M extends Message> MessageCodec<M> create(MessageEncoder<M> encoder, MessageDecoder<M> decoder) {
        return new MessageCodec<>(encoder, decoder);
    }

    /**
     * Encodes relative details on the message to JSON. Aspects of a message like {@link Message#key()} and
     * {@link Message#id()} will be written to the json automatically, with the resulting element being
     * written under the "content" tag.
     *
     * @param message The message to encode
     * @return A JSON element representing the message content
     */
    public JsonElement encode(@NotNull M message) {
        return this.encoder.encode(message);
    }

    /**
     * @param uuid The UUID of the incoming message
     * @param json The actual content layer of the message
     * @return A decoded message built with the content
     */
    public M decode(final @NotNull UUID uuid, final @NotNull JsonElement json) {
        return this.decoder.decode(uuid, json);
    }

    @FunctionalInterface
    public interface MessageEncoder<M extends Message> {

        JsonElement encode(final @NotNull M message);

    }

    @FunctionalInterface
    public interface MessageDecoder<M extends Message> {

        M decode(final @NotNull UUID uuid, final @NotNull JsonElement json);

    }

}
