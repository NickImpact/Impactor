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

package net.impactdev.impactor.test.text;

import net.impactdev.impactor.adventure.LegacyProcessor;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.adventure.TextProcessor;
import net.impactdev.impactor.api.utilities.context.Context;
import net.impactdev.impactor.adventure.MiniMessageProcessor;
import net.impactdev.impactor.api.placeholders.PlaceholderService;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.junit.jupiter.api.Test;

import static net.kyori.adventure.text.Component.text;
import static org.junit.jupiter.api.Assertions.assertEquals;

public final class PlaceholderParsingTest {

    @Test
    public void mini() {
        TextProcessor service = new MiniMessageProcessor();
        PlaceholderService placeholders = Impactor.instance().services().provide(PlaceholderService.class);
        LegacyComponentSerializer serializer = LegacyComponentSerializer.legacyAmpersand();
        placeholders.register(Key.key("impactor", "test"), ctx -> text("Hello World!"));

        Component result = service.parse("Testing: <impactor-test>", Context.empty());
        assertEquals("Testing: Hello World!", serializer.serialize(result));

        Component argument = service.parse("Testing:<impactor-test:s:p>!");
        assertEquals("Testing: Hello World! !", serializer.serialize(argument));
    }

    @Test
    public void ampersand() {
        TextProcessor service = new LegacyProcessor('&');
        PlaceholderService placeholders = Impactor.instance().services().provide(PlaceholderService.class);
        placeholders.register(Key.key("impactor", "test"), ctx -> text("Hello World!"));
        LegacyComponentSerializer serializer = LegacyComponentSerializer.builder().hexColors().character('&').build();

        Component result = service.parse("Ampersand: {{impactor:test}}");
        assertEquals("Ampersand: Hello World!", serializer.serialize(result));

        Component only = service.parse("{{impactor:test}}");
        assertEquals("Hello World!", serializer.serialize(only));

        Component styled = service.parse("&7Testing a gray string with a placeholder of {{impactor:test}}");
        assertEquals("&7Testing a gray string with a placeholder of Hello World!", serializer.serialize(styled));

        Component prependedStyle = service.parse("&7Testing &a{{impactor:test}}");
        assertEquals("&7Testing &aHello World!", serializer.serialize(prependedStyle));

        Component decorated = service.parse("&7Testing &a&l{{impactor:test}}");
        assertEquals("&7Testing &a&lHello World!", serializer.serialize(decorated));

        Component hex = service.parse("&#00FF00Testing {{impactor:test}}");
        assertEquals("&#00ff00Testing Hello World!", serializer.serialize(hex));

        Component prependedHex = service.parse("Testing &#00FF00{{impactor:test}}");
        assertEquals("Testing &#00ff00Hello World!", serializer.serialize(prependedHex));

        Component invalidPlaceholder = service.parse("Testing {{impactor:i-dont-exist}}");
        assertEquals("Testing {{impactor:i-dont-exist}}", serializer.serialize(invalidPlaceholder));

        Component styledInvalidPlaceholder = service.parse("&#AABBCC{{impactor:i-dont-exist}}");
        assertEquals("&#aabbcc{{impactor:i-dont-exist}}", serializer.serialize(styledInvalidPlaceholder));

        Component argumentedSpace = service.parse("{{impactor:test|s}}{{impactor:test}}");
        assertEquals("Hello World! Hello World!", serializer.serialize(argumentedSpace));
    }

}
