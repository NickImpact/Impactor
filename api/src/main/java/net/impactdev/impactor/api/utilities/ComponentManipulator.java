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

package net.impactdev.impactor.api.utilities;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;

public class ComponentManipulator {

    public static String flatten(Component component) {
        StringBuilder result = new StringBuilder();
        if(component instanceof TextComponent) {
            result = new StringBuilder(((TextComponent) component).content());
        }

        if(component.children().isEmpty()) {
            for(Component child : component.children()) {
                result.append(flatten(child));
            }
        }

        return result.toString();
    }

    /**
     * The idea behind this method is that Minecraft with 1.16.5 changed a client setting
     * that enforces italics on custom item titles/lores where no italic flag is set (aka
     * {@link TextDecoration.State#NOT_SET}).
     *
     * <p>With the incoming source, if the source does not specify italic decoration states,
     * this method will update the source of the component to forcibly set italics to false.
     *
     * @param source The source component
     * @return A new component with the source as a child, and italics defaulted to off
     */
    public static Component noItalics(Component source) {
        TextDecoration.State state = source.style().decoration(TextDecoration.ITALIC);
        if(state == TextDecoration.State.NOT_SET) {
            return source.style(source.style().decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        }

        return source;
    }

}
