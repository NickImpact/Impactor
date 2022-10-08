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

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.translation.GlobalTranslator;

import java.util.Locale;

/**
 * Specifies an audience which is bundled with its target locale. You can use this interface
 * to dynamically have an audience render translatable components into the target's set locale.
 *
 * <p> It should be noted that not all
 * {@link net.kyori.adventure.text.TranslatableComponent translatable components}
 * are capable of being translated through the global registry. Take Minecraft's translations for
 * example, these are handled via the actual minecraft client. See {@link net.kyori.adventure.text.TranslatableComponent}
 * for more details.
 *
 * @see net.kyori.adventure.audience.Audience
 * @see net.kyori.adventure.text.TranslatableComponent
 * @see net.kyori.adventure.translation.GlobalTranslator
 * @see net.kyori.adventure.translation.TranslationRegistry
 */
public interface LocalizedAudience extends Audience {

    /**
     * Represents the locale setting of the audience. Any translations that require translating
     * will be processed under this given locale setting via the Global Translator
     * {@link GlobalTranslator#renderer() renderer}.
     *
     * @return The locale of the audience
     */
    Locale locale();

}
