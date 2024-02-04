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

package net.impactdev.impactor.core.commands.parsers;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.commands.CommandSource;
import net.impactdev.impactor.api.platform.players.PlatformPlayer;
import net.impactdev.impactor.api.platform.players.PlatformPlayerService;
import net.impactdev.impactor.api.platform.sources.PlatformSource;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider;
import org.incendo.cloud.suggestion.Suggestion;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class PlatformSourceParser implements ArgumentParser<CommandSource, PlatformSource>, BlockingSuggestionProvider<CommandSource> {

    private final PlainTextComponentSerializer plain = PlainTextComponentSerializer.plainText();
    
    @Override
    public @NonNull ArgumentParseResult<@NonNull PlatformSource> parse(@NonNull CommandContext<@NonNull CommandSource> context, @NonNull CommandInput args) {
        PlatformPlayerService service = Impactor.instance().services().provide(PlatformPlayerService.class);
        Set<PlatformPlayer> online = service.online();
        Set<PlatformSource> options = new HashSet<>();
        options.add(PlatformSource.server());
        options.addAll(online);

        Optional<PlatformSource> match = options.stream()
                .filter(player -> this.plain.serialize(player.name()).equals(args.peekString()))
                .findFirst();

        return match.map(player -> {
                    args.readString();
                    return player;
                })
                .map(ArgumentParseResult::success)
                .orElseGet(() -> ArgumentParseResult.failure(new IllegalArgumentException("No match for username found...")));
    }

    @Override
    public @NonNull Iterable<@NonNull Suggestion> suggestions(@NonNull CommandContext<CommandSource> context, @NonNull CommandInput input) {
        PlatformPlayerService service = Impactor.instance().services().provide(PlatformPlayerService.class);
        List<String> names = service.online().stream()
                .map(player -> this.plain.serialize(player.name()))
                .collect(Collectors.toList());

        names.add("Server");

        return names.stream()
                .filter(name -> name.startsWith(input.peekString()))
                .map(Suggestion::simple)
                .collect(Collectors.toList());
    }

}
