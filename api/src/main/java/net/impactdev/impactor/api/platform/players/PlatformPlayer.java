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

package net.impactdev.impactor.api.platform.players;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.items.ImpactorItemStack;
import net.impactdev.impactor.api.platform.players.transactions.ItemTransaction;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.math.vector.Vector3d;

import java.util.UUID;

/**
 * A platform player represents a mirror to a player instance registered by the game platform. While
 * the game object represents an entity that in some implementations can be altered based on respawns,
 * this instance acts purely as a data accessor to the target player object. As such, this detail
 * is capable of being stored as it'll not actually hold the player instance behind.
 * <p>
 * As a general note, this interface design works primarily for online players only. If the target player
 * is not online at the time of invoking these accessor calls, these calls are expected to be provided
 * dummy results, or fail exceptionally.
 */
public interface PlatformPlayer extends PlatformSource {

    /**
     * Creates a new platform player instance, if one is not already cached.
     *
     * @param uuid The UUID of the target player instance
     * @return A PlatformPlayer instance mirroring the game player instance
     */
    static PlatformPlayer getOrCreate(@NotNull final UUID uuid) {
        return Impactor.instance().factories().provide(Factory.class).create(uuid);
    }

    /**
     * Specifies the display name of a player, if they have one. Otherwise, this call will
     * fall back to their actual username, represented via {@link PlatformSource#name()}.
     *
     * @return The display name of a user, or their actual username
     */
    Component displayName();

//    /**
//     * Indicates the current world the player exists in.
//     *
//     * @return The world the player is currently in
//     */
//    ServerLevel world();

    /**
     * Specifies the current block coordinates of a player. This is where the player
     * would be standing in a particular world.
     *
     * @return The position of the player within a world
     */
    Vector3d position();

    /**
     * Attempts to offer the given stack to the target player. In return, a receipt will specify
     * the result of the transaction, stating its success and, if applicable, the size of the stack
     * successfully given to the target.
     *
     * @param stack The stack being offered to a player
     * @return A receipt indicating the result of the transaction
     */
    ItemTransaction offer(ImpactorItemStack stack);

    interface Factory {

        /**
         * Creates a platform player using the target UUID.
         *
         * @param uuid The UUID of the target player
         * @return A Platform Player representing the player instance
         */
        PlatformPlayer create(@NotNull final UUID uuid);

    }

}
