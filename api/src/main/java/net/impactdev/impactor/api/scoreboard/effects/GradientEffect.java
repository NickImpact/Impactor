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

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import net.impactdev.impactor.api.builders.Builder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;

import java.util.List;
import java.util.PrimitiveIterator;

/**
 * Due to MiniMessage only working on raw input, we needed to port the logic to handle components as well.
 *
 * Logic based on MiniMessage, licensed under MIT.
 */
public class GradientEffect implements FrameEffect {

    private final List<TextColor> colors;

    private int index = 0;
    private int colorIndex = 0;

    private float factorStep = 0;
    private float phase = 0;
    private boolean negativePhase = false;

    private GradientEffect(GradientEffectBuilder builder) {
        this.colors = Lists.newArrayList(builder.start);
        this.colors.addAll(builder.additional);
    }

    @Override
    public String getID() {
        return "gradient";
    }

    @Override
    public Component translate(Component parent) {
        if(parent instanceof TextComponent) {
            final TextComponent textComponent = (TextComponent) parent;
            final String content = this.decode(textComponent);

            TextComponent.Builder builder = Component.text();

            // init
            final int size = content.length();
            final int sectorLength = size / (this.colors.size() - 1);
            this.factorStep = 1.0f / (sectorLength + this.index);
            this.phase = this.phase * sectorLength;
            this.index = 0;

            // apply
            int charSize;
            final char[] holder = new char[2];
            for(final PrimitiveIterator.OfInt it = content.codePoints().iterator(); it.hasNext();) {
                charSize = Character.toChars(it.nextInt(), holder, 0);
                Component comp = Component.text(new String(holder, 0, charSize));
                comp = this.merge(comp, parent);
                comp = comp.color(this.color());
                builder.append(comp);
            }

            return builder.build();
        }

        throw new IllegalArgumentException("Expected TextComponent, got " + parent.getClass().toString());
    }

    protected Component merge(final Component target, final Component template) {
        Component result = target.style(target.style().merge(template.style(), Style.Merge.Strategy.IF_ABSENT_ON_TARGET, Style.Merge.all()));

        if(template.hoverEvent() != null) {
            result = result.hoverEvent(template.hoverEvent());
        }
        if(template.clickEvent() != null) {
            result = result.clickEvent(template.clickEvent());
        }
        if(template.insertion() != null) {
            result = result.insertion(template.insertion());
        }

        return result;
    }

    private TextColor color() {
        // color switch needed?
        if(this.factorStep * this.index > 1) {
            this.colorIndex++;
            this.index = 0;
        }

        float factor = this.factorStep * (this.index++ + this.phase);
        // loop around if needed
        if(factor > 1) {
            factor = 1 - (factor - 1);
        }

        if(this.negativePhase && this.colors.size() % 2 != 0) {
            // flip the gradient segment for to allow for looping phase -1 through 1
            return this.interpolate(this.colors.get(this.colorIndex + 1), this.colors.get(this.colorIndex), factor);
        } else {
            return this.interpolate(this.colors.get(this.colorIndex), this.colors.get(this.colorIndex + 1), factor);
        }
    }

    private TextColor interpolate(final TextColor color1, final TextColor color2, final float factor) {
        return TextColor.color(
                Math.round(color1.red() + factor * (color2.red() - color1.red())),
                Math.round(color1.green() + factor * (color2.green() - color1.green())),
                Math.round(color1.blue() + factor * (color2.blue() - color1.blue()))
        );
    }

    public static GradientEffectBuilder builder() {
        return new GradientEffectBuilder();
    }

    public static class GradientEffectBuilder implements Builder<GradientEffect> {

        private TextColor start = NamedTextColor.BLACK;
        private List<TextColor> additional = Lists.newArrayList();

        public GradientEffectBuilder start(TextColor color) {
            this.start = color;
            return this;
        }

        public GradientEffectBuilder next(TextColor color) {
            this.additional.add(color);
            return this;
        }

        @Override
        public GradientEffect build() {
            Preconditions.checkArgument(this.additional.size() >= 1);
            return new GradientEffect(this);
        }
    }

}
