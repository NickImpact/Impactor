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

package net.impactdev.impactor.api.configuration.loader;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.impactdev.impactor.api.configuration.ConfigKey;
import net.impactdev.impactor.api.configuration.keys.BaseConfigKey;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class KeyLoader {

    private final List<ConfigKey<?>> keys = Lists.newArrayList();

    public KeyLoader(List<Class<?>> providers) {
        this.init(providers);
    }

    public int size() {
        return this.keys.size();
    }

    public ImmutableList<ConfigKey<?>> keys() {
        return ImmutableList.copyOf(this.keys);
    }

    private void init(List<Class<?>> providers) {
        AtomicInteger index = new AtomicInteger();
        providers.forEach(provider -> Arrays.stream(provider.getDeclaredFields())
                .filter(field -> Modifier.isStatic(field.getModifiers()))
                .filter(field -> ConfigKey.class.isAssignableFrom(field.getType()))
                .forEach(field -> {
                    try {
                        // get the key instance
                        BaseConfigKey<?> key = (BaseConfigKey<?>) field.get(null);
                        // set the ordinal value of the key.
                        ((BaseConfigKey.ParentContextBase) key.context()).ordinal(index.getAndIncrement());
                        // add the key to the return map
                        keys.add(key);
                    } catch (Exception e) {
                        throw new RuntimeException("Exception processing field: " + field.getName(), e);
                    }
                }));
    }

}
