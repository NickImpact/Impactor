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

package net.impactdev.impactor.core.translations.components.resolvers;

import net.impactdev.impactor.api.text.TextProcessor;
import net.impactdev.impactor.api.utility.Context;
import net.impactdev.impactor.core.translations.components.Translation;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MultiLineTranslation implements Translation<List<Component>> {

    private final List<String> template;

    public MultiLineTranslation(final List<String> template) {
        this.template = template;
    }

    @Override
    public List<Component> build(final @NotNull TextProcessor processor, @NotNull Context context) {
        return processor.parse(this.template, context);
    }

    @Override
    public void send(@NotNull Audience audience, final @NotNull TextProcessor processor, @NotNull Context context) {
        if(this.template.isEmpty()) {
            return;
        }

        List<Component> built = this.build(processor, context);
        built.forEach(audience::sendMessage);
    }

}
