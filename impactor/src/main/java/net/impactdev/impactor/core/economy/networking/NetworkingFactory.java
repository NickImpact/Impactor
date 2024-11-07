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

package net.impactdev.impactor.core.economy.networking;

import net.impactdev.impactor.api.configuration.Config;
import net.impactdev.impactor.api.logging.PluginLogger;
import net.impactdev.impactor.core.economy.EconomyConfig;
import net.impactdev.impactor.core.economy.ImpactorEconomyService;
import net.impactdev.impactor.core.economy.accounts.AccountManager;
import net.impactdev.impactor.core.economy.networking.messenger.redis.RedisConfig;
import net.impactdev.impactor.core.economy.networking.messenger.redis.RedisProvider;
import net.impactdev.impactor.core.plugin.BaseImpactorPlugin;

import java.util.Locale;

public final class NetworkingFactory {

    private final BaseImpactorPlugin plugin;
    private final ImpactorEconomyService service;

    public NetworkingFactory(final BaseImpactorPlugin plugin, final ImpactorEconomyService service) {
        this.plugin = plugin;
        this.service = service;
    }

    public EconomyNetworkingService create(AccountManager manager) {
        final PluginLogger logger = this.plugin.logger();
        Config config = this.service.config();
        String request = config.get(EconomyConfig.MESSAGING_SERVICE);

        if(request.equals("none")) {
            request = "auto";
        }

        if(request.equals("auto")) {
            RedisConfig redis = config.get(EconomyConfig.REDIS);
            if(redis.enabled()) {
                request = "redis";
            }
        }

        if(request.equals("auto")) {
            this.plugin.logger().warn("Couldn't find a suitable networking setup, no networking will be performed...");
            return null;
        }

        logger.info("Loading messaging service... [" + request.toUpperCase(Locale.ROOT) + "]");
        EconomyNetworkingService service = this.createByRequest(request, manager);
        if(service != null) {
            return service;
        }

        throw new IllegalStateException("Couldn't find a suitable networking setup");
    }

    private EconomyNetworkingService createByRequest(final String request, final AccountManager manager) {
        if(request.equals("redis")) {
            RedisConfig redis = this.service.config().get(EconomyConfig.REDIS);
            if(redis.enabled()) {
                try {
                    return new EconomyNetworkingService(this.plugin, manager, new RedisProvider(this.plugin.logger(), redis));
                } catch (final Exception e) {
                    this.plugin.logger().severe("Encountered an exception while enabling Redis messaging service...", e);
                }
            } else {
                this.plugin.logger().warn("Messaging service was set to redis, but redis is disabled...");
            }
        }

        return null;
    }

}
