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

package net.impactdev.impactor.sponge.scoreboard.frames;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.placeholders.PlaceholderSources;
import net.impactdev.impactor.api.scoreboard.frames.types.ConstantFrame;
import net.impactdev.impactor.api.services.text.MessageService;
import net.kyori.adventure.text.Component;

public class SpongeConstantFrame implements ConstantFrame {

    private final Component text;

    public SpongeConstantFrame(SpongeConstantFrameBuilder builder) {
        this.text = builder.text;
    }

    @Override
    public Component getText() {
        return this.text;
    }

    @Override
    public boolean shouldUpdateOnTick() {
        return false;
    }

    public static class SpongeConstantFrameBuilder implements ConstantFrameBuilder {

        private Component text;

        @Override
        public ConstantFrameBuilder raw(String raw, PlaceholderSources sources) {
            MessageService<Component> service = Impactor.getInstance().getRegistry().get(MessageService.class);
            this.text = service.parse(raw, sources);
            return this;
        }

        @Override
        public ConstantFrameBuilder text(Component text) {
            this.text = text;
            return this;
        }

        @Override
        public ConstantFrameBuilder from(ConstantFrame input) {
            this.text = ((SpongeConstantFrame) input).text;
            return this;
        }

        @Override
        public ConstantFrame build() {
            return new SpongeConstantFrame(this);
        }
    }
}
