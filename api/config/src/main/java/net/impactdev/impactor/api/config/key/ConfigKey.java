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

package net.impactdev.impactor.api.config.key;

import net.impactdev.impactor.api.config.adapter.ConfigurationAdapter;
import net.impactdev.impactor.api.core.annotations.DesignBasedOn;

/**
 * Represents a key in the configuration.
 *
 * @param <T> the value type
 */
@DesignBasedOn(
        github = "https://github.com/LuckPerms/LuckPerms/tree/master",
        license = "https://github.com/LuckPerms/LuckPerms/blob/master/LICENSE.txt"
)
public interface ConfigKey<T> {

    /**
     * Gets the position of this key within the keys enum.
     *
     * @return the position
     */
    int ordinal();

    /**
     * Gets if the config key can be reloaded.
     *
     * @return the if the key can be reloaded
     */
    boolean reloadable();

    /**
     * Resolves and returns the value mapped to this key using the given config instance.
     *
     * @param adapter the config adapter instance
     * @return the value mapped to this key
     */
    T get(ConfigurationAdapter adapter);

}
