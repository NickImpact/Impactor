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

package net.impactdev.impactor.api.scoreboard.events;

import net.impactdev.impactor.api.scoreboard.components.Updatable;
import net.impactdev.impactor.api.scoreboard.frames.types.ListeningFrame;
import net.impactdev.impactor.api.utilities.functional.TriFunction;

import java.util.UUID;

/**
 * Represents a bus responsible for handling the event processes of a particular line/objective on a scoreboard.
 * This is meant to allow for event processes outside the normal platform bus. So, for servers using SpongeForge,
 * you can have a bus that represents Sponge, a bus that represents Forge, and since it's included, a bus that
 * represents Impactor.
 *
 * @param <L> The type of event being processed, typically the parent to all events represented by the event bus
 */
public interface Bus<L> {

    /**
     * Gets the ID of the bus. This is provided as a utility for debugging purposes.
     *
     * @return The ID of the bus
     */
    String getID();

    /**
     * Creates a function responsible for supplying an updatable scoreboard instance alongside an event handler
     * that creates a mapped reference to a {@link RegisteredEvent}.
     *
     * @param type The type of event being listened to
     * @param <E> A sub-event that extends the event type specified by this bus
     * @return A function that returns a {@link RegisteredEvent} given an updatable scoreboard instance alongside
     * an event handler as parameters.
     */
    <E extends L> TriFunction<Updatable, UUID, ListeningFrame.EventHandler<E>, RegisteredEvent> getRegisterHandler(Class<E> type);

    /**
     * Stops any currently registered event given via the event registration from being listened on further.
     * Particularly useful for player disconnects as well as frame switches on an animation where further event
     * listening is no longer required.
     *
     * @param registration The event registration
     */
    void shutdown(RegisteredEvent registration);

}
