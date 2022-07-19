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

package net.impactdev.impactor.sponge.ui.signs;

import com.google.common.reflect.TypeToken;
//import com.ichorpowered.protocolcontrol.channel.ChannelProfile;
//import com.ichorpowered.protocolcontrol.packet.PacketDirection;
//import com.ichorpowered.protocolcontrol.packet.PacketRemapper;
//import com.ichorpowered.protocolcontrol.packet.PacketType;
//import com.ichorpowered.protocolcontrol.service.ProtocolService;
//import com.ichorpowered.protocolcontrol.service.ServiceProvider;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.platform.players.PlatformPlayerManager;
import net.impactdev.impactor.api.ui.signs.SignQuery;
import net.impactdev.impactor.common.ui.signs.ImpactorSignQuery;
import net.impactdev.impactor.sponge.listeners.SignListener;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import java.util.Objects;
import java.util.stream.IntStream;

public class SpongeSignQuery extends ImpactorSignQuery {

    public SpongeSignQuery(SpongeSignQueryBuilder builder) {
        super(builder.lines, builder.position, builder.reopen, builder.callback);
    }

    @Override
    public void sendTo(PlatformPlayer player) {
        BlockState sign = BlockTypes.SPRUCE_SIGN.get().defaultState();
        PlatformPlayerManager<ServerPlayer> manager = (PlatformPlayerManager<ServerPlayer>) Impactor.getInstance().getPlatform().playerManager();
        ServerPlayer sponge = manager.translate(player).orElseThrow(() -> new IllegalStateException("Player not available or found"));
        sponge.sendBlockChange(this.position().toInt(), sign);

//        ProtocolService service = ServiceProvider.get();
//        final ChannelProfile profile = Objects.requireNonNull(service.channels().profile(player.uuid()));
//
//        try {
//            final PacketRemapper.Wrapped view = service.remapper().wrap(PacketType.OPEN_SIGN_MENU, PacketDirection.OUTGOING);
//            final PacketRemapper.Wrapped update = service.remapper().wrap(PacketType.UPDATE_TILE_ENTITY, PacketDirection.OUTGOING);
//
//            BlockPos position = new BlockPos(this.position().x(), this.position().y(), this.position().z());
//            view.set(TypeToken.of(BlockPos.class), 0, position);
//
//            CompoundTag data = new CompoundTag();
//            data.putInt("x", position.getX());
//            data.putInt("y", position.getY());
//            data.putInt("z", position.getZ());
//            data.putString("id", "minecraft:sign");
//
//            IntStream.rangeClosed(1, 4).forEach(line -> data.putString(
//                    "Text" + line,
//                    this.text().size() >= line ? String.format(TEXT_FORMAT, PlainTextComponentSerializer.plainText().serialize(this.text().get(line - 1))) : " "
//            ));
//
//            update.set(TypeToken.of(BlockPos.class), 0, position);
//            update.set(TypeToken.of(int.class), 0, 9);
//            update.set(TypeToken.of(CompoundTag.class), 0, data);
//
//            profile.send(PacketDirection.OUTGOING, update.packet());
//            profile.send(PacketDirection.OUTGOING, view.packet());
//
//            SignListener.requests.put(player.uuid(), this);
//        } catch (Throwable throwable) {
//            throwable.printStackTrace();
//        }
    }

    public static class SpongeSignQueryBuilder extends ImpactorSignQueryBuilder {

        @Override
        public SignQuery build() {
            return new SpongeSignQuery(this);
        }

    }
}
