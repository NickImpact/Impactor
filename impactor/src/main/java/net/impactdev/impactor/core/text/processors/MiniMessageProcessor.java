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

package net.impactdev.impactor.core.text.processors;

import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.platform.sources.PlatformSource;
import net.impactdev.impactor.api.text.TextProcessor;
import net.impactdev.impactor.api.text.placeholders.PlaceholderArguments;
import net.impactdev.impactor.api.text.placeholders.PlaceholderParser;
import net.impactdev.impactor.api.text.placeholders.PlaceholderService;
import net.impactdev.impactor.api.utility.Context;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Supplier;

public final class MiniMessageProcessor implements TextProcessor {

    private final Supplier<PlaceholderService> service = Suppliers.memoize(() -> Impactor.instance().services().provide(PlaceholderService.class));
    private final MiniMessage mini = MiniMessage.miniMessage();

    @Override
    public @NotNull Component parse(@Nullable PlatformSource viewer, String raw, Context context) {
        return this.mini.deserialize(raw, this.createResolvers(viewer, context));
    }

    @SuppressWarnings("PatternValidation")
    private TagResolver[] createResolvers(PlatformSource viewer, Context context) {
        Map<Key, PlaceholderParser> parsers = service.get().parsers();
        Map<String, TagResolver> resolvers = Maps.newHashMap();

        for(Key key : parsers.keySet()) {
            resolvers.computeIfAbsent(key.namespace(), in -> TagResolver.resolver(in, (args, ctx) -> {
                final Tag.Argument path = args.popOr("Invalid placeholder key, no path specified");
                Key target = Key.key(key.namespace(), path.lowerValue());

                PlaceholderArguments arguments = null;
                context.append(PlaceholderArguments.class, arguments);

                PlaceholderParser parser = parsers.get(target);
                if(parser == null) {
                    StringBuilder placeholder = new StringBuilder("<" + key.namespace() + ":" + path.lowerValue());
                    while(args.hasNext()) {
                        placeholder.append(":").append(args.pop());
                    }
                    placeholder.append(">");

                    return Tag.inserting(Component.text(placeholder.toString()));
                }
                return Tag.inserting(parser.parse(viewer, context));
            }));
        }

        return resolvers.values().toArray(new TagResolver[]{});
    }
}
