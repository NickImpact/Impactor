package net.impactdev.impactor.api.economy.builtin.networking;

import net.impactdev.impactor.api.networking.messages.Message;
import net.impactdev.impactor.api.networking.messages.MessageCodec;
import net.impactdev.impactor.api.networking.messages.MessageRegistry;
import net.kyori.adventure.key.Key;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

class EconomyMessageRegistry implements MessageRegistry {

    private final Logger logger;
    private final Map<Key, MessageCodec<?>> codecs = new HashMap<>();

    EconomyMessageRegistry(final Logger logger) {
        this.logger = logger;
    }

    @Override
    public <M extends Message> void register(Key key, MessageCodec<M> codec) {
        this.logger.debug("Registering new message: {}", key.toString());
        this.codecs.put(key, codec);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <M extends Message> MessageCodec<M> codec(Key key) {
        if (!this.codecs.containsKey(key)) {
            throw new IllegalArgumentException("No codec registered for key: " + key.toString());
        }

        return (MessageCodec<M>) this.codecs.get(key);
    }

}
