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
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public final class MiniMessageProcessor implements TextProcessor {

    private final Supplier<PlaceholderService> service = Suppliers.memoize(() -> Impactor.instance().services().provide(PlaceholderService.class));
    private final MiniMessage mini;

    public MiniMessageProcessor(MiniMessage service) {
        this.mini = service;
    }

    @Override
    public @NotNull Component parse(@Nullable PlatformSource viewer, String raw, Context context) {
        return this.mini.deserialize(raw, this.createResolvers(viewer, context));
    }

    @SuppressWarnings("PatternValidation")
    private TagResolver[] createResolvers(PlatformSource viewer, Context context) {
        Map<Key, PlaceholderParser> parsers = service.get().parsers();
        Map<String, TagResolver> resolvers = Maps.newHashMap();
        Map<PlaceholderResolver, Component> results = Maps.newHashMap();

        parsers.keySet().stream()
                .map(Key::namespace)
                .distinct()
                .forEach(namespace -> {
                    resolvers.computeIfAbsent(namespace, in -> TagResolver.resolver(in, (args, ctx) -> {
                        final Tag.Argument path = args.popOr("Invalid placeholder key, no path specified");
                        Key target = Key.key(namespace, path.lowerValue());

                        PlaceholderArguments arguments = PlaceholderArguments.create(args);
                        context.append(PlaceholderArguments.class, arguments);

                        PlaceholderResolver resolver = new PlaceholderResolver(target, arguments);
                        Component result = results.computeIfAbsent(resolver, r -> {
                            return r.resolve(target, viewer, arguments, context, parsers);
                        });

                        return Tag.selfClosingInserting(result);
                    }));
                });

        return resolvers.values().toArray(new TagResolver[]{});
    }

    private static final class PlaceholderResolver {

        private final Key key;
        private final PlaceholderArguments arguments;

        @MonotonicNonNull private Component resolved;

        public PlaceholderResolver(Key key, PlaceholderArguments arguments) {
            this.key = key;
            this.arguments = arguments;
        }

        @NotNull
        public Component resolve(Key target, PlatformSource viewer, PlaceholderArguments arguments, Context context, Map<Key, PlaceholderParser> parsers) {
            if(this.resolved == null) {
                PlaceholderParser parser = parsers.get(target);
                if (parser == null) {
                    StringBuilder placeholder = new StringBuilder("<" + target.asString());
                    while (arguments.hasNext()) {
                        placeholder.append(":").append(arguments.pop());
                    }
                    placeholder.append(">");

                    return (this.resolved = Component.text(placeholder.toString()));
                }
                return (this.resolved = parser.parse(viewer, context));
            }

            return this.resolved;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PlaceholderResolver that = (PlaceholderResolver) o;
            return Objects.equals(key, that.key) && Objects.equals(arguments, that.arguments);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, arguments);
        }
    }
}
