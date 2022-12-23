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

package net.impactdev.impactor.forge.platform.sources;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.impactdev.impactor.api.platform.sources.PlatformPlayer;
import net.impactdev.impactor.api.platform.sources.PlatformSource;
import net.impactdev.impactor.api.platform.sources.SourceType;
import net.impactdev.impactor.api.platform.sources.metadata.MetadataKeys;
import net.impactdev.impactor.minecraft.platform.metadata.GameMetadataKeys;
import net.kyori.adventure.key.Key;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.spongepowered.math.vector.Vector2d;
import org.spongepowered.math.vector.Vector3d;

import java.util.UUID;

public class ForgePlatformFactory implements PlatformSource.Factory, PlatformPlayer.Factory {

    private final Cache<UUID, PlatformSource> cache = Caffeine.newBuilder().build();

    @Override
    public PlatformSource console() {
        return this.cache.get(
                PlatformSource.CONSOLE_UUID,
                uuid -> new ForgePlatformSource(PlatformSource.CONSOLE_UUID, SourceType.CONSOLE)
        );
    }

    @Override
    public PlatformSource entity(UUID uuid) {
        return this.cache.get(uuid, id -> {
            PlayerList players = ServerLifecycleHooks.getCurrentServer().getPlayerList();
            ServerPlayer player = players.getPlayer(id);
            if(player != null) {
                PlatformPlayer source = new ForgePlatformPlayer(id);

                this.cache.put(id, source);
                return source;
            }

            for(ServerLevel level : ServerLifecycleHooks.getCurrentServer().getAllLevels()) {
                Entity entity = level.getEntity(id);
                if(entity != null) {
                    PlatformSource source = new ForgePlatformSource(id, SourceType.ENTITY);
                    source.offer(MetadataKeys.WORLD, () -> {
                        ResourceKey<Level> key = level.dimension();
                        return Key.key(key.location().getNamespace(), key.location().getPath());
                    });
                    source.offer(GameMetadataKeys.ENTITY, () -> entity);
                    source.offer(MetadataKeys.POSITION, () -> {
                        Vec3 vec3 = entity.position();
                        return new Vector3d(vec3.x, vec3.y, vec3.z);
                    });
                    source.offer(MetadataKeys.ROTATION, () -> {
                        Vec2 vec2 = entity.getRotationVector();
                        return new Vector2d(vec2.x, vec2.y);
                    });

                    this.cache.put(id, source);
                    return source;
                }
            }

            throw new IllegalArgumentException("Could not locate a valid entity with this ID");
        });
    }

    @Override
    public PlatformPlayer player(UUID uuid) {
        return (PlatformPlayer) this.cache.get(uuid, ForgePlatformPlayer::new);
    }

}
