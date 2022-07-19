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

package net.impactdev.impactor.api.services.parsing;

import com.google.common.collect.Sets;
import net.impactdev.impactor.api.placeholders.PlaceholderSources;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class PlaceholderParser implements StringParser {

    private static final Pattern MODIFIERS = Pattern.compile("(:[sp]+)$", Pattern.CASE_INSENSITIVE);
    protected final Set<TextModifiers> modifiers = Sets.newHashSet();
    protected final String value;
    protected final String arguments;
    protected final PlaceholderSources sources;

    protected final String raw;

    public PlaceholderParser(String raw, String placeholder, String arguments, PlaceholderSources sources) {
        this.raw = raw;

        Matcher matcher = MODIFIERS.matcher(placeholder);
        if(matcher.find()) {
            String match = matcher.group(1).toLowerCase();
            for (TextModifiers modifier : TextModifiers.values()) {
                if(match.contains(modifier.getKey())) {
                    modifiers.add(modifier);
                }
            }
            this.value = placeholder.replace(match, "");
        } else {
            this.value = placeholder;
        }

        this.arguments = arguments;
        this.sources = sources;
    }

    @Override
    public String value() {
        return this.value;
    }

    @Override
    public String toString() {
        return "TestPlaceholder{" +
                "modifiers=" + modifiers +
                ", value='" + value + '\'' +
                ", arguments='" + arguments + '\'' +
                '}';
    }
}
