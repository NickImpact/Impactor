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

package net.impactdev.impactor.api.config.adapter.system;

import com.google.common.base.Splitter;
import net.impactdev.impactor.api.config.adapter.ConfigurationAdapter;
import net.impactdev.impactor.api.core.annotations.DesignBasedOn;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

@DesignBasedOn(
        github = "https://github.com/LuckPerms/LuckPerms/tree/master",
        license = "https://github.com/LuckPerms/LuckPerms/blob/master/LICENSE.txt"
)
public abstract class StringBasedConfigAdapter implements ConfigurationAdapter {

    private static final Splitter LIST_SPLITTER = Splitter.on(',');
    private static final Splitter.MapSplitter MAP_SPLITTER = Splitter.on(',').withKeyValueSeparator('=');

    protected abstract @Nullable String resolve(String path);

    @Override
    public String getString(String path, String def) {
        String value = this.resolve(path);
        if (value == null) {
            return def;
        }

        return value;
    }

    @Override
    public byte getByte(String path, byte def) {
        String value = this.resolve(path);
        if (value == null) {
            return def;
        }

        try {
            return Byte.parseByte(value);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    @Override
    public short getShort(String path, short def) {
        String value = this.resolve(path);
        if (value == null) {
            return def;
        }

        try {
            return Short.parseShort(value);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    @Override
    public int getInteger(String path, int def) {
        String value = this.resolve(path);
        if (value == null) {
            return def;
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    @Override
    public float getFloat(String path, float def) {
        String value = this.resolve(path);
        if (value == null) {
            return def;
        }

        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    @Override
    public long getLong(String path, long def) {
        String value = this.resolve(path);
        if (value == null) {
            return def;
        }

        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    @Override
    public double getDouble(String path, double def) {
        String value = this.resolve(path);
        if (value == null) {
            return def;
        }

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    @Override
    public boolean getBoolean(String path, boolean def) {
        String value = this.resolve(path);
        if (value == null) {
            return def;
        }

        try {
            return Boolean.parseBoolean(value);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    @Override
    public List<String> getKeys(String path, List<String> def) {
        return def;
    }

    @Override
    public List<String> getStringList(String path, List<String> def) {
        String value = resolve(path);
        if (value == null) {
            return def;
        }

        return LIST_SPLITTER.splitToList(value);
    }

    @Override
    public Map<String, String> getStringMap(String path, Map<String, String> def) {
        String value = resolve(path);
        if (value == null) {
            return def;
        }

        return MAP_SPLITTER.split(value);
    }
}
