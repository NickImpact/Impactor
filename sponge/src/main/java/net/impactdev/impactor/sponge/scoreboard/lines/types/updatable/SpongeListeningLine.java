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

package net.impactdev.impactor.sponge.scoreboard.lines.types.updatable;

import net.impactdev.impactor.api.scoreboard.frames.types.ListeningFrame;
import net.impactdev.impactor.api.scoreboard.lines.ScoreboardLine;
import net.impactdev.impactor.api.scoreboard.lines.types.ListeningLine;
import net.impactdev.impactor.sponge.scoreboard.frames.SpongeListeningFrame;
import net.impactdev.impactor.sponge.scoreboard.lines.AbstractSpongeSBLine;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.scoreboard.Scoreboard;

public class SpongeListeningLine extends AbstractSpongeSBLine implements ListeningLine {

    private ListeningFrame<?> frame;

    public SpongeListeningLine(SpongeListeningLineBuilder builder) {
        this.frame = builder.frame;
    }

    @Override
    public Component getText() {
        return this.frame.getText();
    }

    @Override
    public void start() {
        this.frame.initialize(this);
    }

    @Override
    public void update() {
        this.getTeam().setPrefix(this.getText());
    }

    @Override
    public void shutdown() {
        this.frame.shutdown();
    }

    @Override
    public ListeningFrame.EventHandler<?> getEventHandler() {
        return this.frame.getEventHandler();
    }

    @Override
    public void setup(Scoreboard scoreboard, ServerPlayer target) {
        scoreboard.registerTeam(this.getTeam());
        ((SpongeListeningFrame<?>) this.frame).provideSource(target.uniqueId());
        this.getTeam().setPrefix(this.getText());
    }

    @Override
    public ScoreboardLine copy() {
        SpongeListeningLine clone = new SpongeListeningLine(new SpongeListeningLineBuilder());
        clone.frame = (ListeningFrame<?>) this.frame.copy();
        return clone;
    }

    public static class SpongeListeningLineBuilder implements ListeningBuilder {

        private ListeningFrame<?> frame;

        @Override
        public ListeningBuilder content(ListeningFrame<?> frame) {
            this.frame = frame;
            return this;
        }

        @Override
        public ListeningLine build() {
            return new SpongeListeningLine(this);
        }
    }

}
