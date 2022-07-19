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

package net.impactdev.impactor.sponge.listeners;

import com.google.common.collect.Maps;
import net.impactdev.impactor.api.chat.ChatProcessor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.message.PlayerChatEvent;
import org.spongepowered.api.util.Identifiable;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class SpongeChatProcessor implements ChatProcessor {

    private final Map<UUID, Consumer<String>> callbacks = Maps.newHashMap();

    @Override
    public void register(UUID uuid, Consumer<String> callback) {
        this.callbacks.put(uuid, callback);
    }

    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onPlayerChat(PlayerChatEvent event) {
        event.cause().first(ServerPlayer.class).map(Identifiable::uniqueId).ifPresent(id -> {
            if(this.callbacks.containsKey(id)) {
                event.setCancelled(true);
                this.callbacks.remove(id).accept(PlainTextComponentSerializer.plainText().serialize(event.originalMessage()));
            }
        });
    }

}
