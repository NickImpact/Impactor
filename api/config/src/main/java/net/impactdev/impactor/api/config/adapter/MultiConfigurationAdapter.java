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

import com.google.common.collect.ImmutableList;
import net.impactdev.impactor.api.core.annotations.DesignBasedOn;

import java.util.List;
import java.util.Map;

/**
 * Represents a {@link ConfigurationAdapter} built amongst multiple child adapters. The goal
 * of this adapter is to allow any of the children to provide the value.
 *
 * @since 6.0.0
 */
@DesignBasedOn(
        github = "https://github.com/LuckPerms/LuckPerms/tree/master",
        license = "https://github.com/LuckPerms/LuckPerms/blob/master/LICENSE.txt"
)
public final class MultiConfigurationAdapter implements ConfigurationAdapter {

    private final List<ConfigurationAdapter> adapters;

    /**
     * Creates a {@link MultiConfigurationAdapter}.
     *
     * <p>The first adapter in the list has priority (the final say) in deciding what the value is.
     * All adapters are tried in reverse order, and the value returned from the previous adapter
     * is passed into the next as the {@code def} value.</p>
     *
     * @param adapters The list of child adapters
     * @since 6.0.0
     */
    public MultiConfigurationAdapter(final List<ConfigurationAdapter> adapters) {
        this.adapters = ImmutableList.copyOf(adapters).reverse();
    }

    /**
     * Creates a {@link MultiConfigurationAdapter}.
     *
     * <p>The first adapter in the list has priority (the final say) in deciding what the value is.
     * All adapters are tried in reverse order, and the value returned from the previous adapter
     * is passed into the next as the {@code def} value.</p>
     *
     * @param adapters The list of child adapters
     * @since 6.0.0
     */
    public MultiConfigurationAdapter(final ConfigurationAdapter... adapters) {
        this(ImmutableList.copyOf(adapters));
    }

    @Override
    public void reload() {
        this.adapters.forEach(ConfigurationAdapter::reload);
    }

    @Override
    public String getString(String path, String def) {
        String result = def;
        for (final ConfigurationAdapter adapter : this.adapters) {
            result = adapter.getString(path, result);
        }

        return result;
    }

    @Override
    public byte getByte(String path, byte def) {
        byte result = def;
        for (final ConfigurationAdapter adapter : this.adapters) {
            result = adapter.getByte(path, result);
        }

        return result;
    }

    @Override
    public short getShort(String path, short def) {
        short result = def;
        for (final ConfigurationAdapter adapter : this.adapters) {
            result = adapter.getShort(path, result);
        }

        return result;
    }

    @Override
    public int getInteger(String path, int def) {
        int result = def;
        for (final ConfigurationAdapter adapter : this.adapters) {
            result = adapter.getInteger(path, result);
        }

        return result;
    }

    @Override
    public float getFloat(String path, float def) {
        float result = def;
        for (final ConfigurationAdapter adapter : this.adapters) {
            result = adapter.getFloat(path, result);
        }

        return result;
    }

    @Override
    public long getLong(String path, long def) {
        long result = def;
        for (final ConfigurationAdapter adapter : this.adapters) {
            result = adapter.getLong(path, result);
        }

        return result;
    }

    @Override
    public double getDouble(String path, double def) {
        double result = def;
        for (final ConfigurationAdapter adapter : this.adapters) {
            result = adapter.getDouble(path, result);
        }

        return result;
    }

    @Override
    public boolean getBoolean(String path, boolean def) {
        boolean result = def;
        for (final ConfigurationAdapter adapter : this.adapters) {
            result = adapter.getBoolean(path, result);
        }

        return result;
    }

    @Override
    public List<String> getKeys(String path, List<String> def) {
        List<String> result = def;
        for (final ConfigurationAdapter adapter : this.adapters) {
            result = adapter.getKeys(path, result);
        }

        return result;
    }

    @Override
    public List<String> getStringList(String path, List<String> def) {
        List<String> result = def;
        for (final ConfigurationAdapter adapter : this.adapters) {
            result = adapter.getStringList(path, result);
        }

        return result;
    }

    @Override
    public Map<String, String> getStringMap(String path, Map<String, String> def) {
        Map<String, String> result = def;
        for (final ConfigurationAdapter adapter : this.adapters) {
            result = adapter.getStringMap(path, result);
        }

        return result;
    }
}
