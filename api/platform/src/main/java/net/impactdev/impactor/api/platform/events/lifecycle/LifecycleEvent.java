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

package net.impactdev.impactor.api.platform.events.lifecycle;

import net.impactdev.impactor.api.platform.Platform;
import net.impactdev.impactor.api.platform.Server;
import org.spongepowered.eventgen.annotations.GenerateFactoryMethod;

/**
 * Represents an event in which aspects of the server lifecycle occur. For instance, this should include
 * mod construction time, as well as lifecycle instances such as the server starting up and shutting down.
 */
public interface LifecycleEvent {

    /**
     * Specifies the platform the lifecycle is occurring against. This should typically represent details
     * about the server, such as its mod loader and implementation details.
     *
     * @return The platform of the mod loading instance
     */
    Platform platform();

    /**
     * Represents the event where the server has begun starting. At this phase, the server has not been
     * fully opened to the public, but is finalizing startup procedures.
     *
     * <p>This event is typically where you should perform standard pre-init logic that should be complete
     * before the server has been opened.</p>
     */
    @GenerateFactoryMethod
    interface ServerStarting extends LifecycleEvent {

        /**
         * A mirror to the server instance.
         *
         * @return The server
         */
        Server server();

    }

    /**
     *
     *
     *
     */
    @GenerateFactoryMethod
    interface ServerStarted extends LifecycleEvent {

        Server server();

    }

}
