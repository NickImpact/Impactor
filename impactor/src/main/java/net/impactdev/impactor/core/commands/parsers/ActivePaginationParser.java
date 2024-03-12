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

import com.google.common.base.Suppliers;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.commands.CommandSource;
import net.impactdev.impactor.core.text.pagination.ActivePagination;
import net.impactdev.impactor.core.text.pagination.PaginationService;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActivePaginationParser implements ArgumentParser<CommandSource, ActivePagination> {

    private static final Supplier<PaginationService> SERVICE = Suppliers.memoize(() -> Impactor.instance().services().provide(PaginationService.class));
    private static final Pattern UUID_PATTERN = Pattern.compile("^[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}$");

    @Override
    public @NonNull ArgumentParseResult<@NonNull ActivePagination> parse(@NonNull CommandContext<@NonNull CommandSource> context, @NonNull CommandInput input) {
        final String argument = input.peekString();
        Matcher matcher = UUID_PATTERN.matcher(argument);
        if(matcher.matches()) {
            UUID uuid = UUID.fromString(argument);
            PaginationService service = SERVICE.get();

            @Nullable ActivePagination pagination = service.pagination(uuid).orElse(null);
            if(pagination != null) {
                input.readString();
                return ArgumentParseResult.success(pagination);
            }
        }

        return ArgumentParseResult.failure(new IllegalArgumentException("No active pagination for that ID"));
    }

}
