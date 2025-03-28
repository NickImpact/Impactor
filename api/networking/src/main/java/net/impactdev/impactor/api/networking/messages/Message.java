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

package net.impactdev.impactor.api.networking.messages;

import net.impactdev.impactor.api.networking.Messenger;
import net.impactdev.impactor.api.networking.response.ErrorCode;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents a message that can be sent via a {@link Messenger}. Messages are expected
 * to be identifiable via a unique ID, allowing instances to ensure that an incoming
 * message hasn't yet already been received.
 *
 * @since 6.0.0
 */
public interface Message {

    /**
     * Specifies an identifying key that denotes the type of message being handled to processors.
     *
     * @return The key indicating the type of message being relayed
     * @since 6.0.0
     */
    Key key();

    /**
     * Specifies the unique ID associated with this message.
     *
     * <p>This ID helps to ensure that a receiver does not process the same message twice.</p>
     *
     * @return The unique ID of this message
     * @since 6.0.0
     */
    @NotNull UUID id();

    /**
     * A type of message used to indicate a stateful response is necessary to ensure synchronization is maintained.
     *
     * <br><br>
     * <p>For example, lets say we have a scenario where two servers act against the same database. Conveniently,
     * both servers fire a request to act on the same point of data. The question now becomes, which goes first,
     * and depending on that result, should the second request still apply?</p>
     * <br>
     * <p>In essence, request messages are the primary root for evaluation, and are built around a response message
     * which details the actual result of the request. </p>
     *
     * @since 6.0.0
     */
    interface Request extends Message {

        /**
         * Specifies the type of response message this request expects from the responding networking service.
         *
         * @return The type of response this message awaits
         * @since 6.0.0
         */
        Class<? extends Response> awaits();

    }

    /**
     * A type of message built from a request. These messages are to be handled only by the responding service,
     * and are used to indicate how a particular request performed.
     *
     * @since 6.0.0
     */
    interface Response extends Message {

        /**
         * Represents the UUID of the request message which spawned this response. This is essential for ensuring
         * receiving services process a response accordingly, ensuring the response received lines up with an awaiting
         * request.
         *
         * @return The UUID of the spawning request
         * @since 6.0.0
         */
        UUID request();

        /**
         * Specifies the duration of time it took for the response to be crafted and relayed back to the requesting
         * service.
         *
         * @return The duration of the response evaluation
         * @since 6.0.0
         */
        Duration duration();

        /**
         * Details if the request processed was evaluated successfully or was blocked. This comes down to an
         * implementation detail on the handler, rather than the message processor itself.
         *
         * @return Indicates if the message was successful.
         * @since 6.0.0
         */
        boolean successful();

        /**
         * An optionally provided error code, providing details on why a particular message might have failed
         * to process properly. It's expected that if an error is provided, the message itself will also be marked
         * unsuccessful.
         *
         * @return An optionally wrapped error code, if any were captured
         * @since 6.0.0
         */
        Optional<ErrorCode> error();

    }

}
