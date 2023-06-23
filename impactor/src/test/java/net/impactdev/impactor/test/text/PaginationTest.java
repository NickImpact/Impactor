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

import net.impactdev.impactor.api.text.pagination.PaginatedText;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.ansi.ANSIComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

public class PaginationTest {

    @Test
    public void createPagination() {
        Audience audience = new Audience() {
            @Override
            public void sendMessage(@NotNull Identity source, @NotNull Component msg, @NotNull MessageType type) {
                System.out.println(ANSIComponentSerializer.ansi().serialize(msg));
            }
        };

        PaginatedText pagination = PaginatedText.builder()
                .title(Component.text("Impactor Test").color(NamedTextColor.YELLOW))
                .padding(Component.text("=").color(NamedTextColor.GREEN))
                .contents(Component.text("Testing"), Component.text("123"))
                .build();
        pagination.send(audience);

        PaginatedText noTitle = PaginatedText.builder()
                .padding(Component.text("=").color(NamedTextColor.GREEN))
                .contents(Component.text("Testing"), Component.text("123"))
                .build();
        noTitle.send(audience);
    }

}
