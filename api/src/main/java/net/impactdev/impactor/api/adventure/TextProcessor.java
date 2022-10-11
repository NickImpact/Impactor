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

package net.impactdev.impactor.api.adventure;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.utilities.context.Context;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public interface TextProcessor {

    /**
     * Provides a text processor which uses mini message to process raw message strings. The inputs
     * for this processor are expected to be using MiniMessage's desired tag format. Strings not under
     * this pattern will appear effectively unparsed.
     *
     * <p>Placeholders under this processor are expected to be in the MiniMessage tag style. When
     * it comes to specifying the tag, the key registration for the placeholder will appear in the
     * following format: &lt;(namespace)-(value)&gt;
     *
     * @return A text processor based around MiniMessage text processing
     */
    static TextProcessor mini() {
        return Impactor.instance().factories().provide(Factory.class).mini();
    }

    /**
     * Provides a text processor which is based around the
     * {@link net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer} from
     * Adventure. This processor focuses on the normal format we are all familiar with,
     * for example, strings like "&aHello World!". With this method, you can choose the exact
     * character you wish for the processor to handle.
     *
     * @param character The character indicating text styling
     * @return A text processor based around legacy principles which uses the given character
     */
    static TextProcessor legacy(char character) {
        return Impactor.instance().factories().provide(Factory.class).legacy(character);
    }

    /**
     * Translates a raw string into a renderable {@link Component}. This
     * attempts to resolve placeholders with an empty context set, so placeholders that require
     * additional context may produce empty results.
     *
     * @param raw The raw string
     * @return A {@link Component} representing the result
     */
    default @NotNull Component parse(String raw) {
        return this.parse(raw, Context.empty());
    }

    /**
     * Translates a raw string given a set of context into a renderable {@link Component}. This
     * method accepts context built by the caller that can be used in an effort to help placeholders
     * parse relative content.
     *
     * @param raw The raw string
     * @param context Context used to aid in placeholder parsing
     * @return A {@link Component} representing the result
     */
    @NotNull Component parse(String raw, Context context);

    /**
     * Translates a set of raw strings into a renderable list of {@link Component Components}. This
     * attempts to resolve placeholders with an empty context set, so placeholders that require
     * additional context may produce empty results.
     *
     * @param raw The list of raw strings to translate
     * @return A transformed list of parsed and renderable {@link Component Components}
     */
    default List<@NotNull Component> parse(List<String> raw) {
        return this.parse(raw, Context.empty());
    }

    /**
     * Translates a set of raw strings into a renderable list of {@link Component Components}. This
     * method accepts context built by the caller that can be used in an effort to help placeholders
     * parse relative content.
     *
     * @param raw The list of raw strings to translate
     * @param context A set of context relative to placeholder parsing
     * @return A transformed list of parsed and renderable {@link Component Components}
     */
    default List<@NotNull Component> parse(List<String> raw, Context context) {
        return raw.stream().map(input -> this.parse(input, context)).collect(Collectors.toList());
    }

    /**
     * Represents a utility factory capable of providing registered instances of different
     * {@link TextProcessor TextProcessors}. Rather than implementing this interface, you
     * should use {@link TextProcessor#mini()} or {@link TextProcessor#legacy(char)} to ask
     * the Impactor implementation to provide these processors.
     */
    interface Factory {

        /**
         * Provides a text processor which uses mini message to process raw message strings. The inputs
         * for this processor are expected to be using MiniMessage's desired tag format. Strings not under
         * this pattern will appear effectively unparsed.
         *
         * <p>Placeholders under this processor are expected to be in the MiniMessage tag style. When
         * it comes to specifying the tag, the key registration for the placeholder will appear in the
         * following format: &lt;(namespace)-(value)&gt;
         *
         * @return A text processor based around MiniMessage text processing
         */
        TextProcessor mini();

        /**
         * Provides a text processor which is based around the
         * {@link net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer} from
         * Adventure. This processor focuses on the normal format we are all familiar with,
         * for example, strings like "&aHello World!". With this method, you can choose the exact
         * character you wish for the processor to handle.
         *
         * @param character The character indicating text styling
         * @return A text processor based around legacy principles which uses the given character
         */
        TextProcessor legacy(char character);

    }

}
