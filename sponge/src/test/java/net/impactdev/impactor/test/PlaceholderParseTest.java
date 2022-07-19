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

package net.impactdev.impactor.test;

import com.google.common.collect.Lists;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.placeholders.PlaceholderSources;
import net.impactdev.impactor.api.services.parsing.PlaceholderParser;
import net.impactdev.impactor.api.services.parsing.GenericTextParser;
import net.impactdev.impactor.common.api.ApiRegistrationUtil;
import net.impactdev.impactor.common.placeholders.PlaceholderSourcesImpl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderParseTest {

    private static final Pattern TOKEN_LOCATOR = Pattern.compile("(^[^{]+)?(?<raw>[{][{](?<placeholder>[\\w-:]+)(\\|(?<arguments>.+))?[}][}])(.+)?");
    @Before
    public void before() {
        ApiRegistrationUtil.register(new TestAPIProvider());
        Impactor.getInstance().getRegistry().registerBuilderSupplier(PlaceholderSources.SourceBuilder.class,
                PlaceholderSourcesImpl.PlaceholderSourcesBuilderImpl::new);
    }

//    @Test
//    public void simple() {
//        final String text = "Testing {{placeholder}} {{placeholder2:s}}";
//        List<StringParser> tokens = this.tokenize(text);
//        for(StringParser content : tokens) {
//            System.out.println(content);
//        }
//    }

    @Test
    public void larger() {
        final String text = "&7Testing a string with a set of text before the &3first {{placeholder}}, then with &aanother {{placeholder2:s}}";
        Component result = this.translate(text);
        System.out.println(LegacyComponentSerializer.legacyAmpersand().serialize(result));
    }

    @Test
    public void general() {
        final String text = "&a&l{{gts:prefix}} &7Testing a larger normal string";
        Component result = this.translate(text);
        System.out.println(LegacyComponentSerializer.legacyAmpersand().serialize(result));
    }

    @Test
    public void lineStylingTest() {
        final String text = "&8&m=======================";
        System.out.println(LegacyComponentSerializer.legacyAmpersand().serialize(this.translate(text)));
    }

    @Test
    public void reforgedLoreTest() {
        final List<String> pre = Lists.newArrayList(
                "&aGeneric Information:",
                "  &7Form: &e{{gts-reforged:form}}",
                "  &7Ability: &e{{gts-reforged:ability}}",
                "  &7Gender: {{gts-reforged:gender}}",
                "  &7Nature: &e{{gts-reforged:nature}}",
                "  &7Size: &e{{gts-reforged:size}}",
                "  &7Breed Status: {{gts-reforged:unbreedable}}",
                "",
                "&aStats:",
                "  &7EVs: &e{{gts-reforged:ev_hp}}&7/&e{{gts-reforged:ev_attack}}&7/&e{{gts-reforged:ev_defence}}&7/&e{{gts-reforged:ev_specialattack}}&7/&e{{gts-reforged:ev_specialdefence}}&7/&e{{gts-reforged:ev_speed}} &7(&b{{gts-reforged:ev_percentage}}&7)",
                "  &7IVs: &e{{gts-reforged:iv_hp}}&7/&e{{gts-reforged:iv_attack}}&7/&e{{gts-reforged:iv_defence}}&7/&e{{gts-reforged:iv_specialattack}}&7/&e{{gts-reforged:iv_specialdefence}}&7/&e{{gts-reforged:iv_speed}} &7(&b{{gts-reforged:iv_percentage}}&7)"
        );

        pre.stream()
                .map(this::translate)
                .forEach(component -> System.out.println(LegacyComponentSerializer.legacyAmpersand().serialize(component)));
    }

    private Component translate(String message) {
        Stack<Component> components = new Stack<>();

        String reference = message;
        while(!reference.isEmpty()) {
            Matcher matcher = TOKEN_LOCATOR.matcher(reference);
            if(matcher.find()) {
                String placeholder = matcher.group("placeholder");
                String arguments = matcher.group("arguments");

                if(matcher.group(1) != null) {
                    GenericTextParser generic = new GenericTextParser(matcher.group(1));
                    components.addAll(generic.components());
                    reference = reference.replaceFirst("^[^{]+", "");
                }

                PlaceholderParser parser = new TestPlaceholderParser(matcher.group("raw"), placeholder, arguments, PlaceholderSources.empty());
                components.addAll(parser.components());

                reference = reference.replaceFirst("[{][{]([\\w-:]+)(\\|(.+))?[}][}]", "");
            } else {
                GenericTextParser generic = new GenericTextParser(reference);
                components.addAll(generic.components());
                break;
            }
        }

        Component result = null;
        while(!components.empty()) {
            Component component = components.pop();
            if(result == null) {
                result = component;
            } else {
                result = component.append(result);
            }
        }

        return result == null ? Component.empty() : result;
    }
}
