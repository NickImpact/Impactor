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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
//import com.ichorpowered.protocolcontrol.event.PacketEvent;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.gui.signs.SignQuery;
//import net.kyori.event.method.annotation.Subscribe;
import net.kyori.adventure.text.TextComponent;
import net.minecraft.network.play.client.CUpdateSignPacket;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.math.vector.Vector3i;

import java.util.Map;
import java.util.UUID;

public class SignListener {

    public final static Map<UUID, SignQuery<TextComponent, ServerPlayer, Vector3i>> requests = Maps.newHashMap();

//    @Subscribe
//    public void onSignUpdate(PacketEvent<CUpdateSignPacket> event) {
//        if(requests.containsKey(event.profile().player())) {
//            SignQuery<Text, Player> query = requests.remove(event.profile().player());
//            Impactor.getInstance().getScheduler().executeSync(() -> {
//                if(!query.getSubmissionHandler().process(Lists.newArrayList(event.packet().getLines()))) {
//                    if(query.shouldReopenOnFailure()) {
//                        requests.put(event.profile().player(), query);
//                        query.sendTo(Sponge.getServer().getPlayer(event.profile().player()).get());
//                    }
//                }
//            });
//        }
//    }

}
