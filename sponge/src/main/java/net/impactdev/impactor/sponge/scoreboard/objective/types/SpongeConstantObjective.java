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

package net.impactdev.impactor.sponge.scoreboard.objective.types;

import com.google.common.base.Preconditions;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.placeholders.PlaceholderSources;
import net.impactdev.impactor.api.scoreboard.lines.types.ConstantLine;
import net.impactdev.impactor.api.scoreboard.objective.ScoreboardObjective;
import net.impactdev.impactor.api.scoreboard.objective.types.ConstantObjective;
import net.impactdev.impactor.api.services.text.MessageService;
import net.impactdev.impactor.sponge.scoreboard.objective.AbstractSpongeObjective;
import net.impactdev.impactor.sponge.scoreboard.util.SourceResolvers;
import net.impactdev.impactor.sponge.util.LazyComponent;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

public class SpongeConstantObjective extends AbstractSpongeObjective implements ConstantObjective {

    private LazyComponent supplier;

    private SpongeConstantObjective(SpongeConstantObjectiveBuilder builder) {
        this.supplier = builder.supplier;
    }

    @Override
    public Component getText() {
        return this.supplier.resolve();
    }

    @Override
    public void consumeFocus(ServerPlayer focus) {
        this.supplier = this.supplier.provide(focus.uniqueId());
    }

    @Override
    public ScoreboardObjective copy() {
        SpongeConstantObjective clone = new SpongeConstantObjective(new SpongeConstantObjectiveBuilder());
        clone.supplier = this.supplier;
        return clone;
    }

    public static class SpongeConstantObjectiveBuilder implements ConstantObjectiveBuilder {

        private LazyComponent supplier;

        @Override
        public SpongeConstantObjectiveBuilder raw(String raw, PlaceholderSources sources) {
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
        public SpongeConstantObjectiveBuilder text(Component text) {
            this.supplier = new LazyComponent(fallback -> text);
            return this;
        }

        @Override
        public ConstantObjectiveBuilder from(ConstantObjective input) {
            Preconditions.checkArgument(input instanceof SpongeConstantObjective);
            this.supplier = ((SpongeConstantObjective) input).supplier;
            return this;
        }

        @Override
        public ConstantObjective build() {
            return new SpongeConstantObjective(this);
        }
    }
}
