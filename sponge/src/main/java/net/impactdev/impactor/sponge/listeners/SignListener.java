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
//import com.ichorpowered.protocolcontrol.event.annotation.Subscribe;
//import com.ichorpowered.protocolcontrol.packet.PacketDirection;
//import com.ichorpowered.protocolcontrol.packet.PacketType;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.ui.signs.SignQuery;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;

import java.util.Map;
import java.util.UUID;

public class SignListener {

    public final static Map<UUID, SignQuery> requests = Maps.newHashMap();

//    @Subscribe(type = PacketType.UPDATE_SIGN, direction = PacketDirection.INCOMING)
//    public void onSignUpdate(PacketEvent event) {
//        if(requests.containsKey(event.profile().id())) {
//            SignQuery query = requests.remove(event.profile().id());
//            // TODO - Not forge friendly
//            ServerboundSignUpdatePacket packet = (ServerboundSignUpdatePacket) event.packet();
//            Impactor.getInstance().getScheduler().executeSync(() -> {
//                if(!query.submissionHandler().process(Lists.newArrayList(packet.getLines()))) {
//                    if(query.shouldReopenOnFailure()) {
//                        requests.put(event.profile().id(), query);
//                        query.sendTo(PlatformPlayer.from(event.profile().player().orElseThrow(IllegalStateException::new)));
//                    }
//                }
//            });
//        }
//    }

}
