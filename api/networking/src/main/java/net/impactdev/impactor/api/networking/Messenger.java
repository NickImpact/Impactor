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

package net.impactdev.impactor.api.networking;

import net.impactdev.impactor.api.networking.messages.Message;
import net.impactdev.impactor.api.networking.messages.MessageConsumer;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an element capable of dispatching {@link Message Messages} across the
 * network.
 *
 * @since 6.0.0
 */
public interface Messenger {

    /**
     * Dispatches the given message across the networking service.
     *
     * @param message The message to publish
     * @since 6.0.0
     */
    void publish(final @NotNull Message message);

    /**
     * Performs the necessary actions needed to gracefully shutdown the messenger.
     *
     * @since 6.0.0
     */
    default void shutdown() { }

    /**
     * Represents a provider for {@link Messenger} instances.
     *
     * <p>Should a plugin wish to provide a method not already supported, this is how
     * one should create the messenger.</p>
     *
     * @since 6.0.0
     */
    interface Provider {

        /**
         * Specifies the name of the messenger that will be provided.
         *
         * @return The name of the provided messenger
         * @since 6.0.0
         */
        @NotNull String name();

        /**
         * Creates a new {@link Messenger} instance, using the given consumer for messages received.
         *
         * @return A messenger capable of processing inbound and outbound messages
         * @since 6.0.0
         */
        @NotNull Messenger obtain(final @NotNull MessageConsumer consumer);
    }
}
