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

package net.impactdev.impactor.core.text.pagination;

import com.google.common.base.Strings;
import net.impactdev.impactor.api.Impactor;
import net.kyori.adventure.text.BuildableComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.translation.GlobalTranslator;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.math.GenericMath;

import java.text.MessageFormat;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.Locale;
import java.util.PrimitiveIterator;

final class PaginationCalculator {

    private static final int LINE_WIDTH = 320;
    private static final String NON_UNICODE_CHARS;
    private static final int[] NON_UNICODE_CHAR_WIDTHS;
    private static final byte[] UNICODE_CHAR_WIDTHS;

    private final int lines;

    PaginationCalculator(final int lines) {
        this.lines = lines;
    }

    static {
        final ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder()
                .url(PaginationCalculator.class.getResource("font-sizes.json"))
                .build();

        try {
            final CommentedConfigurationNode node = loader.load();
            NON_UNICODE_CHARS = node.node("non-unicode").getString();
            NON_UNICODE_CHAR_WIDTHS = node.node("char-widths").get(int[].class, new int[]{});
            UNICODE_CHAR_WIDTHS = node.node("glyph-widths").get(byte[].class, new byte[]{});
        } catch (final ConfigurateException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    int getMaxLinesPerPage() {
        return this.lines;
    }

    /**
     * Gets the number of lines the specified text flows into.
     *
     * @param text The text to calculate the number of lines for
     * @return The number of lines that this text flows into
     */
    int getLines(final Component text) {
        return (int) Math.ceil((double) this.width(text) / PaginationCalculator.LINE_WIDTH);
    }

    Component center(Component text, Component padding) {
        int inputLength = this.width(text);
        //Minecraft breaks lines when the next character would be > then LINE_WIDTH, this seems most graceful way to fail
        if (inputLength >= PaginationCalculator.LINE_WIDTH) {
            return text;
        }
        final Component textWithSpaces = this.addSpaces(Component.space(), text);

        //Minecraft breaks lines when the next character would be > then LINE_WIDTH
        final boolean addSpaces = this.width(textWithSpaces) <= PaginationCalculator.LINE_WIDTH;

        int paddingLength = this.width(padding);
        final TextComponent.Builder output = Component.text();

        //Using 0 width unicode symbols as padding throws us into an unending loop, replace them with the default padding
        if (paddingLength < 1) {
            padding = ImpactorPaginatedText.DEFAULT_PADDING;
            paddingLength = this.width(padding);
        }

        //if we only need padding
        if (inputLength == 0) {
            this.addPadding(padding, output, GenericMath.floor((double) PaginationCalculator.LINE_WIDTH / paddingLength));
        } else {
            if (addSpaces) {
                text = textWithSpaces;
                inputLength = this.width(textWithSpaces);
            }

            final int paddingNecessary = PaginationCalculator.LINE_WIDTH - inputLength;

            final int paddingCount = GenericMath.floor(paddingNecessary / paddingLength);
            //pick a halfway point
            final int beforePadding = GenericMath.floor(paddingCount / 2.0);
            //Do not use ceil, this prevents floating point errors.
            final int afterPadding = paddingCount - beforePadding - 1;

            this.addPadding(padding, output, beforePadding);
            output.append(text);
            this.addPadding(padding, output, afterPadding);
        }

        return this.finalizeBuilder(text, output);
    }

    /**
     * Finalizes the builder used in centering text.
     *
     * @param text The text to get the style from
     * @param build The work in progress text builder
     * @return The finalized, properly styled text.
     */
    private Component finalizeBuilder(final Component text, final TextComponent.Builder build) {
        return build.style(text.style()).build();
    }

    /**
     * Adds spaces to both sides of the specified text.
     *
     * <p>Overrides all color and style with the
     * text's color and style.</p>
     *
     * @param spaces The spaces to use
     * @param text The text to add to
     * @return The text with the added spaces
     */
    private Component addSpaces(final BuildableComponent<?, ?> spaces, final Component text) {
        return spaces.toBuilder()
                .style(text.style())
                .append(text.style(Style.empty()))
                .append(spaces)
                .build();
    }

    /**
     * Adds the specified padding text to a piece of text being built
     * up to a certain amount specified by a count.
     *
     * @param padding The padding text to use
     * @param build The work in progress text to add to
     * @param count The amount of padding to add
     */
    private void addPadding(final Component padding, final TextComponent.Builder build, final int count) {
        if (count > 0) {
            // In simple cases, we can create a more compact component
            if (padding instanceof TextComponent && padding.children().isEmpty()) {
                build.append(Component.text(Strings.repeat(((TextComponent) padding).content(), count), padding.style()));
            } else {
                build.append(Collections.nCopies(count, padding));
            }
        }
    }

    private int width(final Component text) {
        final Deque<Component> children = new ArrayDeque<>(1 + text.children().size());
        children.add(text);
        int total = 0;

        Component child;
        while((child = children.pollFirst()) != null) {
            for(final Component grandchild : child.children()) {
                children.add(grandchild.style(child.style().merge(grandchild.style())));
            }

            final PrimitiveIterator.OfInt iterator;
            if(child instanceof TextComponent) {
                iterator = ((TextComponent) child).content().codePoints().iterator();
            } else if(child instanceof TranslatableComponent) {
                final TranslatableComponent component = (TranslatableComponent) child;
                final MessageFormat global = GlobalTranslator.translator().translate(component.key(), Locale.US);
                if (global != null) {
                    iterator = global.toPattern().codePoints().iterator();
                    children.addAll(component.args());
                } else {
                    final String mc = Impactor.instance().services().provide(LanguageProcessor.class).getOrDefault(component.key());
                    if (!mc.equals(component.key())) {
                        children.addAll(component.args());
                    }
                    iterator = mc.codePoints().iterator();
                }
            } else {
                continue;
            }

            final boolean bold = child.style().hasDecoration(TextDecoration.BOLD);
            int code;
            boolean newline = false;
            while(iterator.hasNext()) {
                code = iterator.next();

                if(code == '\n') {
                    if(newline) {
                        total += PaginationCalculator.LINE_WIDTH;
                    } else {
                        total = ((int) Math.ceil((double) total / PaginationCalculator.LINE_WIDTH)) * PaginationCalculator.LINE_WIDTH;
                        newline = true;
                    }
                } else {
                    final int width = this.width(code, bold);
                    total += width;
                    newline = false;
                }
            }
        }

        return total;
    }

    private int width(final int code, final boolean bold) {
        final int nonUnicodeIdx = PaginationCalculator.NON_UNICODE_CHARS.indexOf(code);
        int width;
        if (code == 32) {
            width = 4;
        } else if (code > 0 && nonUnicodeIdx != -1) {
            width = PaginationCalculator.NON_UNICODE_CHAR_WIDTHS[nonUnicodeIdx];
        } else if (PaginationCalculator.UNICODE_CHAR_WIDTHS[code] != 0) {
            //from 1.9 & 255 to avoid strange signed int math ruining things.
            //https://bugs.mojang.com/browse/MC-7181
            final int temp = PaginationCalculator.UNICODE_CHAR_WIDTHS[code] & 255;
            // Split into high and low nibbles.
            //bit digits
            //87654321 >>> 4 = 00008765
            final int startColumn = temp >>> 4;
            //87654321 & 00001111 = 00004321
            final int endColumn = temp & 15;

            width = (endColumn + 1) - startColumn;
            //Why does this scaling happen?
            //I believe it makes unicode fonts skinnier to better match the character widths of the default Minecraft
            // font however there is a int math vs float math bug in the Minecraft FontRenderer.
            //The float math is adjusted for rendering, they attempt to do the same thing for calculating string widths
            //using integer math, this has potential rounding errors, but we should copy it and use ints as well.
            width = (width / 2) + 1;
        } else {
            width = 0;
        }
        //if bolded width gets 1 added.
        if (bold && width > 0) {
            width = width + 1;
        }

        return width;
    }

}
