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

public class ProgressBarEffect implements FrameEffect {

    private final int size;
    private final int max;
    private final Component done;
    private final Component remaining;

    private ProgressBarEffect(ProgressBarEffectBuilder builder) {
        this.size = builder.size;
        this.max = builder.max;
        this.done = builder.done;
        this.remaining = builder.remaining;
    }

    @Override
    public String getID() {
        return "progress-bar";
    }

    @Override
    public Component translate(Component parent) {
        if(parent instanceof TextComponent) {
            String content = this.decode(parent);
            try {
                TextComponent.Builder builder = Component.text();

                int current = Integer.parseInt(content);
                float percent = ((float) current / this.max);
                if(percent >= 1.0f) {
                    for(int i = 0; i < this.size; i++) {
                        builder.append(this.done);
                    }

                    return builder.build();
                }

                int fill = (int) (percent * 100) / this.size;

            } catch (Exception e) {
                throw new IllegalArgumentException("Component contents must be an integer only!");
            }
        }

        throw new IllegalArgumentException("Expected TextComponent, got " + parent.getClass().toString());
    }

    public ProgressBarEffectBuilder builder() {
        return new ProgressBarEffectBuilder();
    }

    private static class ProgressBarEffectBuilder implements Builder<ProgressBarEffect> {

        private int size;
        private int max;
        private Component done;
        private Component remaining;

        public ProgressBarEffectBuilder size(int size) {
            this.size = size;
            return this;
        }

        public ProgressBarEffectBuilder max(int max) {
            this.max = max;
            return this;
        }

        public ProgressBarEffectBuilder done(Component text) {
            this.done = text;
            return this;
        }

        public ProgressBarEffectBuilder remaining(Component remaining) {
            this.remaining = remaining;
            return this;
        }

        @Override
        public ProgressBarEffect build() {
            return new ProgressBarEffect(this);
        }
    }
}
