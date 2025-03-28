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

package net.impactdev.impactor.api.config.adapter;

import net.impactdev.impactor.api.core.annotations.DesignBasedOn;

import java.util.List;
import java.util.Map;

/**
 * Represents a method of parsing configuration values from some target path to a specified value.
 *
 * @since 5.0.0
 */
@DesignBasedOn(
        github = "https://github.com/LuckPerms/LuckPerms/tree/master",
        license = "https://github.com/LuckPerms/LuckPerms/blob/master/LICENSE.txt"
)
public interface ConfigurationAdapter {

    /**
     * Reloads the configuration.
     *
     * @since 5.0.0
     */
    void reload();

    /**
     * Reads a string from the given path, defaulting to the provided default parameter should that
     * value be unavailable.
     *
     * @param path The path for the target configuration
     * @param def  The value to return should that value not be available
     * @return A string representing the target path
     * @since 5.0.0
     */
    String getString(String path, String def);

    /**
     * Reads a byte from the given path, defaulting to the provided default parameter should that
     * value be unavailable.
     *
     * @param path The path for the target configuration
     * @param def  The value to return should that value not be available
     * @return A byte representing the target path
     * @since 5.0.0
     */
    byte getByte(String path, byte def);

    /**
     * Reads a short from the given path, defaulting to the provided default parameter should that
     * value be unavailable.
     *
     * @param path The path for the target configuration
     * @param def  The value to return should that value not be available
     * @return A short representing the target path
     * @since 5.0.0
     */
    short getShort(String path, short def);

    /**
     * Reads a int from the given path, defaulting to the provided default parameter should that
     * value be unavailable.
     *
     * @param path The path for the target configuration
     * @param def  The value to return should that value not be available
     * @return An int representing the target path
     * @since 5.0.0
     */
    int getInteger(String path, int def);

    /**
     * Reads a float from the given path, defaulting to the provided default parameter should that
     * value be unavailable.
     *
     * @param path The path for the target configuration
     * @param def  The value to return should that value not be available
     * @return A float representing the target path
     * @since 5.0.0
     */
    float getFloat(String path, float def);

    /**
     * Reads a long from the given path, defaulting to the provided default parameter should that
     * value be unavailable.
     *
     * @param path The path for the target configuration
     * @param def  The value to return should that value not be available
     * @return A long representing the target path
     * @since 5.0.0
     */
    long getLong(String path, long def);

    /**
     * Reads a double from the given path, defaulting to the provided default parameter should that
     * value be unavailable.
     *
     * @param path The path for the target configuration
     * @param def  The value to return should that value not be available
     * @return A double representing the target path
     * @since 5.0.0
     */
    double getDouble(String path, double def);

    /**
     * Reads a boolean from the given path, defaulting to the provided default parameter should that
     * value be unavailable.
     *
     * @param path The path for the target configuration
     * @param def  The value to return should that value not be available
     * @return A boolean representing the target path
     * @since 5.0.0
     */
    boolean getBoolean(String path, boolean def);

    /**
     * Returns a set of sub-keys defined under the particular path, where possible. By nature, this might
     * only be supported with specific adapter implementations.
     *
     * @param path The path to the parent element
     * @return A list of keys at the given path
     * @since 5.0.0
     */
    List<String> getKeys(String path, List<String> def);

    /**
     * Gets an array of strings at the target path.
     *
     * @param path The path of the list
     * @param def  A fallback list should the path not be defined
     * @return A list of strings
     * @since 5.0.0
     */
    List<String> getStringList(String path, List<String> def);

    /**
     * Calculates a map of strings to strings at the given path.
     *
     * @param path The path of the mapping
     * @param def  A fallback map should the path not be defined
     * @return A mapping of strings to strings
     * @since 5.0.0
     */
    Map<String, String> getStringMap(String path, Map<String, String> def);

}
