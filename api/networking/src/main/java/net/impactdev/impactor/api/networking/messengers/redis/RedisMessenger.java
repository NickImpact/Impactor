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

package net.impactdev.impactor.api.networking.messengers.redis;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.impactdev.impactor.api.networking.messages.Message;
import net.impactdev.impactor.api.networking.Messenger;
import net.impactdev.impactor.api.networking.NetworkingService;
import net.kyori.adventure.key.Key;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.UnifiedJedis;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class RedisMessenger implements Messenger {

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
    private static final Executor EXECUTOR = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder()
            .setDaemon(true)
            .setNameFormat("Impactor - Redis Messenger")
            .build());

    private final Logger logger;
    private final Key channel;
    private final NetworkingService service;

    private @MonotonicNonNull UnifiedJedis jedis;
    private @MonotonicNonNull Subscription subscription;

    private boolean closing = false;

    public RedisMessenger(final Logger logger, final Key channel, final NetworkingService service) {
        this.logger = logger;
        this.channel = channel;
        this.service = service;
    }

    public void initialize(RedisConfig config) {
        List<String> addresses = config.addresses();
        if (addresses.isEmpty()) {
            throw new RuntimeException("Cannot create redis messenger, no address specified...");
        } else if (addresses.size() == 1) {
            this.initialize(new JedisPooled(parseAddress(addresses.getFirst()), config(config.username(), config.password(), config.ssl())));
        } else {
            Set<HostAndPort> hosts = addresses.stream().map(RedisMessenger::parseAddress).collect(Collectors.toSet());
            this.initialize(new JedisCluster(hosts, config(config.username(), config.password(), config.ssl())));
        }
    }

    private void initialize(UnifiedJedis jedis) {
        this.jedis = jedis;
        this.subscription = new Subscription(this);
        EXECUTOR.execute(this.subscription);
    }

    private static JedisClientConfig config(String username, String password, boolean ssl) {
        return DefaultJedisClientConfig
                .builder()
                .user(username)
                .password(password)
                .ssl(ssl)
                .timeoutMillis(Protocol.DEFAULT_TIMEOUT)
                .build();
    }

    private static HostAndPort parseAddress(String address) {
        String[] split = address.split(":");
        String host = split[0];
        int port = split.length > 1 ? Integer.parseInt(split[1]) : Protocol.DEFAULT_PORT;
        return new HostAndPort(host, port);
    }

    @Override
    public void publish(@NotNull Message message) {
        this.jedis.publish(
                this.channel.asString(), GSON.toJson(service
                        .registry()
                        .codec(message.key())
                        .encode(message))
        );
    }

    @Override
    public void shutdown() {
        this.closing = true;
        this.subscription.unsubscribe();
        this.jedis.close();
    }

    private static class Subscription extends JedisPubSub implements Runnable {
        private final RedisMessenger messenger;

        private Subscription(final RedisMessenger messenger) {
            this.messenger = messenger;
        }

        @Override
        public void run() {
            boolean first = true;
            while (!this.messenger.closing && !Thread.interrupted() && this.isRedisAlive()) {
                try {
                    if (first) {
                        first = false;
                    } else {
                        this.messenger.logger.info("Redis pubsub connection re-established");
                    }

                    this.messenger.jedis.subscribe(this, this.messenger.channel.asString());
                } catch (final Exception e) {
                    if (this.messenger.closing) {
                        return;
                    }

                    this.messenger.logger.warn("Redis pubsub connection dropped, trying to re-open the connection", e);
                    try {
                        this.unsubscribe();
                    } catch (Exception ignored) {
                    }

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException interruption) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        @Override
        public void onMessage(String channel, String message) {
            if (!channel.equals(this.messenger.channel.asString())) {
                return;
            }

            JsonObject json = GSON.fromJson(message, JsonObject.class);
            this.messenger.service.consumer().consume(json);
        }

        private boolean isRedisAlive() {
            UnifiedJedis jedis = this.messenger.jedis;

            if (jedis instanceof JedisPooled pooled) {
                return !pooled.getPool().isClosed();
            } else if (jedis instanceof JedisCluster cluster) {
                return !cluster.getClusterNodes().isEmpty();
            } else {
                throw new RuntimeException("Unknown jedis type: " + jedis.getClass().getName());
            }
        }
    }
}
