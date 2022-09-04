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

package net.impactdev.impactor.testing.ui;

import com.google.common.collect.Lists;
import io.leangen.geantyref.TypeToken;
import net.impactdev.impactor.api.items.ImpactorItemStack;
import net.impactdev.impactor.api.items.types.ItemTypes;
import net.impactdev.impactor.api.ui.containers.Icon;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IconTests {

    @Test
    public void basic() {
        ImpactorItemStack stack = ImpactorItemStack.basic()
                .type(ItemTypes.GRASS)
                .title(Component.text("Basic Stack"))
                .unbreakable()
                .build();

        Icon icon = Icon.builder()
                .display(() -> stack)
                .constant()
                .build();

        assertEquals(0, icon.context().size());
        assertFalse(icon.refreshable());

        ImpactorItemStack result = icon.display().get();
        assertEquals(ItemTypes.GRASS, result.type());
        assertTrue(result.unbreakable());
    }

    @Test
    public void withContext() {
        ImpactorItemStack stack = ImpactorItemStack.basic()
                .type(ItemTypes.GRASS)
                .title(Component.text("Basic Stack"))
                .unbreakable()
                .build();

        final TypeToken<List<Boolean>> KEY = new TypeToken<List<Boolean>>() {};
        Icon icon = Icon.builder()
                .display(() -> stack)
                .constant()
                .append(int.class, 17)
                .append(TypeToken.get(String.class), "Hello World!")
                .append(KEY, Lists.newArrayList(true, false, true))
                .build();

        assertFalse(icon.context().isEmpty());
        assertEquals(3, icon.context().size());
        assertEquals(17, icon.context().require(int.class));
        assertEquals("Hello World!", icon.context().require(String.class));
        assertEquals(3, icon.context().require(KEY).size());
        assertTrue(icon.context().require(KEY).get(0));
        assertFalse(icon.context().require(KEY).get(1));
        assertTrue(icon.context().require(KEY).get(2));
    }

    @Test
    public void missingDisplayProvider() {
        Exception exception = assertThrows(NullPointerException.class, () -> Icon.builder().build());
        assertEquals("Display provider was null", exception.getMessage());
    }

}
