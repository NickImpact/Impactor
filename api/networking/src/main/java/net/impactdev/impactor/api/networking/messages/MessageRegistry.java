package net.impactdev.impactor.api.networking.messages;

import net.kyori.adventure.key.Key;

public interface MessageRegistry {

    /**
     * Registers a message using the given key, binding the given codec to aid in message encoding and decoding.
     *
     * @param key   The key for the message typing
     * @param codec The codec responsible for encoding and decoding a message
     * @since 6.0.0
     */
    <M extends Message> void register(Key key, MessageCodec<M> codec);

    /**
     * Locates and provides the codec which matches the given key.
     *
     * @param key The key mapped to a particular codec
     * @param <M> The message type
     * @return The codec mapped to the given key
     * @throws IllegalArgumentException If the key provided does not match an entry within the registry
     * @since 6.0.0
     */
    <M extends Message> MessageCodec<M> codec(Key key);

}
