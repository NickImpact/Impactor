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

package net.impactdev.impactor.api.events;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Details the results of a published event via {@link Event#publish(Object)}.
 *
 * @since 6.0.0
 */
public abstract class PublicationResult {

    PublicationResult() {
        if(!(this instanceof Successful || this instanceof Failure)) {
            throw new IllegalStateException();
        }
    }

    /**
     * Returns a publication result indicating no exception were thrown by subscribers.
     *
     * @return A {@link PublicationResult} indicating successful publication
     * @since 6.0.0
     */
    public static Successful successful() {
        return Successful.INSTANCE;
    }

    /**
     * Returns a publication result which details exceptions thrown by an event's subscribers.
     *
     * @param exceptions A mapping of a subscription to its thrown exception
     * @return A {@link PublicationResult} indicating failed publication
     * @since 6.0.0
     */
    public static Failure failure(final @NotNull Map<EventSubscription<?>, Throwable> exceptions) {
        if(exceptions.isEmpty()) {
            throw new IllegalStateException("No exceptions provided");
        }

        return new Failure(ImmutableMap.copyOf(exceptions));
    }

    /**
     * Details if the total publication to subscribers via {@link Event#publish(Object)} was successful.
     *
     * @return <code>true</code> on success, <code>false</code> otherwise
     * @since 6.0.0
     */
    public abstract boolean wasSuccessful();

    /**
     * Specifies the mapping of exceptions encountered during event publication. While this will never be
     * null, a successful publication will always result in an empty map.
     *
     * @return The mapping of subscriptions to exceptions thrown
     * @since 6.0.0
     */
    public abstract @NotNull Map<EventSubscription<?>, Throwable> exceptions();

    /**
     * Forcibly triggers an {@link EventPublicationException} should the event have been marked unsuccessful.
     *
     * @throws EventPublicationException If the publication was marked unsuccessful
     * @since 6.0.0
     */
    public abstract void raise() throws EventPublicationException;

    /**
     * Represents the successful state for an event publication. With this implementation, no exceptions
     * were raised and all details should promote success.
     *
     * @since 6.0.0
     */
    public static final class Successful extends PublicationResult {

        static final Successful INSTANCE = new Successful();

        @Override
        public boolean wasSuccessful() {
            return true;
        }

        @Override
        public @NotNull Map<EventSubscription<?>, Throwable> exceptions() {
            return Map.of();
        }

        @Override
        public void raise() {}

    }

    /**
     * Represents the failure state for an event publication. With this implementation, we can safely
     * expect exceptions to be provided, as well as be able to forcibly raise an exception via {@link #raise()}
     *
     * @since 6.0.0
     */
    public static final class Failure extends PublicationResult {

        private final Map<EventSubscription<?>, Throwable> exceptions;

        private Failure(final @NotNull Map<EventSubscription<?>, Throwable> exceptions) {
            this.exceptions = exceptions;
        }

        @Override
        public boolean wasSuccessful() {
            return false;
        }

        @Override
        public @NotNull Map<EventSubscription<?>, Throwable> exceptions() {
            return this.exceptions;
        }

        @Override
        public void raise() throws EventPublicationException {
            throw new EventPublicationException(this);
        }

    }

    /**
     * Represents an exception encapsulating the resulting exceptions as part of a
     * {@link Failure failed event publication}.
     *
     * @since 6.0.0
     */
    public static final class EventPublicationException extends Exception {

        private final PublicationResult publication;

        EventPublicationException(final @NotNull PublicationResult publication) {
            this.publication = publication;
        }

        /**
         * Provides the publication that created this exception
         *
         * @return The publication details
         * @since 6.0.0
         */
        public @NotNull PublicationResult result() {
            return this.publication;
        }

        /**
         * Prints all the stack traces involved in the exception. This will write exceptions generally
         * to "Standard Error", in the typical Java exception stacktrace publication. If you wish to have
         * fine-grained exception printing, consider processing the exceptions yourself as desired.
         *
         * @see Exception#printStackTrace()
         * @since 6.0.0
         */
        public void printAllTraces() {
            this.printStackTrace();
            for (final Throwable exception : this.result().exceptions().values()) {
                exception.printStackTrace();
            }
        }
    }

}
