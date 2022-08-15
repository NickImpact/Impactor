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

package net.impactdev.impactor.api.items.properties.enchantments;

import net.impactdev.impactor.api.Impactor;
import net.kyori.adventure.key.Key;

/**
 * Details an enchantment with the exact pathing to the type of enchantment, as well as the level
 * of which an enchantment should be applied.
 */
public interface Enchantment {

    static Enchantment create(Key key, int level) {
        return Impactor.instance().factories().provide(Factory.class).create(key, level);
    }

    /**
     * Represents the registry key for an enchantment. This acts as the look-up to Minecraft
     * to fetch an implementation of the actual enchantment.
     *
     * @return A key representing the registry path of an enchantment
     */
    Key type();

    /**
     * Specifies the level which this enchantment should be. A level directly affects the abilities
     * of an enchantment and how powerful they would be.
     *
     * @return The level of an enchantment
     */
    int level();

    interface Factory {

        Enchantment create(Key key, int level);

    }

}
