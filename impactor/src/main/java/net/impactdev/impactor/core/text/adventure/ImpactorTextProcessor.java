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

import com.google.common.base.Suppliers;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.platform.sources.PlatformSource;
import net.impactdev.impactor.api.text.TextProcessor;
import net.impactdev.impactor.api.text.placeholders.PlaceholderArguments;
import net.impactdev.impactor.api.text.placeholders.PlaceholderParser;
import net.impactdev.impactor.api.text.placeholders.PlaceholderService;
import net.impactdev.impactor.api.utility.Context;
import net.impactdev.impactor.core.plugin.BaseImpactorPlugin;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ImpactorTextProcessor implements TextProcessor {

    private static final Pattern PLACEHOLDER = Pattern.compile("\\{\\{(?<placeholder>[a-z0-9_\\-.]+:[a-z0-9_\\-./]+)(/(?<args>[\\w;#]+))?}}");
    private final Supplier<PlaceholderService> service = Suppliers.memoize(() -> Impactor.instance().services().provide(PlaceholderService.class));

    protected abstract String serialize(Component component);
    protected abstract Component deserialize(String raw);

    @Override
    public @NotNull Component parse(PlatformSource viewer, String raw, Context context) {
        return this.deserialize(this.preprocess(viewer, raw, context));
    }

    private String preprocess(PlatformSource viewer, String raw, Context context) {
        PlaceholderService service = this.service.get();
        String result = raw;

        Matcher matcher = PLACEHOLDER.matcher(raw);
        while(matcher.find()) {
            String match = matcher.group(0);

            String placeholder = matcher.group("placeholder");
            PlaceholderParser parser = service.parsers().get(Key.key(placeholder));
            if(parser != null) {
                Optional<PlaceholderArguments> arguments = Optional.ofNullable(matcher.group("args"))
                        .map(args -> new PlaceholderArguments(args.split(";")));

                Context ctx = Context.empty().with(context);
                arguments.ifPresent(args -> ctx.append(PlaceholderArguments.class, args));

                result = result.replace(match, this.serialize(parser.parse(viewer, ctx)));
            }
        }

        return result;
    }

}
