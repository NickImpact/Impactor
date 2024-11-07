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

package net.impactdev.impactor.core.economy.networking.messenger.redis;

import net.impactdev.impactor.api.logging.PluginLogger;
import net.impactdev.impactor.core.economy.networking.consumption.MessageConsumer;
import net.impactdev.impactor.core.economy.networking.messenger.Messenger;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

public final class RedisProvider implements Messenger.Provider {

    private final PluginLogger logger;
    private final RedisConfig config;

    public RedisProvider(final PluginLogger logger, final RedisConfig config) {
        this.logger = logger;
        this.config = config;
    }

    @Override
    public @NotNull Messenger obtain(MessageConsumer consumer) {
        RedisMessenger messenger = new RedisMessenger(this.logger, Key.key("impactor:economy"), consumer);
        messenger.initialize(this.config);

        return messenger;
    }
}
