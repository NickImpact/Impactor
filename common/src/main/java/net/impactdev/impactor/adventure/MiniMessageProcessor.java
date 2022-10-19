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

import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.adventure.TextProcessor;
import net.impactdev.impactor.api.placeholders.ComponentModifiers;
import net.impactdev.impactor.api.placeholders.PlaceholderParser;
import net.impactdev.impactor.api.placeholders.PlaceholderService;
import net.impactdev.impactor.api.utilities.context.Context;
import net.impactdev.impactor.api.utilities.mappings.PairStream;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class MiniMessageProcessor implements TextProcessor {

    private final Supplier<PlaceholderService> service = Suppliers.memoize(() -> Impactor.instance().services().provide(PlaceholderService.class));
    private final MiniMessage mini = MiniMessage.miniMessage();
    private final Pattern TAG = Pattern.compile("<(?<tag>\\w+(-[-_\\w:]+)?)>");

    @Override
    public @NotNull Component parse(String raw, Context context) {
        Map<String, PlaceholderParser> parsers = this.service.get().parsers().entrySet().stream()
                .map(entry -> Maps.immutableEntry(entry.getKey().asString().replace(":", "-"), entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        List<TagResolver> placeholders = this.locate(raw, context, parsers);
        return mini.deserialize(raw, placeholders.toArray(new TagResolver[0]));
    }

    private List<TagResolver> locate(String raw, Context context, Map<String, PlaceholderParser> parsers) {
        return PairStream.from(parsers)
                .filter((key, parser) -> this.TAG.matcher(raw).results()
                        .map(mr -> {
                            String match = mr.group(1);
                            return match.substring(0, match.contains(":") ? match.indexOf(":") : match.length());
                        })
                        .anyMatch(s -> s.equals(key))
                )
                .map((key, parser) -> TagResolver.resolver(key, (args, ctx) -> {
                    Component result = parser.parse(context);
                    while(args.hasNext()) {
                        result = ComponentModifiers.transform(args.pop().value().charAt(0), result);
                    }

                    return Tag.selfClosingInserting(result);
                }))
                .collect(Collectors.toList());
    }

}
