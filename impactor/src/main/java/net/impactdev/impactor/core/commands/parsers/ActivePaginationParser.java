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

import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import com.google.common.base.Suppliers;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.commands.CommandSource;
import net.impactdev.impactor.core.text.pagination.ActivePagination;
import net.impactdev.impactor.core.text.pagination.PaginationService;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActivePaginationParser implements ArgumentParser<CommandSource, ActivePagination> {

    private static final Supplier<PaginationService> SERVICE = Suppliers.memoize(() -> Impactor.instance().services().provide(PaginationService.class));
    private static final Pattern UUID_PATTERN = Pattern.compile("^[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}$");

    @Override
    public @NonNull ArgumentParseResult<@NonNull ActivePagination> parse(@NonNull CommandContext<@NonNull CommandSource> context, @NonNull Queue<@NonNull String> args) {
        final String input = args.peek();
        if(input == null) {
            return ArgumentParseResult.failure(new NoInputProvidedException(
                    ActivePaginationParser.class,
                    context
            ));
        }

        Matcher matcher = UUID_PATTERN.matcher(input);
        if(matcher.matches()) {
            UUID uuid = UUID.fromString(input);
            PaginationService service = SERVICE.get();

            @Nullable ActivePagination pagination = service.pagination(uuid).orElse(null);
            if(pagination != null) {
                args.poll();
                return ArgumentParseResult.success(pagination);
            }
        }

        return ArgumentParseResult.failure(new IllegalArgumentException("No active pagination for that ID"));
    }

    @Override
    public @NonNull List<@NonNull String> suggestions(@NonNull CommandContext<CommandSource> commandContext, @NonNull String input) {
        return ArgumentParser.super.suggestions(commandContext, input);
    }

}
