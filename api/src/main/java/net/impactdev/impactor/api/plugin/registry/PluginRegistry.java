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

package net.impactdev.impactor.api.plugin.registry;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import net.impactdev.impactor.api.plugin.ImpactorPlugin;

import java.util.Map;
import java.util.Optional;

/**
 *
 */
public final class PluginRegistry {

    /** A mapping of a plugins ID to its actual implementation */
    private static final Map<String, ImpactorPlugin> plugins = Maps.newHashMap();

    public static void register(ImpactorPlugin plugin) {
        plugins.put(plugin.metadata().id(), plugin);
    }

    public static Optional<ImpactorPlugin> get(String id) {
        return Optional.ofNullable(plugins.get(id));
    }

    public static ImmutableList<ImpactorPlugin> getAll() {
        return ImmutableList.copyOf(plugins.values());
    }

    public static boolean isRegistered(String id) {
        return plugins.containsKey(id);
    }

}
