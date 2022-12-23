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

package net.impactdev.impactor.core.text.adventure;

import com.google.common.base.Splitter;
import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.text.TextProcessor;
import net.impactdev.impactor.api.text.placeholders.ComponentModifiers;
import net.impactdev.impactor.api.text.placeholders.PlaceholderParser;
import net.impactdev.impactor.api.text.placeholders.PlaceholderService;
import net.impactdev.impactor.api.utility.Context;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextFormat;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.kyori.adventure.text.Component.text;

@SuppressWarnings("PatternValidation")
public final class LegacyProcessor implements TextProcessor {

    private static final Pattern TOKENIZER = Pattern.compile("((?=([{]{2}|[}]{2})|(?<=([{]{2}|[}]{2}))))");
    private static final Pattern PLACEHOLDER = Pattern.compile("(?<placeholder>[\\w-:]+)(\\|(?<arguments>.+))?");
    private static final Pattern LAST_FORMATS = Pattern.compile("(&([a-fk-or0-9]|#[a-f0-9]{6})){1,2}$", Pattern.CASE_INSENSITIVE);
    private static final Map<Character, TextFormat> FORMATTERS = Maps.newHashMap();

    private final Supplier<PlaceholderService> service = Suppliers.memoize(() -> Impactor.instance().services().provide(PlaceholderService.class));
    private final LegacyComponentSerializer serializer;

    public LegacyProcessor(char character) {
        this.serializer = LegacyComponentSerializer.builder().character(character).hexColors().build();
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
        Map<Key, PlaceholderParser> parsers = this.service.get().parsers();

        List<String> split = Splitter.on(TOKENIZER).splitToList(input);
        List<Component> result = Lists.newArrayList();

        List<TextFormat> decorations = Lists.newArrayList();
        StringBuilder cache = new StringBuilder();
        for(int i = 0; i < split.size(); i++) {
            if(split.get(i).equals("{{") && split.get(i + 2).equals("}}")) {
                if(cache.length() > 0) {
                    String built = cache.toString();
                    cache = new StringBuilder();

                    result.add(this.serializer.deserialize(built));
                    Matcher matcher = LAST_FORMATS.matcher(built);
                    if(matcher.find()) {
                        Arrays.stream(matcher.group().split("&"))
                                .filter(in -> !in.isEmpty())
                                .map(in -> {
                                    if(in.length() == 1) {
                                        return FORMATTERS.get(in.charAt(0));
                                    } else {
                                        try {
                                            return TextColor.color(Integer.parseInt(in.substring(1), 16));
                                        } catch (NumberFormatException e) {
                                            return null;
                                        }
                                    }
                                })
                                .filter(Objects::nonNull)
                                .forEach(decorations::add);
                    }
                }

                AtomicReference<Component> parsed = new AtomicReference<>();
                Matcher placeholder = PLACEHOLDER.matcher(split.get(i + 1));
                if(!placeholder.find()) {
                    parsed.set(this.serializer.deserialize("{{" + split.get(i + 1) + "}}"));
                } else {
                    String rawKey = placeholder.group("placeholder");
                    Key key;
                    if(rawKey.contains(":")) {
                        key = Key.key(rawKey);
                    } else {
                        key = Key.key("unknown", rawKey);
                    }

                    parsed.set(parsers.getOrDefault(key, ctx -> text("{{").append(text(rawKey)).append(text("}}"))).parse(context));
                    String arguments = placeholder.group("arguments");
                    if(arguments != null) {
                        for (int c = 0; c < arguments.length(); c++) {
                            char arg = arguments.charAt(c);
                            parsed.set(ComponentModifiers.transform(arg, parsed.get()));
                        }
                    }
                }

                decorations.forEach(format -> {
                    parsed.set(parsed.get().style(parent -> {
                        if(format instanceof TextColor) {
                            parent.color((TextColor) format);
                        } else {
                            parent.decoration((TextDecoration) format, TextDecoration.State.TRUE);
                        }
                    }));
                });

                result.add(parsed.get());
                i += 2;
            } else {
                cache.append(split.get(i));
            }
        }

        if(cache.length() > 0) {
            result.add(this.serializer.deserialize(cache.toString()));
        }

        return result;
    }

    static {
        FORMATTERS.put('0', NamedTextColor.BLACK);
        FORMATTERS.put('1', NamedTextColor.DARK_BLUE);
        FORMATTERS.put('2', NamedTextColor.DARK_GREEN);
        FORMATTERS.put('3', NamedTextColor.DARK_AQUA);
        FORMATTERS.put('4', NamedTextColor.DARK_RED);
        FORMATTERS.put('5', NamedTextColor.DARK_PURPLE);
        FORMATTERS.put('6', NamedTextColor.GOLD);
        FORMATTERS.put('7', NamedTextColor.GRAY);
        FORMATTERS.put('8', NamedTextColor.DARK_GRAY);
        FORMATTERS.put('9', NamedTextColor.BLUE);
        FORMATTERS.put('a', NamedTextColor.GREEN);
        FORMATTERS.put('b', NamedTextColor.AQUA);
        FORMATTERS.put('c', NamedTextColor.RED);
        FORMATTERS.put('d', NamedTextColor.LIGHT_PURPLE);
        FORMATTERS.put('e', NamedTextColor.YELLOW);
        FORMATTERS.put('f', NamedTextColor.WHITE);

        FORMATTERS.put('k', TextDecoration.OBFUSCATED);
        FORMATTERS.put('l', TextDecoration.BOLD);
        FORMATTERS.put('m', TextDecoration.STRIKETHROUGH);
        FORMATTERS.put('n', TextDecoration.UNDERLINED);
        FORMATTERS.put('o', TextDecoration.ITALIC);
    }
}
