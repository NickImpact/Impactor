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

//import com.ichorpowered.protocolcontrol.channel.ChannelProfile;
//import com.ichorpowered.protocolcontrol.packet.PacketDirection;
//import com.ichorpowered.protocolcontrol.packet.PacketRemapper;
//import com.ichorpowered.protocolcontrol.service.ProtocolService;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.ui.signs.SignQuery;
import net.impactdev.impactor.api.ui.signs.SignSubmission;
import net.impactdev.impactor.common.ui.signs.ImpactorSignQuery;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.math.vector.Vector3i;

import java.util.List;

public class SpongeSignQuery extends ImpactorSignQuery {

    public SpongeSignQuery(SpongeSignQueryBuilder builder) {
        super(builder.lines, builder.position, builder.reopen, builder.callback);
    }

    @Override
    public void sendTo(PlatformPlayer player) {
        throw new UnsupportedOperationException("Awaiting a 8.0.0 compatible build");
//        BlockState sign = BlockTypes.STANDING_SIGN.getDefaultState();
//        player.sendBlockChange(this.position.toInt(), sign);
//
//        ProtocolService service = Sponge.getServiceManager().provideUnchecked(ProtocolService.class);
//        final ChannelProfile profile = Objects.requireNonNull(service.channels().profile(player.getUniqueId()));
//
//        try {
//            final PacketRemapper.Wrapped<SOpenSignMenuPacket> view = service.remapper().wrap(new SOpenSignMenuPacket());
//            final PacketRemapper.Wrapped<SUpdateTileEntityPacket> update = service.remapper().wrap(new SUpdateTileEntityPacket());
//
//            BlockPos position = new BlockPos(this.position.getX(), this.position.getY(), this.position.getZ());
//            view.set(BlockPos.class, 0, position);
//
//            NBTTagCompound data = new NBTTagCompound();
//            data.setInteger("x", position.getX());
//            data.setInteger("y", position.getY());
//            data.setInteger("z", position.getZ());
//            data.setString("id", "minecraft:sign");
//
//            IntStream.rangeClosed(1, 4).forEach(line -> data.setString(
//                    "Text" + line,
//                    this.getText().size() >= line ? String.format(TEXT_FORMAT, TextSerializers.LEGACY_FORMATTING_CODE.serialize(this.getText().get(line - 1))) : " "
//            ));
//
//            update.set(BlockPos.class, 0, position);
//            update.set(int.class, 0, 9);
//            update.set(NBTTagCompound.class, 0, data);
//
//            profile.send(PacketDirection.OUTGOING, update.packet());
//            profile.send(PacketDirection.OUTGOING, view.packet());
//
//            SignListener.requests.put(player.getUniqueId(), this);
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
