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

package net.impactdev.impactor.api.platform.adventure;

import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;

import static net.impactdev.impactor.api.core.ServiceProvider.instance;

public interface Audiences {

    static Audiences audiences() {
        return instance().provide(Audiences.class);
    }

    /**
     * Gets an {@link Audience} which targets the entire server. This includes players as well as the
     * general system, when available.
     *
     * @return An audience representing the overall server
     */
    @NotNull
    Audience server();

    /**
     * Gets an {@link Audience} that targets the system, typically also known as the server console.
     *
     * @return An audience representing the system
     */
    @NotNull
    Audience system();

    /**
     * Gets an {@link Audience} with a general collection of all online players currently on the server.
     *
     * @return An audience representing all online players
     */
    @NotNull
    Audience onlinePlayers();

    /**
     * Gets an {@link Audience} built of online players who all have the given permission.
     *
     * @param permission The permission to query
     * @return An audience representing online players with the given permission
     */
    @NotNull
    Audience withPermission(final @NotNull String permission);

}
