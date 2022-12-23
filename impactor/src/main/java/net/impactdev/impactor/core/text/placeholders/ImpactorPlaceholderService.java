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

package net.impactdev.impactor.core.text.placeholders;

import com.google.common.collect.Maps;
import net.impactdev.impactor.api.text.placeholders.PlaceholderParser;
import net.impactdev.impactor.api.text.placeholders.PlaceholderService;
import net.impactdev.impactor.core.text.placeholders.provided.ImpactorPlaceholder;
import net.impactdev.impactor.core.text.placeholders.provided.ImpactorPlaceholders;
import net.kyori.adventure.key.Key;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;

public final class ImpactorPlaceholderService implements PlaceholderService {

    private final Map<Key, PlaceholderParser> placeholders = Maps.newHashMap();

    public ImpactorPlaceholderService() {
        Arrays.stream(ImpactorPlaceholders.class.getDeclaredFields())
                .filter(field -> Modifier.isStatic(field.getModifiers()))
                .filter(field -> ImpactorPlaceholder.class.isAssignableFrom(field.getType()))
                .forEach(field -> {
                    try {
                        ImpactorPlaceholder placeholder = (ImpactorPlaceholder) field.get(null);
                        this.register(placeholder.key(), placeholder.parser());
                    } catch (Exception e) {
                        throw new RuntimeException("Exception processing field: " + field.getName(), e);
                    }
                });
    }

    @Override
    public void register(Key key, PlaceholderParser parser) {
        this.placeholders.put(key, parser);
    }

    @Override
    public Map<Key, PlaceholderParser> parsers() {
        return this.placeholders;
    }

    @Override
    public String name() {
        return "Placeholder Service";
    }
}
