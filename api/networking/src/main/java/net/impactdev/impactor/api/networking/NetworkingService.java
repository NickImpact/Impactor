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

import net.impactdev.impactor.api.networking.messages.MessageConsumer;
import net.impactdev.impactor.api.networking.messages.MessageRegistry;

/**
 * A networking service represents the way aspects of code can communicate across a networking connection.
 *
 * @since 6.0.0
 */
public interface NetworkingService {

    /**
     * Indicates the messenger responsible for the publishing of messages across the networking service.
     *
     * @return The networking service's messenger
     * @since 6.0.0
     */
    Messenger messenger();

    /**
     * Represents the consumer responsible for processing inbound messages received by the {@link Messenger}.
     *
     * @return The inbound message consumer
     * @since 6.0.0
     */
    MessageConsumer consumer();

    /**
     * Represents the registry of all possible messages that can occur through a networking service.
     *
     * @return A registry of different message types
     * @since 6.0.0
     */
    MessageRegistry registry();

    /**
     * Responsible for gracefully shutting down the networking service.
     *
     * @since 6.0.0
     */
    void shutdown();

}
