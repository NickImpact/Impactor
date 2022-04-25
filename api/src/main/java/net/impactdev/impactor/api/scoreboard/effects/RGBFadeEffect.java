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

package net.impactdev.impactor.api.scoreboard.effects;

import net.impactdev.impactor.api.builders.Builder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.HSVLike;

import java.util.concurrent.atomic.AtomicInteger;

public class RGBFadeEffect implements FrameEffect {

    private final int frames;
    private final int step;
    private final AtomicInteger index;

    private RGBFadeEffect(RGBFadeEffectBuilder builder) {
        this.frames = builder.frames;
        this.step = builder.step;
        this.index = new AtomicInteger(builder.start);
    }

    @Override
    public String getID() {
        return "fade";
    }

    @Override
    public Component translate(Component parent) {
        if(parent instanceof TextComponent) {
            TextComponent.Builder builder = Component.text();
            String content = this.decode(parent);

            int spacer = 360 / frames;
            int hue = this.index.getAndUpdate(current -> current = (current + this.step) % 360);

            int index = 0;
            for(char c : content.toCharArray()) {
                TextColor color = TextColor.color(HSVLike.hsvLike((hue + (index++ * spacer)) % 360.0F / 360.0F, 1.0F, 1.0F));
                Component next = Component.text(c).color(color);
                builder.append(next);
            }

            return builder.build();
        }

        throw new IllegalArgumentException("Expected TextComponent, got " + parent.getClass().toString());
    }

    public static RGBFadeEffectBuilder builder() {
        return new RGBFadeEffectBuilder();
    }

    public static class RGBFadeEffectBuilder implements Builder<RGBFadeEffect> {

        private int frames;
        private int step;
        private int start;

        /**
         * Specifies the amount of frames this effect will be able to step through for the fade effect.
         * Larger values result in a much larger color spectrum, but will likely not be fully visible on the screen
         * at one time. For smoother transitions between colors, it's definitely recommended using a high
         * value for the frame count.
         *
         * @param frames The amount of frames this effect will step through
         * @return This builder, updated via this call
         */
        public RGBFadeEffectBuilder frames(int frames) {
            this.frames = frames;
            return this;
        }

        public RGBFadeEffectBuilder step(int step) {
            this.step = step;
            return this;
        }

        public RGBFadeEffectBuilder start(int start) {
            this.start = start;
            return this;
        }

        @Override
        public RGBFadeEffect build() {
            return new RGBFadeEffect(this);
        }

    }

}
