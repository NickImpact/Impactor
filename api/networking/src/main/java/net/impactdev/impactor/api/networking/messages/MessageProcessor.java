package net.impactdev.impactor.api.networking.messages;

import org.jetbrains.annotations.NotNull;

/**
 * Responsible for processing messages as they are consumed by the {@link MessageConsumer}. While a
 * {@link MessageConsumer} is responsible for decoding a message, and ensuring a message hasn't been
 * re-encountered, the processor is responsible for handling a message which has been decoded and
 * confirmed to be unique.
 *
 * @see MessageConsumer
 * @since 6.0.0
 */
@FunctionalInterface
public interface MessageProcessor<M extends Message> {

    /**
     * Responsible for processing a message as provided by the incoming message consumer.
     *
     * @param message The message to process
     * @since 6.0.0
     */
    void process(final @NotNull M message);

}
