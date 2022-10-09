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

package net.impactdev.impactor.game.items.stacks.providers;

import net.impactdev.impactor.api.items.AbstractedItemStack;
import net.impactdev.impactor.api.items.builders.provided.SkullStackBuilder;
import net.impactdev.impactor.api.items.extensions.SkullStack;
import net.impactdev.impactor.api.items.types.ItemTypes;
import net.impactdev.impactor.game.items.stacks.builders.ImpactorSkullStackBuilder;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.ListBinaryTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.UUID;

public final class ImpactorSkullStack extends AbstractedItemStack implements SkullStack {

    private final SkullType type;
    private final PlayerHeadMetadata metadata;

    @SuppressWarnings("ConstantConditions")
    public ImpactorSkullStack(ImpactorSkullStackBuilder builder) {
        super(builder.isPlayer ? ItemTypes.PLAYER_HEAD : builder.type.delegate(), builder);
        this.type = builder.type;

        if(builder.isPlayer) {
            this.metadata = new ImpactorPlayerHeadMetadata(builder.target, builder.texture);
        } else {
            this.metadata = null;
        }
    }

    @Override
    public boolean supportsTextures() {
        return !this.skullType().isPresent();
    }

    @Override
    public Optional<SkullType> skullType() {
        return Optional.ofNullable(this.type);
    }

    @Override
    public Optional<PlayerHeadMetadata> playerMetadata() {
        return Optional.ofNullable(this.metadata);
    }

    @Override
    public CompoundBinaryTag nbt() {
        CompoundBinaryTag nbt =  super.nbt();
        if(this.playerMetadata().isPresent()) {
            if (this.metadata.username().isPresent()) {
                if (!this.metadata.texture().isPresent()) {
                    nbt = nbt.putString("SkullOwner", this.metadata.username().get());
                }
                else {
                    CompoundBinaryTag owner = CompoundBinaryTag.empty();
                    owner.putString("Name", this.metadata.username().orElse("Impactor"));
                    this.properties(owner);

                    nbt = nbt.put("SkullOwner", owner);
                }
            } else {
                if (this.metadata.texture().isPresent()) {
                    CompoundBinaryTag owner = CompoundBinaryTag.empty();
                    owner.putIntArray("Id", NbtUtils.createUUID(UUID.randomUUID()).getAsIntArray());
                    this.properties(owner);

                    nbt = nbt.put("SkullOwner", owner);
                }
            }
        }

        return nbt;
    }

    private void properties(CompoundBinaryTag nbt) {
        CompoundBinaryTag properties = CompoundBinaryTag.empty();
        ListBinaryTag textures = ListBinaryTag.empty();

        CompoundBinaryTag value = CompoundBinaryTag.empty();
        value.putString("Value", this.metadata.texture().orElseThrow(() -> new IllegalStateException("No available texture")));
        textures.add(value);
        properties.put("textures", textures);

        nbt.put("Properties", properties);
    }

    public static class ImpactorPlayerHeadMetadata implements PlayerHeadMetadata {

        private final String username;
        private final String texture;

        public ImpactorPlayerHeadMetadata(final String username, final String texture) {
            this.username = username;
            this.texture = texture;
        }

        @Override
        public Optional<String> username() {
            return Optional.ofNullable(this.username);
        }

        @Override
        public Optional<String> texture() {
            return Optional.ofNullable(this.texture);
        }

        private void apply(SkullStackBuilder root) {
            if(this.username != null) {
                root.player(this.username, false);
            } else {
                root.player(this.texture, true);
            }
        }
    }
}
