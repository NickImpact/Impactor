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

package net.impactdev.impactor.api.text;

import net.impactdev.impactor.api.core.ServiceProvider;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.pointer.Pointers;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
        return ServiceProvider.instance().provide(Factory.class).mini();
    }

    /**
     * Creates a text processor which users mini message to process raw message strings. Following the style
     * of {@link #mini()}, this processor uses a given mini message instance instead of the default Impactor
     * system (which internally is effectively the base mini message instance). Make use of this type of processor
     * if you wish to provide additional context to MiniMessage .
     *
     * @param delegate The {@link MiniMessage} backing that should be used for this text processor
     * @return A text processor based around MiniMessage text processing
     */
    static TextProcessor mini(MiniMessage delegate) {
        return ServiceProvider.instance().provide(Factory.class).mini(delegate);
    }

    /**
     * Provides a text processor which is based around the
     * {@link net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer} from
     * Adventure. This processor focuses on the normal format we are all familiar with,
     * for example, strings like "{@literal &}aHello World!". With this method, you can choose the exact
     * character you wish for the processor to handle.
     *
     * @param character The character indicating text styling
     * @return A text processor based around legacy principles which uses the given character
     */
    static TextProcessor legacy(char character) {
        return ServiceProvider.instance().provide(Factory.class).legacy(character);
    }

    /**
     * Attempts to process the given string into a {@link Component}. This parsed text will parse placeholders
     * based on a generic empty context, meaning no relativity to the target audience. Additionally, no context
     * will be leveraged to parse placeholders where context might be required.
     *
     * @param input The raw string, relative to the processor, that should be parsed into a {@link Component}
     * @return A component representing the final output of the input
     */
    @NotNull
    default Component parse(final @NotNull String input) {
        return this.parse(Audience.empty(), input, Pointers.empty());
    }

    /**
     * Attempts to process the given string into a {@link Component} relative to the given {@link Audience}. How
     * relativity is performed against an audience is entirely up to individual placeholders. No additional context
     * will be supplied to the processor, so placeholders which might require external context face the potential
     * of failing processing.
     *
     * @param audience The audience this text should be parsed relative to
     * @param input The raw string, relative to the processor, that should be parsed into a {@link Component}
     * @return A component representing the final output of the input
     */
    @NotNull
    default Component parse(final @NotNull Audience audience, final @NotNull String input) {
        return this.parse(audience, input, Pointers.empty());
    }

    /**
     * Attempts to process the given string into a {@link Component} relative to the given {@link Audience}. How
     * relativity is performed against an audience is entirely up to individual placeholders. This call allows
     * for additional context outside the {@link Pointers} the audience would otherwise provide, allowing for
     * placeholders to reference context they wouldn't otherwise have access to.
     *
     * <p>For this reference, the {@link Audience} need not be an actual target, and can simply be
     * {@link Audience#empty()} as desired.
     *
     * @param audience The audience this text should be parsed relative to
     * @param input The raw string, relative to the processor, that should be parsed into a {@link Component}
     * @param context Additional context that might help certain placeholders properly transform into their
     *                target result
     * @return A component representing the final output of the input
     */
    @NotNull
    Component parse(final @NotNull Audience audience, final @NotNull String input, final @NotNull Pointers context);

    /**
     * Attempts to process the given string inputs into a list of {@link Component Components}. This parsed text will
     * parse placeholders based on a generic empty context, meaning no relativity to the target audience. Additionally,
     * no context will be leveraged to parse placeholders where context might be required.
     *
     * @param input The raw list of strings, relative to the processor, that should be parsed into
     *              {@link Component Components}
     * @return A list of components representing the final output of the input
     */
    default List<Component> parse(final @NotNull List<String> input) {
        return this.parse(Audience.empty(), input, Pointers.empty());
    }

    /**
     * Attempts to process the given strings into {@link Component Components} relative to the given {@link Audience}. How
     * relativity is performed against an audience is entirely up to individual placeholders. No additional context
     * will be supplied to the processor, so placeholders which might require external context face the potential
     * of failing processing.
     *
     * @param audience The audience this text should be parsed relative to
     * @param input The raw list of strings, relative to the processor, that should be parsed into
     *              {@link Component Components}
     * @return A list of components representing the final output of the input
     */
    default List<Component> parse(final @NotNull Audience audience, final @NotNull List<String> input) {
        return this.parse(audience, input, Pointers.empty());
    }

    /**
     * Attempts to process the given strings into {@link Component Components} relative to the given {@link Audience}.
     * How relativity is performed against an audience is entirely up to individual placeholders. This call allows
     * for additional context outside the {@link Pointers} the audience would otherwise provide, allowing for
     * placeholders to reference context they wouldn't otherwise have access to.
     *
     * <p>For this reference, the {@link Audience} need not be an actual target, and can simply be
     * {@link Audience#empty()} as desired.
     *
     * @param audience The audience this text should be parsed relative to
     * @param input The raw list of strings, relative to the processor, that should be parsed into
     *              {@link Component Components}
     * @param context Additional context that might help certain placeholders properly transform into their
     *                target result
     * @return A list of components representing the final output of the input
     */
    @NotNull
    default List<Component> parse(final @NotNull Audience audience, final @NotNull List<String> input, final @NotNull Pointers context) {
        return input.stream().map(raw -> this.parse(audience, raw, context)).toList();
    }

    interface Factory {

        /**
         * Provides a text processor which uses mini message to process raw message strings. The inputs
         * for this processor are expected to be using MiniMessage's desired tag format. Strings not under
         * this pattern will appear effectively unparsed.
         *
         * <p>Placeholders under this processor are expected to be in the MiniMessage tag style. When
         * it comes to specifying the tag, the key registration for the placeholder will appear in the
         * following format: &lt;(namespace):(value)&gt;
         *
         * @return A text processor based around MiniMessage text processing
         */
        TextProcessor mini();

        /**
         * Creates a text processor which users mini message to process raw message strings. Following the style
         * of {@link #mini()}, this processor uses a given mini message instance instead of the default Impactor
         * system (which internally is effectively the base mini message instance). Make use of this type of processor
         * if you wish to provide additional context to MiniMessage .
         *
         * @param delegate The {@link MiniMessage} backing that should be used for this text processor
         * @return A text processor based around MiniMessage text processing
         */
        TextProcessor mini(MiniMessage delegate);

        /**
         * Provides a text processor which is based around the
         * {@link net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer} from
         * Adventure. This processor focuses on the normal format we are all familiar with,
         * for example, strings like "{@literal &}aHello World!". With this method, you can choose the exact
         * character you wish for the processor to handle.
         *
         * @param character The character indicating text styling
         * @return A text processor based around legacy principles which uses the given character
         */
        TextProcessor legacy(char character);

    }
}
