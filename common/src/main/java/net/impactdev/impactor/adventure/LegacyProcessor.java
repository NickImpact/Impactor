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

package net.impactdev.impactor.adventure;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.adventure.TextProcessor;
import net.impactdev.impactor.api.placeholders.PlaceholderParser;
import net.impactdev.impactor.api.placeholders.PlaceholderService;
import net.impactdev.impactor.api.utilities.context.Context;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("PatternValidation")
public final class LegacyProcessor implements TextProcessor {

    private static final Pattern TOKENIZER = Pattern.compile("((?=([{]{2}|[}]{2})|(?<=([{]{2}|[}]{2}))))");
    private static final Pattern PLACEHOLDER = Pattern.compile("(?<placeholder>[\\w-:]+)(\\|(?<arguments>.+))?");

    private final PlaceholderService service = Impactor.instance().services().provide(PlaceholderService.class);
    private final LegacyComponentSerializer serializer;

    public LegacyProcessor(char character) {
        this.serializer = LegacyComponentSerializer.legacy(character);
    }

    @Override
    public @NotNull Component parse(String raw, Context context) {
        Component result = null;
        List<Component> tokens = this.tokenize(raw, context);
        for(Component token : tokens) {
            if(result == null) {
                result = token;
            } else {
                result = result.append(token);
            }
        }


        return Objects.requireNonNull(result);
    }

    private List<Component> tokenize(String input, Context context) {
        Map<Key, PlaceholderParser> parsers = this.service.parsers();

        List<String> split = Splitter.on(TOKENIZER).splitToList(input);
        List<Component> result = Lists.newArrayList();

        StringBuilder cache = new StringBuilder();
        for(int i = 0; i < split.size() - 2; i++) {
            if(split.get(i).equals("{{") && split.get(i + 2).equals("}}")) {
                if(cache.length() > 0) {
                    result.add(this.serializer.deserialize(cache.toString()));
                    cache = new StringBuilder();
                }

                Matcher placeholder = PLACEHOLDER.matcher(split.get(i + 1));
                if(!placeholder.find()) {
                    result.add(this.serializer.deserialize("{{" + split.get(i + 1) + "}}"));
                } else {
                    String rawKey = placeholder.group("placeholder");
                    Key key;
                    if(rawKey.contains(":")) {
                        key = Key.key(rawKey);
                    } else {
                        key = Key.key("unknown", rawKey);
                    }

                    result.add(parsers.getOrDefault(key, ctx -> Component.empty()).parse(context));
                }
            } else {
                cache.append(split.get(i));
            }
        }

        return result;
    }
}
