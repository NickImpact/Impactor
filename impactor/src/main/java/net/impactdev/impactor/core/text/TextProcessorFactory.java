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

package net.impactdev.impactor.core.text;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import net.impactdev.impactor.api.text.TextProcessor;
import net.impactdev.impactor.core.text.processors.LegacyTextProcessor;
import net.impactdev.impactor.core.text.processors.MiniMessageProcessor;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.concurrent.TimeUnit;

public class TextProcessorFactory implements TextProcessor.Factory {

    private final MiniMessageProcessor mini = new MiniMessageProcessor(MiniMessage.miniMessage());
    private final LoadingCache<Character, LegacyTextProcessor> legacy = Caffeine.newBuilder()
            .expireAfterAccess(15, TimeUnit.MINUTES)
            .build(LegacyTextProcessor::new);

    @Override
    public TextProcessor mini() {
        return this.mini;
    }

    @Override
    public TextProcessor mini(MiniMessage delegate) {
        return new MiniMessageProcessor(delegate);
    }

    @Override
    public TextProcessor legacy(char character) {
        return this.legacy.get(character);
    }
}
