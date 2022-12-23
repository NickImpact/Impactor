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

package net.impactdev.impactor.minecraft.commands;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.commands.CommandSource;
import net.impactdev.impactor.api.platform.sources.PlatformPlayer;
import net.impactdev.impactor.api.platform.sources.PlatformSource;
import net.impactdev.impactor.api.platform.sources.metadata.MetadataKeys;
import net.impactdev.impactor.core.commands.sources.ImpactorCommandSource;
import net.impactdev.impactor.core.platform.sources.ImpactorPlatformSource;
import net.impactdev.impactor.minecraft.platform.GamePlatform;
import net.impactdev.impactor.minecraft.text.AdventureTranslator;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.math.vector.Vector2d;
import org.spongepowered.math.vector.Vector3d;

public class CommandSourceStackTranslator {

    public static @NotNull CommandSource impactor(final @NotNull CommandSourceStack delegate) {
        @Nullable Entity entity = delegate.getEntity();
        if(entity == null) {
            return new ImpactorCommandSource(PlatformSource.console());
        }

        if(entity instanceof ServerPlayer) {
            return new ImpactorCommandSource(PlatformPlayer.getOrCreate(entity.getUUID()));
        } else {
            throw new UnsupportedOperationException("Pending source translation");
        }
    }

    public static @NotNull CommandSourceStack minecraft(final @NotNull CommandSource delegate) {
        MinecraftServer server = ((GamePlatform) Impactor.instance().platform()).server();
        ImpactorPlatformSource platform = (ImpactorPlatformSource) delegate.source();

        net.minecraft.commands.CommandSource source = net.minecraft.commands.CommandSource.NULL;
        Vec3 position = transform(delegate.source().metadata(MetadataKeys.POSITION).orElse(Vector3d.ZERO), impactor -> {
            if(impactor.equals(Vector3d.ZERO)) {
                return Vec3.ZERO;
            }

            return new Vec3(impactor.x(), impactor.y(), impactor.z());
        });
        Vec2 rotation = transform(delegate.source().metadata(MetadataKeys.ROTATION).orElse(Vector2d.ZERO), impactor -> {
            if(impactor.equals(Vector2d.ZERO)) {
                return Vec2.ZERO;
            }

            return new Vec2((float) impactor.x(), (float) impactor.y());
        });
        ServerLevel world = transform(
                delegate.source().metadata(MetadataKeys.WORLD).orElse(Key.key("minecraft:overworld")),
                impactor -> {
                        ResourceKey<Level> key = ResourceKey.create(Registry.DIMENSION_REGISTRY, ResourceLocation.of(
                                impactor.asString(),
                                ':'
                        ));

                        return server.getLevel(key);
        });

        PlainTextComponentSerializer serializer = PlainTextComponentSerializer.plainText();
        return new CommandSourceStack(
                source,
                position,
                rotation,
                world,
                platform.metadata(MetadataKeys.PERMISSION_LEVEL).orElse(0),
                serializer.serialize(delegate.name()),
                AdventureTranslator.toNative(delegate.name()),
                server,
                world.getEntity(delegate.uuid())
        );
    }

    private static <T, R> R transform(T input, Transformer<T, R> transformer) {
        return transformer.transform(input);
    }

    @FunctionalInterface
    public interface Transformer<T, R> {

        R transform(T input);

    }

}
