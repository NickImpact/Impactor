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
import net.impactdev.impactor.api.scoreboard.frames.ScoreboardFrame;
import net.impactdev.impactor.api.scoreboard.frames.types.ConstantFrame;
import net.impactdev.impactor.api.scoreboard.lines.types.ConstantLine;
import net.impactdev.impactor.api.services.text.MessageService;
import net.impactdev.impactor.sponge.scoreboard.lines.types.SpongeConstantLine;
import net.impactdev.impactor.sponge.scoreboard.util.SourceResolvers;
import net.impactdev.impactor.sponge.util.LazyComponent;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import java.util.UUID;

public class SpongeConstantFrame extends AbstractSpongeFrame implements ConstantFrame {

    private LazyComponent supplier;

    public SpongeConstantFrame(SpongeConstantFrameBuilder builder) {
        this.supplier = builder.supplier;
    }

    @Override
    public Component getText() {
        return this.supplier.resolve();
    }

    @Override
    public boolean shouldUpdateOnTick() {
        return false;
    }

    @Override
    public void provideSource(UUID uuid) {
        this.supplier = this.supplier.provide(uuid);
    }

    @Override
    public ScoreboardFrame copy() {
        SpongeConstantFrame frame = new SpongeConstantFrame(new SpongeConstantFrameBuilder());
        frame.supplier = this.supplier;
        return frame;
    }

    public static class SpongeConstantFrameBuilder implements ConstantFrameBuilder {

        private LazyComponent supplier;

        @Override
        public SpongeConstantFrameBuilder raw(String raw, PlaceholderSources sources) {
            MessageService<Component> service = Impactor.getInstance().getRegistry().get(MessageService.class);
            this.supplier = new LazyComponent(fallback -> service.parse(
                    raw,
                    PlaceholderSources.builder()
                            .from(sources)
                            .appendIfAbsent(ServerPlayer.class, SourceResolvers.PLAYER.apply(fallback))
                            .build()
            ));
            return this;
        }

        @Override
        public SpongeConstantFrameBuilder text(Component text) {
            this.supplier = new LazyComponent(fallback -> text);
            return this;
        }

        @Override
        public ConstantFrameBuilder from(ConstantFrame input) {
            this.supplier = ((SpongeConstantFrame) input).supplier;
            return this;
        }

        @Override
        public ConstantFrame build() {
            return new SpongeConstantFrame(this);
        }
    }
}
