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

package net.impactdev.impactor.minecraft.scoreboard.display.formatters;

import net.impactdev.impactor.api.scoreboards.display.formatters.ColorFormatter;
import net.impactdev.impactor.api.scoreboards.display.formatters.rgb.ColorCycle;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.HSVLike;

public class ColorCycleFormatter extends ColorFormatter implements ColorCycle {

    private final int frames;
    private final int increment;

    private final int space;

    private int phase;
    private float hue;

    private int index = 0;

    protected ColorCycleFormatter(FormatterConfig config) {
        super(config.locked);
        this.frames = config.frames;
        this.increment = config.increment;
        this.phase = config.phase;

        this.space = 360 / this.frames;
    }

    @Override
    protected void advance(int length) {
        this.index++;
    }

    @Override
    protected TextColor color(int length) {
        HSVLike hsv = HSVLike.hsvLike((this.hue + (this.index * this.space)) % 360.0F / 360.0F, 1.0F, 1.0F);
        return TextColor.color(hsv);
    }

    @Override
    public void step() {
        this.phase += this.increment;
        this.phase %= 360;

        this.hue = this.phase;
        this.index = 0;
    }

    @Override
    public int increment() {
        return this.increment;
    }

    @Override
    public int frames() {
        return this.frames;
    }

    @Override
    public int phase() {
        return this.phase;
    }

    public static final class FormatterConfig implements Config {

        private int frames;
        private int increment;
        private int phase;

        private boolean locked;

        @Override
        public Config frames(int frames) {
            this.frames = frames;
            return this;
        }

        @Override
        public Config phase(int phase) {
            this.phase = phase;
            return this;
        }

        @Override
        public Config increment(int increment) {
            this.increment = increment;
            return this;
        }

        @Override
        public ColorCycle build() {
            return new ColorCycleFormatter(this);
        }

    }
}
