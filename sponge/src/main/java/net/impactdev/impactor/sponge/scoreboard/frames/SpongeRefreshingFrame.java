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

package net.impactdev.impactor.sponge.scoreboard.frames;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.placeholders.PlaceholderSources;
import net.impactdev.impactor.api.scoreboard.components.Updatable;
import net.impactdev.impactor.api.scoreboard.effects.FrameEffect;
import net.impactdev.impactor.api.scoreboard.frames.types.RefreshingFrame;
import net.impactdev.impactor.api.services.text.MessageService;
import net.kyori.adventure.text.Component;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class SpongeRefreshingFrame implements RefreshingFrame {

    private final String raw;
    private final Queue<FrameEffect> effects;
    private final PlaceholderSources sources;

    public SpongeRefreshingFrame(SpongeRefreshingFrameBuilder builder) {
        this.raw = builder.raw;
        this.effects = new LinkedList<>(Arrays.asList(builder.effects));
        this.sources = builder.sources;
    }

    @Override
    public Component getText() {
        MessageService<Component> service = Impactor.getInstance().getRegistry().get(MessageService.class);
        Component result = service.parse(this.raw, this.sources);
        for(FrameEffect effect : this.effects) {
            result = effect.translate(result);
        }

        return result;
    }

    @Override
    public boolean shouldUpdateOnTick() {
        return true;
    }

    @Override
    public void initialize(Updatable parent) {}

    @Override
    public void shutdown() {}

    public static class SpongeRefreshingFrameBuilder implements RefreshingFrameBuilder {

        private String raw;
        private FrameEffect[] effects = new FrameEffect[0];
        private PlaceholderSources sources;

        @Override
        public RefreshingFrameBuilder raw(String raw) {
            this.raw = raw;
            return this;
        }

        @Override
        public RefreshingFrameBuilder effects(FrameEffect... effects) {
            this.effects = effects;
            return this;
        }

        @Override
        public RefreshingFrameBuilder sources(PlaceholderSources sources) {
            this.sources = sources;
            return this;
        }

        @Override
        public RefreshingFrameBuilder from(RefreshingFrame input) {
            this.raw = ((SpongeRefreshingFrame) input).raw;
            return this;
        }

        @Override
        public RefreshingFrame build() {
            return new SpongeRefreshingFrame(this);
        }

    }
}
