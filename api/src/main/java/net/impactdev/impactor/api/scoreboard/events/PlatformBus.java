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

import net.impactdev.impactor.api.Impactor;

import java.util.Optional;

/**
 * Represents the event bus that coincides with the target platform of the server.
 *
 * <p>This is relative to the providing platform type of Impactor. For instance, when using the Sponge
 * version of Impactor, this bus will be based on the Sponge event bus, regardless of forge being on
 * the workspace via SpongeForge.
 */
public abstract class PlatformBus<L> implements Bus<L> {

    private static PlatformBus<?> instance;

    public static <L> PlatformBus<L> getOrCreate() {
        return Optional.ofNullable((PlatformBus<L>) instance).orElseGet(() -> {
            PlatformBus<L> result = Impactor.getInstance().getPlatform().createPlatformBus();
            instance = result;
            return result;
        });
    }

    @Override
    public String getID() {
        return "Platform - " + this.getPlatformType();
    }

    protected abstract String getPlatformType();

}
