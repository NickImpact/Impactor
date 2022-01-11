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
import net.impactdev.impactor.api.gui.signs.SignQuery;
import net.impactdev.impactor.api.gui.signs.SignSubmission;
import net.impactdev.impactor.sponge.listeners.SignListener;
import net.kyori.adventure.text.TextComponent;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SOpenSignMenuPacket;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.math.vector.Vector3d;
import org.spongepowered.math.vector.Vector3i;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class SpongeSignQuery implements SignQuery<TextComponent, ServerPlayer, Vector3i> {

    private final List<TextComponent> lines;
    private final Vector3i position;
    private final boolean reopen;
    private final SignSubmission callback;

    private SpongeSignQuery(SpongeSignQueryBuilder builder) {
        this.lines = builder.lines;
        this.position = builder.position;
        this.reopen = builder.reopen;
        this.callback = builder.callback;
    }

    @Override
    public List<TextComponent> getText() {
        return this.lines;
    }

    @Override
    public Vector3i getSignPosition() {
        return this.position;
    }

    @Override
    public boolean shouldReopenOnFailure() {
        return this.reopen;
    }

    @Override
    public SignSubmission getSubmissionHandler() {
        return this.callback;
    }

    @Override
    public void sendTo(ServerPlayer player) {
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

    public static class SpongeSignQueryBuilder implements SignQueryBuilder<TextComponent, ServerPlayer, Vector3i> {

        private List<TextComponent> lines;
        private Vector3i position;
        private boolean reopen;
        private SignSubmission callback;

        @Override
        public SignQueryBuilder<TextComponent, ServerPlayer, Vector3i> text(List<TextComponent> text) {
            this.lines = text;
            return this;
        }

        @Override
        public SignQueryBuilder<TextComponent, ServerPlayer, Vector3i> position(Vector3i position) {
            this.position = position;
            return this;
        }

        @Override
        public SignQueryBuilder<TextComponent, ServerPlayer, Vector3i> reopenOnFailure(boolean state) {
            this.reopen = state;
            return this;
        }

        @Override
        public SignQueryBuilder<TextComponent, ServerPlayer, Vector3i> response(SignSubmission response) {
            this.callback = response;
            return this;
        }

        @Override
        public SignQueryBuilder<TextComponent, ServerPlayer, Vector3i> from(SignQuery<TextComponent, ServerPlayer, Vector3i> query) {
            this.lines = query.getText();
            this.position = query.getSignPosition();
            this.reopen = query.shouldReopenOnFailure();
            this.callback = query.getSubmissionHandler();
            return this;
        }

        @Override
        public SignQuery<TextComponent, ServerPlayer, Vector3i> build() {
            return new SpongeSignQuery(this);
        }
    }
}
