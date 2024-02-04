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

package net.impactdev.impactor.test.mail;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.mail.MailMessage;
import net.impactdev.impactor.api.mail.MailService;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GeneralMailTest {

    private static final UUID GENERIC = UUID.randomUUID();

    @AfterAll
    public static void clean() throws IOException {
        Path config = Paths.get("config");
        FileUtils.deleteDirectory(config.toFile());
    }

    @Test
    public void publish() {
        MailMessage message = MailMessage.create(Component.text("This is another piece of testing mail"));
        MailService service = Impactor.instance().services().provide(MailService.class);

        boolean result = service.sendFromServer(GENERIC, "This is a piece of testing mail").join();
        assertTrue(result);

        result = service.send(GENERIC, message).join();
        assertTrue(result);

        result = service.send(GENERIC, MailMessage.create(UUID.randomUUID(), Component.text("Testing"))).join();
        assertTrue(result);

        Component complex = Component.text("Complex message").color(TextColor.color(255, 69, 123));
        complex = complex.style(complex.style().hoverEvent(HoverEvent.showItem(Key.key("minecraft:grass"), 5)));
        complex = complex.style(complex.style().clickEvent(ClickEvent.copyToClipboard("Testing")));

        result = service.send(GENERIC, MailMessage.create(complex)).join();
        assertTrue(result);

        complex = complex.style(complex.style().clickEvent(ClickEvent.callback(audience -> this.test())));
        result = service.send(UUID.randomUUID(), MailMessage.create(complex)).join();
        assertTrue(result);
    }

    @Test
    public void inbox() {
        MailService service = Impactor.instance().services().provide(MailService.class);
        List<MailMessage> inbox = service.inbox(GENERIC).join();
        assertEquals(4, inbox.size());
    }

    private void test() {}
}
