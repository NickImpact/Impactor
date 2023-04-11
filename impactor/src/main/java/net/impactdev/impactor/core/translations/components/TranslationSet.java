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

package net.impactdev.impactor.core.translations.components;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.impactdev.impactor.core.translations.components.resolvers.MultiLineTranslation;
import net.impactdev.impactor.core.translations.components.resolvers.SingleLineTranslation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public final class TranslationSet {

    private final Map<String, Translation<?>> translations = Maps.newLinkedHashMap();

    @SuppressWarnings("unchecked")
    public <T> Translation<T> translation(String key) {
        return (Translation<T>) this.translations.get(key);
    }

    public void register(String key, Translation<?> translation) {
        this.translations.put(key, translation);
    }

    public static TranslationSet fromJson(JsonObject json) throws Exception {
        TranslationSet result = new TranslationSet();
        parse(result, null, json);
        return result;
    }

    private static void parse(@NotNull TranslationSet set, @Nullable String path, @NotNull JsonObject parent) {
        parent.entrySet().forEach(entry -> {
            String key = entry.getKey();
            JsonElement element = entry.getValue();

            String target;
            if(path == null) {
                target = key;
            } else {
                target = path + "." + key;
            }

            if(element.isJsonObject()) {
                parse(set, target, element.getAsJsonObject());
            } else if(element.isJsonArray()) {
                JsonArray array = element.getAsJsonArray();
                List<String> template = Lists.newArrayList();
                for(JsonElement value : array) {
                    if(!value.isJsonPrimitive()) {
                        throw new IllegalStateException("Invalid JSON target within array");
                    }

                    template.add(value.getAsString());
                }

                set.register(target, new MultiLineTranslation(template));
            } else if(element.isJsonPrimitive()) {
                set.register(target, new SingleLineTranslation(element.getAsString()));
            } else {
                throw new IllegalStateException("Reached a null field within JSON");
            }
        });
    }
}
