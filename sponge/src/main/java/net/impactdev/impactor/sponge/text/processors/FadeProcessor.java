/*
 * This file is part of Impactor, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018-2021 NickImpact
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

package net.impactdev.impactor.sponge.text.processors;

import com.google.common.collect.Maps;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.HSVLike;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class FadeProcessor implements ComponentProcessor<Map<String, String>> {

    private final Map<UUID, FadeManager> fadeManagers = Maps.newHashMap();

    @Override
    public Component process(Map<String, String> input) {
        int frames = getFromArgs("frames", input, Integer::parseInt);
        String text = getFromArgs("text", input, x -> x);
        UUID id = getFromArgs("id", input, UUID::fromString);

        FadeManager manager = fadeManagers.computeIfAbsent(id, key -> new FadeManager(0));
        int spacer = 360 / frames;
        int start = manager.step();

        Component result = Component.empty();
        int index = 0;
        for(char c : text.toCharArray()) {
            TextColor color = TextColor.color(HSVLike.of((start + (index++ * spacer)) % 360.0F / 360.0F, 1.0F, 1.0F));
            Component next = Component.text(c).color(color);
            result = result.append(next);
        }

        return result;
    }

    private static <T> T getFromArgs(String key, Map<String, String> args, Function<String, T> parser) {
        AtomicReference<Throwable> error = new AtomicReference<>();
        return Optional.ofNullable(args.get(key))
                .map(value -> {
                    try {
                        return parser.apply(value);
                    } catch (Exception e) {
                        error.set(e);
                        return null;
                    }
                })
                .orElseThrow(() -> new IllegalArgumentException("Failed to decode arg with key: " + key, error.get()));
    }

    private static class FadeManager {

        private int interval;

        public FadeManager(int start) {
            this.interval = start;
        }

        public int step() {
            return this.interval++ % 360;
        }

    }
}
